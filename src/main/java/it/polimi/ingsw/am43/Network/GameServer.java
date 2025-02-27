package it.polimi.ingsw.am43.Network;

import it.polimi.ingsw.am43.Controller.GameController;
import it.polimi.ingsw.am43.Network.Messages.toClientMessages.endGameMsg;
import it.polimi.ingsw.am43.Network.Messages.toClientMessages.gameInfoMsg;
import it.polimi.ingsw.am43.Network.RMI.RMIServer;
import it.polimi.ingsw.am43.Network.Messages.toClientMessages.privateChatMessageMsg;
import it.polimi.ingsw.am43.Network.Messages.toClientMessages.publicChatMessageMsg;
import it.polimi.ingsw.am43.Network.Socket.SocketServer;

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * Class that unifies the RMI and Socket servers and runs both.
 */
public class GameServer {

    /**
     * ANSI color codes for various messages printed from the server
     */
    static String RESET = "\033[0m";
    static String RED = "\033[0;31m"; // Used for ERROR
    static String GREEN = "\033[0;32m"; //Used for GAME messages
    static String YELLOW = "\033[0;33m"; // Used for SERVER messages

    /**
     * Address of the servers
     */
    private final String addr;

    /**
     * Port of the servers
     */
    private final int port;

    /**
     * {@link SocketServer} for Socket communication.
     */
    private SocketServer socketServer;

    /**
     * List of RMI clients connected.
     */
    private final List<Integer> rmiClients = new ArrayList<>();

    /**
     * Boolean flag that indicates if the RMI server is running.
     */
    private boolean rmiRunning = false;

    /**
     * Timer that send a heartbeat periodically.
     */
    private final Timer heartbeatTimer = new Timer();

    /**
     * {@link RMIServer} for RMI communication.
     */
    private RMIServer rmiServer;

    /**
     * The {@link GameController} that manages the {@link it.polimi.ingsw.am43.Model.Game}.
     */
    private final GameController gameController;

    /**
     * A counter that keeps track of the number of clients connected (both connections).
     */
    private int clientsCounter = 0;

    /**
     * A counter that keeps track of the number of objectives chosen by the players.
     * It is used to understand when to start the game.
     */
    private int chosenObjectivesCounter = 0;

    /**
     * A counter that keeps track of the number of players playing the game (NOT connected clients).
     */
    private int numberOfPlayers;

    /**
     * A map that keeps track of the connected clients (only Socket connection).
     */
    private final Map<Integer, Socket> socketClients = new HashMap<>();

    /**
     * A flag used to stop accepting new clients when the game has started.
     */
    private boolean acceptingNewClients = true;

    /**
     * A flag used by {@link it.polimi.ingsw.am43.Network.RMI.RMIClient}s to know if anything has changed.
     */
    private Boolean hasNewGameInfo;

    /**
     * A flag that indicates if the server is stopping.
     */
    private boolean isStopping = false;

    /**
     * A hashmap used for RMI clients to notice changes in game.
     */
    private final HashMap<Integer, Boolean> gameInfoCounter = new HashMap<>();

    /**
     * A message used when the match has finished.
     */
    private endGameMsg endGameMsg = null;

    /**
     * A hashmap that keeps track of the messages sent to RMI clients.
     */
    private final HashMap<Integer, Stack<Message>> messagesToRMICounter = new HashMap<>();

    /**
     * A flag that indicates if the RMI server has to close and kick the clients.
     */
    public boolean rmiHasToClose = false;

    /**
     * Constructor that starts both {@link SocketServer} and {@link RMIServer}.
     * This is used when creating a new game.
     *
     * @param a  address of the servers
     * @param p  port of the servers
     */
    public GameServer(String a, int p)  {
        this.gameController = new GameController();
        addr = a;
        port = p;
        try {
            socketServer = new SocketServer(addr, port, gameController, this);
        } catch (IOException e) {
            System.err.println(RED + "[SERVER] Error while starting servers: " + e.getMessage());
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, 10000);
        }
        System.out.println(GREEN + "[GAME] Created game with ID: " + gameController.getGameID() + RESET);
    }

    /**
     * Constructor that starts both {@link SocketServer} and {@link RMIServer}.
     * This is used when loading an existing game.
     *
     * @param a  address of the servers
     * @param p  port of the servers
     * @param gameID  ID of the game
     */
    public GameServer(String a, int p, String gameID) throws IOException, ClassNotFoundException {
        this.gameController = new GameController();
        addr = a;
        port = p;
        gameController.loadGame(gameID);
        this.numberOfPlayers = gameController.getNumPlayers();
        try {
            socketServer = new SocketServer(addr, port, gameController, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(GREEN + "[GAME] Loaded game with ID: " + gameID + RESET);
    }

    /**
     * Method to run both {@link SocketServer} and {@link RMIServer}.
     */
    public void run() {
        System.out.println(YELLOW + "[SERVER] Servers starting on " + addr + ":" + port + "/" + (port+1) + RESET);
        startRMIServer();
        // Starts a thread to constantly update the connected clients counter
        new Thread(this::getConnectedClients).start();
        // Starts the heartbeating function
        startHeartbeatChecking();
        System.out.println(YELLOW + "[SERVER] Starting socket server..." + RESET);
        socketServer.start();
    }

    /**
     * A method that stops the servers by stopping the RMI server, stopping the heartbeat checking,
     * and interrupting the socket server.
     */
    public void stop() {
        isStopping = true;
        System.out.println(YELLOW + "[SERVER] Stopping the servers..." + RESET);
        stopRMIServer();
        stopHeartbeatChecking();
        socketServer.interrupt();
        kickAll();
        System.out.println(YELLOW + "[SERVER] Servers successfully stopped!" + RESET);
        System.out.println(RED + "Press enter to exit..." + RESET);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
        System.exit(0);
    }

    /**
     * A method that starts the match by invoking the preliminary phase
     * of the game and sending the initial situations to all connected clients.
     */
    private void startMatch(){
        gameController.startPreliminaryPhase();
        socketServer.sendInitialSituations();
    }

    /**
     * A method that sends the game information to all players.
     */
    public void sendGameInfo() {
        gameInfoMsg msg = gameController.getGameInfo();
        hasNewGameInfo = true;
        gameInfoCounter.replaceAll((k, v) -> false);
        System.out.println(GREEN + "[GAME] Turn completed!\n[GAME] Sending game info for next turn..." + RESET);
        socketServer.sendGameInfo(msg);

        if (msg.getLastOnGroundIndex() == -1 && endGameMsg == null) {
            endGameMsg = new endGameMsg(gameController.endGame());
            socketServer.sendEndGameInfo(endGameMsg);
        }
        try {
            gameController.saveGame();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * A method that sends the initial game information to all players.
     */
    public void sendInitialGameInfo(){
        System.out.println(GREEN + "[GAME] Sending initial game info to all players!" + RESET);
        socketServer.sendInitialGameInfo();
        for (int i = 0; i < numberOfPlayers; i++) {
            gameInfoCounter.put(i, false);
        }
    }

    /**
     * Starts the RMI server and binds it to the registry.
     */
    public void startRMIServer() {
        try {
            System.out.println(YELLOW + "[SERVER] Starting RMI server..." + RESET);
            rmiServer = new RMIServer(this, gameController);
            Registry registry = LocateRegistry.createRegistry(port + 1);
            registry.rebind("RMIServer", rmiServer);
            setRmiRunning(true);
            System.out.println(YELLOW + "[SERVER] RMI Server started." + RESET);
        } catch (RemoteException e) {
            System.err.println(RED + "[ERROR] RMI Server exception: " + e);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, 10000);
        }
    }

    /**
     * Stops the RMI server by unbinding it from the registry and setting the RMI server running flag to false.
     */
    public void stopRMIServer() {
        try {
            System.out.println(YELLOW + "[SERVER] Stopping RMI server..." + RESET);
            Registry registry = LocateRegistry.getRegistry();
            registry.unbind("RMIServer");
            setRmiRunning(false);
            System.out.println(YELLOW + "[SERVER] RMI Server stopped." + RESET);
        } catch (Exception e) {
            System.err.println(RED + "[ERROR] RMI Server exception: " + e);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, 10000);
        }
    }

    /**
     * Adds a new {@link it.polimi.ingsw.am43.Network.RMI.RMIClient} to the list of RMI clients if the {@link RMIServer} is running.
     *
     * @param  hashCode  the hashCode of the client to be added
     * @return           the index of the newly added client in the list of RMI clients,
     *                   or -1 if the RMI server is not running
     */
    public int addRMIClient(int hashCode) {
        if (isRmiRunning()) {
            rmiClients.add(hashCode);
            clientsCounter++;
            System.out.println(YELLOW + "[SERVER] RMI Client added: " + hashCode + RESET);
            return clientsCounter-1;
        } else {
            System.err.println("[ERROR] Unable to add client. RMI server is down.");
        }
        return -1;
    }

    /**
     * Adds a new {@link it.polimi.ingsw.am43.Network.Socket.SocketClient} to the list of socket clients.
     *
     * @param  client  the socket client to be added
     * @return         the index of the newly added client in the list of socket clients
     */
    public int addSocketClient(Socket client){
        socketClients.put(clientsCounter, client);
        clientsCounter++;
        return (clientsCounter-1);
    }

    /**
     * A method that checks if enough players are connected and starts the match if there are.
     */
    public void tryStartingMatch(){
        if (gameController.getNumPlayers()==numberOfPlayers) {
            System.out.println(GREEN + "[GAME] All players connected, starting match!" + RESET);
            this.acceptingNewClients = false;
            new Thread(this::startMatch).start();
        }
    }

    /**
     * A method that checks if all players have rejoined and restarts the match if they have.
     */
    public void tryRestartingMatch(){
        if (gameController.getRejoinedPlayers().size()==numberOfPlayers) {
            System.out.println(GREEN + "[GAME] All players connected, restarting match!" + RESET);
            this.acceptingNewClients = false;
            for (int i = 0; i < numberOfPlayers; i++) {
                gameInfoCounter.put(i, false);
            }
            new Thread(this::sendGameInfo).start();
        }
    }

    /**
     *  A method that increments the chosen objectives counter
     *  and sends initial game information if the counter reaches the number of players.
     */
    public void incrementChosenObjectivesCounter(){
        this.chosenObjectivesCounter++;
        if (chosenObjectivesCounter==numberOfPlayers){
            sendInitialGameInfo();
        }
    }

    /**
     * Returns the current value of the clientsCounter variable
     *
     * @return the current value of the clientsCounter variable
     */
    public int getClientsCounter() {
        return clientsCounter;
    }

    /**
     * Returns the number of players in the game.
     *
     * @return the number of players
     */
    public int getNumberOfPlayers() {return numberOfPlayers;}

    /**
     * Sets the number of players in the game.
     *
     * @param  numberOfPlayers  the new number of players
     */
    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    /**
     * Returns the current value of the acceptingNewClients variable
     *
     * @return the value of the flag acceptingNewClients
     */
    public boolean isAcceptingNewClients() {
        return acceptingNewClients;
    }

    /**
     * A method that tells if the server is stopping or not.
     *
     * @return true if the server is stopping, false otherwise
     */
    public boolean isStopping() {
        return isStopping;
    }

    /**
     * Sets the value of the acceptingNewClients variable
     *
     * @param  value  the new value of the acceptingNewClients variable
     */
    public void setAccepting(boolean value) {
        this.acceptingNewClients = value;
    }

    /**
     * Returns the current value of the rmiRunning flag, indicating whether the RMI server is running or not.
     *
     * @return true if the RMI server is running, false otherwise
     */
    public synchronized boolean isRmiRunning() {
        return rmiRunning;
    }

    /**
     * Sets the value of the rmiRunning flag
     *
     * @param  serverRunning  the new value of the rmiRunning flag
     */
    public synchronized void setRmiRunning(boolean serverRunning) {
        this.rmiRunning = serverRunning;
    }

    /**
     * Returns the current value of the chosenObjectivesCounter variable
     *
     * @return the current value of the chosenObjectivesCounter variable
     */
    public int getChosenObjectivesCounter() {
        return chosenObjectivesCounter;
    }

    /**
     * A method that checks if there are new game info for the player.
     *
     * @param playerId the ID of the player
     * @return true if there are new info, false otherwise
     */
    public boolean hasNewInfo(int playerId) {
        if (hasNewGameInfo != null) {
            boolean hasNewInfoForYou = hasNewGameInfo && !gameInfoCounter.get(playerId);
            if (hasNewInfoForYou) {
                gameInfoCounter.put(playerId, true);
            }
            if (!gameInfoCounter.containsValue(false)) {
                hasNewGameInfo = false;
            }
            return hasNewInfoForYou;
        } else {
            return false;
        }
    }

    /**
     * Retrieves the number of connected clients (with both RMI and socket) and
     * continuously updates the count, every 5 seconds.
     */
    public void getConnectedClients() {
        int lastNumClients = -1;

        while (!rmiClients.isEmpty() || !socketClients.isEmpty()) {
            int totalClients = rmiClients.size() + socketClients.size();
            // If the number of clients has changed, print and update
            if (totalClients != lastNumClients) {
                System.out.println(YELLOW + "[SERVER] Number of connected clients: " + totalClients + RESET);
                lastNumClients = totalClients;
            }
            try {
                Thread.sleep(5000); // Check every 5 seconds
            } catch (InterruptedException ignored) {
                break;
            }
        }
    }

    /**
     * A method that starts the heartbeat checking.
     */
    private void startHeartbeatChecking() {
        heartbeatTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Perform server status check here
                if (!isRmiRunning()) {
                    System.err.println("[ERROR] RMI Server is down.");
                    stop();
                }
                try {
                    rmiServer.checkClientHeartbeats();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, 5000); // Check server status every 5 seconds
    }

    /**
     * A method that stops the heartbeat checking.
     */
    private void stopHeartbeatChecking() {
        heartbeatTimer.cancel();
    }

    /**
     * Sends a public chat message from the sender to all connected clients.
     *
     * @param  sender   the sender of the message
     * @param  senderID the ID of the sender
     * @param  msg      the content of the message
     */
    public void sendPublicChat(String sender, int senderID, String msg) {
        // Socket
        socketServer.sendPublicChat(sender, senderID, msg);
        // RMI
        for (int i = 0; i < numberOfPlayers; i++) {
            if (i == senderID) continue;
            Stack<Message> stack = messagesToRMICounter.get(i);
            if (stack == null) {
                stack = new Stack<>();
            }
            stack.push(new publicChatMessageMsg(sender, msg));
            messagesToRMICounter.put(i, stack);
        }
    }

    /**
     * Sends a private chat message from the sender to the receiver.
     *
     * @param  sender     the sender of the message
     * @param  receiver   the receiver of the message
     * @param  receiverID the ID of the receiver
     * @param  msg        the content of the message
     */
    public void sendPrivateChat(String sender, String receiver, int receiverID, String msg) {
        // Socket
        socketServer.sendPrivateChat(sender, receiver, receiverID, msg);
        // RMI
        Stack<Message> stack = messagesToRMICounter.get(receiverID);
        if (stack == null) {
            stack = new Stack<>();
        }
        stack.push(new privateChatMessageMsg(sender, receiver, msg));
        messagesToRMICounter.put(receiverID, stack);
    }

    /**
     * A method used to retrieve a message from a specific client.
     *
     * @param receiverID the ID of the client that receives the message
     * @return the message
     */
    public Message getMessage(int receiverID) {
        if (messagesToRMICounter.get(receiverID) != null) {
            Stack<Message> stack = messagesToRMICounter.get(receiverID);
            if (!stack.isEmpty()) {
                return stack.pop();
            }
        }
        return null;
    }

    /**
     * A method used when the servers are stopping to
     * kick all the clients connected to the server.
     */
    public void kickAll() {
        try {
            socketServer.closeClients();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        rmiHasToClose = true;
    }

    /**
     * A method used to retrieve the engGameMsg variable.
     *
     * @return the endGameMsg
     */
    public Message getEndGameInfo() {
        return endGameMsg;
    }
}