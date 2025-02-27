package it.polimi.ingsw.am43.Network.RMI;

import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Network.*;
import it.polimi.ingsw.am43.Network.Messages.controllerMessages.*;
import it.polimi.ingsw.am43.Network.Messages.toClientMessages.*;

import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;

public class RMIClient implements ClientInterface {
    static String RED = "\033[0;31m"; // Used for ERROR
    static String GREEN = "\033[0;32m"; //Used for GAME messages

    private static final long HEARTBEAT_INTERVAL = 2500; // Heartbeat interval in milliseconds

    private int playerID;

    private final RMIServerInterface server;

    private final Stack<Message> pendingMessagesToView = new Stack<>();

    private it.polimi.ingsw.am43.Network.Messages.toClientMessages.initialSituation initialSituation;

    private ObjectiveCard personalObjective;

    private initialGameInfoMsg initialGameInfo;

    private boolean hasRejoined = false;

    public RMIClient(String address, int port) throws IOException, NotBoundException {
        try {
            LocateRegistry.getRegistry(address, port).lookup("RMIServer");
        } catch (ConnectException e) {
            System.err.println(RED + "Server appears to be down. Try again later.");
            System.exit(0);
        }
        server = (RMIServerInterface) LocateRegistry.getRegistry(address, port).lookup("RMIServer");
        run();
    }

    /**
     * Main method of the Client. Once joined, starts a new {@link Thread} that checks for new inputs
     * every time interval, equal to 5 seconds.
     * Game setup:
     * First, if the player has rejoined an existing game, gets the game information.
     * Else it receives the {@link initialSituation} if not already received.
     * If the {@link personalObjectivesMsg} has not been chosen, receives the request to assign one
     * Lastly, if the {@link initialGameInfoMsg} has not been received, it saves the message.
     * Game operation:
     * It tries to fetch new game info from the {@link RMIServer}.
     * It listens for new chat messages from the {@link GameServer}.
     */
    private void run() {
        try {
            join();
            new Thread(() -> {
                while (!Thread.interrupted()) {
                    try {
                        if (hasRejoined) {
                            try {
                                handleGameInfo(server.getGameInfo(playerID));
                            } catch (Exception ignored) {}
                        }
                        if (this.initialSituation == null && !hasRejoined) {
                            try {
                                initialSituation msg = (initialSituation) server.getInitialSituation(playerID);
                                pendingMessagesToView.add(msg);
                                this.initialSituation = msg;
                            } catch (Exception ignored) {}
                        }
                        if (this.personalObjective == null) {
                            try {
                                this.personalObjective = server.getPersonalObjective(playerID);
                            } catch (Exception ignored) {
                            }
                        }
                        if (this.initialGameInfo == null) {
                            try {
                                initialGameInfoMsg msg = (initialGameInfoMsg) server.getInitialGameInfo();
                                pendingMessagesToView.add(msg);
                                this.initialGameInfo = msg;
                            } catch (Exception ignored) {
                            }
                        }
                        if (initialSituation != null) {
                            try {
                                handleGameInfo(server.getGameInfo(playerID));
                            } catch (Exception ignored) {}
                        }
                        try {
                            Message msg = server.receiveChat(playerID);
                            pendingMessagesToView.add(msg);
                        } catch (Exception ignored) {}
                        Thread.sleep(HEARTBEAT_INTERVAL * 2);
                    } catch (InterruptedException ignored) {}
                }
            }).start();
        } catch (Exception e) {
            System.err.println(RED + "Client exception: " + e);
        }
    }

    /**
     * Checks if the {@link GameServer} is accepting new clients.
     * Starts sending heartbeats and registers as a new player.
     * Then, if the game is new, sends a {@link firstPlayerMsg} if it's the first player, or a
     * {@link generalPlayerMsg} if it's not.
     * If the game is loaded from an existing one, sends a {@link rejoinPlayerMsg}.
     * @throws IOException on a {@link IOException} from client registration.
     */
    private void join() throws IOException {
        if (server.isAcceptingNewClients()) {
            playerID = server.registerClient(this);
            startHeartbeatChecker();
            if (!server.getLoaded()) {
                if (server.getClientsCounter() == 1) {
                    pendingMessagesToView.add(new firstPlayerMsg());
                } else {
                    pendingMessagesToView.add(new generalPlayerMsg(
                            server.getAvailablePawnColors(),
                            server.getPlayerNames()
                    ));
                }
            } else {
                pendingMessagesToView.add(new rejoinPlayerMsg(server.getPlayerNames()));
            }
        }
    }

    /**
     * Constantly checks for new messages to display to the view.
     * @return a {@link Message} to display.
     */
    @Override
    public Message readNewMessage()  {
        while (true) {
            if (!pendingMessagesToView.isEmpty()) {
                return pendingMessagesToView.pop();
            }
        }
    }

    /**
     * Handles new messages coming from the view.
     * @param msg is the message received.
     */
    @Override
    public void writeNewMessage(Message msg) {
        try {
            switch (msg.getType()) {
                case FIRSTPLAYERJOINS -> firstPlayerJoins((SCMsgFirstPlayerJoins) msg);
                case GENERALPLAYERJOINS -> generalPlayerJoins((SCMsgGeneralPlayerJoins) msg);
                case DRAW_PERSONAL_OBJECTIVES -> drawPersonalObjective((SCMsgDrawPersonalObjectives) msg);
                case PERSONAL_OBJECTIVE_CHOICE -> setPersonalObjective((SCMsgPersonalObjective) msg);
                case PLAY -> getAvailableInHand();
                case COUPLECARDS -> getAvailablePlacements((SCMsgCardCouple) msg);
                case PLAYTURN -> playTurn((SCMsgPlayTurn) msg);
                case FIRST_STILL_JOINING -> firstStillJoining();
                case PLAYER_REJOINS -> playerRejoins((SCMsgPlayerRejoins) msg);
                case GENERAL_MSG -> sendGeneralChatMessage((publicChatMessageMsg) msg);
                case PRIVATE_MSG -> sendPrivateChatMessage((privateChatMessageMsg) msg);
            }
        } catch (Exception e) {
            System.err.println(RED + "Client exception: " + e);
        }
    }

    /**
     * Handles the response from joining as the first player of a new game.
     * @param msg contains the information from the player setup.
     * @throws IOException on an exception from server.
     */
    private void firstPlayerJoins(SCMsgFirstPlayerJoins msg) throws IOException {
        server.setNumberOfPlayers(msg.getNumPlayers());
        server.initializePlayers(msg.getNumPlayers(), msg.getUsername(), PawnColor.values()[msg.getColor()]);
    }

    /**
     * Handles the response from joining as the non-first player of a new game.
     * When a {@link SCMsgGeneralPlayerJoins} is received, check first if all the information
     * contained in the message are valid, and then add the player to the game.
     * @param msg contains the information from the player setup.
     * @throws IOException on an exception from server.
     */
    private void generalPlayerJoins(SCMsgGeneralPlayerJoins msg) throws IOException {
        boolean canAdd = true;
        boolean found = false;
        if (Arrays.asList(server.getPlayerNames()).contains(msg.getUsername())) {
            canAdd = false;
        }
        for (int n : server.getAvailablePawnColors()) {
            if(canAdd && n == msg.getColor()){
                found = true;
                break;
            }
        }
        if(!found){
            canAdd = false;
        }
        if(canAdd){
            server.addPlayer(msg.getUsername(), PawnColor.values()[msg.getColor()]);
            server.tryStartingMatch();
        }else{
            System.out.println(GREEN + new nickOrColorAlreadyUsedMsg(server.getPlayerNames(), server.getAvailablePawnColors()));
        }
    }

    /**
     * Handles the request of the choice of a personal objective for this player.
     * @param msg contains the card chosen.
     * @throws RemoteException on an exception from server.
     */
    private void drawPersonalObjective(SCMsgDrawPersonalObjectives msg) throws RemoteException {
        server.placeStartingCardSide(msg.getSide(), playerID);
        personalObjectivesMsg answer = new personalObjectivesMsg(server.drawPersonalObjectives());
        pendingMessagesToView.add(answer);
    }

    /**
     * Handles the request of the choice of a personal objective for this player.
     * @param msg contains the card chosen.
     * @throws RemoteException on an exception from server.
     */
    private void setPersonalObjective(SCMsgPersonalObjective msg) throws RemoteException {
        server.setPersonalObjective(playerID, msg.getObj());
    }

    /**
     * Handles the request to view the cards in hand for this player.
     * @throws RemoteException on an exception from server.
     */
    private void getAvailableInHand() throws RemoteException {
        pendingMessagesToView.add(new placeableCardsMsg(server.getInHandAvailableSides(playerID)));
    }

    /**
     * Handles the request to view the available placements for a chosen card by this player.
     * @param msg contains the card chosen.
     * @throws RemoteException on an exception from server.
     */
    private void getAvailablePlacements(SCMsgCardCouple msg) throws RemoteException {
        pendingMessagesToView.add(server.getAvailablePlacements(playerID, msg.getDeployedIndex()));
    }

    /**
     * Handles the deployment of a card and drawing of a new one.
     * @param msg contains the card deployed, on which angle of which card, and the newly drawn card.
     * @throws RemoteException on an exception from server.
     */
    private void playTurn(SCMsgPlayTurn msg) throws RemoteException {
        PlayableCard drawn = server.playTurn(playerID, msg.getHandIndex(), msg.getDeployedIndex(), msg.getCorner(), msg.getDrawn());
        pendingMessagesToView.add(new newInHandMsg(drawn));
        server.sendGameInfo();
    }

    /**
     * Handles the warning message by the server that the first player is still setting the game up.
     * @throws RemoteException on an exception from server.
     */
    private void firstStillJoining() throws RemoteException {
        pendingMessagesToView.add(new checkFirstMsg(server.getPlayerNames(), server.getAvailablePawnColors()));
    }

    /**
     * Handles the rejoin of a previous game, checks that the username is not already taken, and sends all the info.
     * @param msg contains the chosen username.
     * @throws RemoteException on an exception from server.
     */
    private void playerRejoins(SCMsgPlayerRejoins msg) throws IOException {
        final String uName = server.isAlreadyTaken(msg.getPick());
        if (uName != null) {
            server.rejoinPlayer(msg.getPick());
            hasRejoined = true;
            pendingMessagesToView.add(server.getRequiredData(msg.getPick()));
            server.tryRestartingMatch();
        } else {
            pendingMessagesToView.add(new cannotRejoinMsg(server.getRejoinedIndexes(), server.getPlayerNames()));
        }
    }

    /**
     * Handles the sending of a public chat message.
     * @param msg contains the sender's info and the chat message.
     * @throws RemoteException on an exception from server.
     */
    private void sendGeneralChatMessage(publicChatMessageMsg msg) throws RemoteException {
        server.sendPublicChat(msg.getSender(), playerID, msg.getMessage());
    }

    /**
     * Handles the sending of a private chat message.
     * @param msg contains the sender's info, the receiver's info and the chat message.
     * @throws RemoteException on an exception from server.
     */
    private void sendPrivateChatMessage(privateChatMessageMsg msg) throws RemoteException {
        server.sendPrivateChat(msg);
    }

    /**
     * Handles exceptions in connection. It starts a 10-second {@link Timer} after which the execution is stopped.
     */
    private static void killConnection() {
        System.err.println(RED + "Connection to server lost!\nGame window will close in 10 seconds.");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.exit(1);
            }
        }, 10000);
    }

    /**
     * Commences the check of heartbeats, both client-to-server and server-to-client, every 2 seconds
     * On any exception, the connection is closed.
     */
    private void startHeartbeatChecker() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    server.sendHeartbeat();
                    server.heartbeat(playerID);
                } catch (Exception e) {
                    timer.cancel();
                    killConnection();
                }
            }
        }, 0, HEARTBEAT_INTERVAL); // Check heartbeat every 2 seconds
    }

    /**
     * Handles the reception of {@link gameInfoMsg}. If a {@link endGameMsg} is sent together with it, the end game
     * sequence is made to start.
     * @param output contains the request responses from the {@link RMIServer}.
     */
    private void handleGameInfo(Message[] output) {
        pendingMessagesToView.add(output[0]);
        if (output.length == 2) {
            pendingMessagesToView.add(output[1]);
        }
    }
}