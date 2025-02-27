package it.polimi.ingsw.am43.Network.Socket;

import it.polimi.ingsw.am43.Controller.GameController;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Network.GameServer;
import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.controllerMessages.*;
import it.polimi.ingsw.am43.Network.Messages.toClientMessages.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * A class that handles the communication between the {@link SocketServer} and
 * a {@link SocketClient}.
 */
public class Handler extends Thread {

    /**
     * ANSI color codes for various messages printed from the server
     */
    static String RESET = "\033[0m";
    static String RED = "\033[0;31m"; // Used for ERROR

    /**
     * A {@link Socket} object to communicate with the {@link SocketClient}
     */
    private final Socket client;

    /**
     * The input and output streams responsible for reading and writing
     * messages between the server and the client
     */
    private final ObjectInputStream in_obj;
    private final ObjectOutputStream out_obj;

    /**
     * The id of the {@link SocketClient} assigned to this handler
     */
    private int playerID;

    /**
     * The {@link GameController} that manages the {@link it.polimi.ingsw.am43.Model.Game}.
     */
    private final GameController gameController;

    /**
     * The {@link GameServer} that manages the {@link it.polimi.ingsw.am43.Model.Game}.
     */
    private final GameServer gameServer;

    private static final BlockingDeque<Message> msgQueue = new LinkedBlockingDeque<>();

    private final Thread heartbeatThread;

    /**
     * Constructor for a {@link Handler} object
     *
     * @param client   {@link SocketClient}
     */
    public Handler(Socket client, int playerID, GameController gameController, GameServer gameServer) throws IOException {
        this.client = client;
        this.in_obj = new ObjectInputStream(client.getInputStream());
        this.out_obj = new ObjectOutputStream(client.getOutputStream());
        this.playerID = playerID;
        this.gameController = gameController;
        this.gameServer = gameServer;
        heartbeatThread = new Thread(this::heartbeat);
        heartbeatThread.start();
    }

    /**
     * A function that stops the Handler and closes the
     * socket connection with the {@link SocketClient}
     */
    public void stopHandler() {
        try{
            this.client.close();
            this.interrupt();
            heartbeatThread.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A function that retrieves the {@link SocketClient}'s socket.
     *
     * @return         {@link SocketClient}'s socket
     */
    public Socket getClient() {
        return client;
    }

    /**
     * A function that retrieves the {@link SocketClient}'s id.
     *
     * @return         {@link SocketClient}'s id
     */
    public int getPlayerID() {
        return playerID;
    }

    /**
     * A function that sets the {@link SocketClient}'s id.
     *
     * @param playerID   {@link SocketClient}'s id
     */
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    /**
     * A function that checks if the {@link SocketClient}'s socket is closed.
     *
     * @return         	true if the socket is closed, false otherwise
     */
    public boolean isClosed() {
        return this.client.isClosed();
    }

    /**
     * Runs the handler to handle incoming messages, checks for messages starting with "Msg",
     * and processes them accordingly.
     */
    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                msgQueue.add((Message) in_obj.readObject());
                handleMessage(msgQueue.take());
            } catch (IOException e) {
                if (!gameServer.isStopping()) {
                    System.out.println(RED + "Client " + this.getTitle() + " disconnected.\nSystem shut down." + RESET);
                    stopHandler();
                    gameServer.stop();
                    killConnection();
                }
                break;
            } catch (ClassNotFoundException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Continuously sends a heartbeat message to the connected client at regular intervals.
     */
    private void heartbeat() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                writeMessageToClient(new SCMsgHeartbeat());
                Thread.sleep(200);
            } catch (InterruptedException e) {}
        }
    }

    /**
     * Retrieves the title of the user, if available, or the
     * client's InetAddress with the first character removed.
     *
     * @return          the title of the user or the client's InetAddress
     */
    public String getTitle() {
        return this.client.getInetAddress().toString().substring(1);
    }

    /**
     * Flushes the output stream and resets it to allow for the next write operation.
     *
     * @throws IOException if an I/O error occurs while flushing or resetting the stream.
     */
    private synchronized void completeSend() throws IOException {
        out_obj.flush();
        out_obj.reset();
    }

    /**
     * Handles the incoming message based on its type.
     *
     * @param  message  the message to be handled
     */
    public synchronized void handleMessage(Message message) throws IOException {
        switch (message.getType()) {
            case FIRSTPLAYERJOINS -> firstPlayerJoins((SCMsgFirstPlayerJoins) message);
            case GENERALPLAYERJOINS -> generalPlayerJoins((SCMsgGeneralPlayerJoins) message);
            case DRAW_PERSONAL_OBJECTIVES -> drawPersonalObjective((SCMsgDrawPersonalObjectives) message);
            case PERSONAL_OBJECTIVE_CHOICE -> setPersonalObjective((SCMsgPersonalObjective) message);
            case PLAY -> getAvailableInHand();
            case COUPLECARDS -> getAvailablePlacements((SCMsgCardCouple) message);
            case PLAYTURN -> playTurn((SCMsgPlayTurn) message);
            case FIRST_STILL_JOINING -> firstStillJoining();
            case PLAYER_REJOINS -> playerRejoins((SCMsgPlayerRejoins) message);
            case GENERAL_MSG -> generalChatMessage((publicChatMessageMsg) message);
            case PRIVATE_MSG -> pvtChatMessage((privateChatMessageMsg) message);
        }
    }

    /**
     * Writes a {@link Message} to the client's output stream.
     *
     * @param  msg  the {@link Message} to be written
     */
    private synchronized void writeMessageToClient(Message msg){
        try {
            out_obj.writeObject(msg);
            completeSend();
        } catch (SocketException e) {
            //killConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a {@link firstPlayerMsg} to the first and only the first client indicating that it is the first player.
     * After this message is received, the View asks for username, {@link PawnColor} and number of players.
     * When the reply arrives, the {@link GameController} sets the number of players and adds the first
     * player to the match.
     */
    public void firstPlayer(){
        writeMessageToClient(new firstPlayerMsg());
    }

    /**
     * Sends a {@link nickOrColorAlreadyUsedMsg} to all the players that are NOT the first player,
     * only if they join the server while the first player is still setting up the game.
     * To avoid conflicts in game initialization, clients that receive this message have to wait
     * and periodically check if the first player is still setting up the game.
     */
    public void firstStillJoining(){
        writeMessageToClient(new checkFirstMsg(gameController.getPlayerNames(), gameController.getAvailablePawnColors()));
    }

    /**
     * When a {@link SCMsgFirstPlayerJoins} is received, ask the {@link GameServer} to set the number of players
     * and the {@link GameController} to initialize the players.
     *
     * @param msg the {@link SCMsgFirstPlayerJoins} received, containing all the necessary information
     */
    private void firstPlayerJoins(SCMsgFirstPlayerJoins msg){
        gameServer.setNumberOfPlayers(msg.getNumPlayers());
        gameController.initializePlayers(msg.getNumPlayers(), msg.getUsername(), toColor(msg.getColor()));
    }

    /**
     * Sends a {@link generalPlayerMsg} to all the players that are NOT the first player.
     * The message contains all the available colors and all the players of players who have already
     * joined the game. The View, when receiving this message, asks for username and color.
     */
    public void generalPlayer(){
        writeMessageToClient(new generalPlayerMsg(gameController.getAvailablePawnColors(), gameController.getPlayerNames()));
    }

    /**
     * When a {@link SCMsgGeneralPlayerJoins} is received, check first if all the information
     * contained in the message are valid, and then add the player to the game.
     *
     * @param msg the {@link SCMsgGeneralPlayerJoins} received, containing all the necessary information
     */
    private void generalPlayerJoins(SCMsgGeneralPlayerJoins msg){
        // Flags used for the checks
        boolean canAdd = true;
        boolean found = false;
        // If a player has inserted a nickname already chosen by somebody else, do not add him/her
        if(Arrays.asList(gameController.getPlayerNames()).contains(msg.getUsername())){
            canAdd = false;
        }
        // If a player has inserted a color already chosen by somebody else, do not add him/her
        for (int n : gameController.getAvailablePawnColors()) {
            if(canAdd && n == msg.getColor()){
                found = true;
                break;
            }
        }
        if(!found){
            canAdd = false;
        }
        // If no errors were found, add the player to the game
        if(canAdd){
            gameController.addPlayer(msg.getUsername(), toColor(msg.getColor()));
            gameServer.tryStartingMatch();
        }else{
            writeMessageToClient(new nickOrColorAlreadyUsedMsg(gameController.getPlayerNames(), gameController.getAvailablePawnColors()));
        }
    }

    /**
     * Sends a {@link rejoinPlayerMsg} to all the players that are rejoining a loaded game.
     * The message contains all the usernames previously chosen for that game.
     * The View, when receiving this message, will display them and ask the user to pick
     * the username that he/her chose during that game.
     */
    public void rejoin(){
        writeMessageToClient(new rejoinPlayerMsg(gameController.getPlayerNames()));
    }

    /**
     * When a {@link SCMsgPlayerRejoins} is received, check first if all the information
     * contained in the message are valid, and then re-add the player to the game.
     * This method also sends to the player who has rejoined all the useful information
     * needed to proceed with the game, inside a {@link requiredDataMsg}.
     *
     * @param msg the {@link SCMsgPlayerRejoins} received, containing all the necessary information
     */
    private void playerRejoins(SCMsgPlayerRejoins msg){
        // If the player has chosen a nickname not chosen by somebody else
        if(!gameController.getRejoinedPlayers().contains(gameController.getPlayerNames()[msg.getPick()])){
            gameController.rejoinPlayer(msg.getPick());
            setPlayerID(msg.getPick());
            writeMessageToClient(new requiredDataMsg(gameController.getPlayers()[msg.getPick()].getInHand(),
                    gameController.getPlayers()[msg.getPick()].getStartingCard(),
                    gameController.getOnGround(),
                    gameController.getCommonObjectives(),
                    gameController.getPlayers()[msg.getPick()].getPersonalObjective(),
                    gameController.getScores(),
                    gameController.getAllPlacements(),
                    gameController.getAllDeployed(),
                    gameController.getUsersColors(),
                    gameController.getPlayerNames(),
                    gameController.getNumPlayers(),
                    gameController.getFirstPlayer()));
            gameServer.tryRestartingMatch();
        // If the player has chosen a nickname already chosen by somebody else, do not add him/her
        } else {
            writeMessageToClient(new cannotRejoinMsg(gameController.getRejoinedIndexes(), gameController.getPlayerNames()));
        }
    }

    /**
     * Retrieves the initial situation for the player and sends it to the client in a {@link initialSituation}.
     */
    public void initialSituation(){
        writeMessageToClient(gameController.getInitialSituation(playerID));
    }

    /**
     * Retrieves the game information from the game controller and sends it to the client in a {@link gameInfoMsg}.
     * If the player ID in the game information message is -1, it also sends an {@link endGameMsg} to the client.
     */
    public void gameInfo(gameInfoMsg msg){
        writeMessageToClient(msg);
    }
    public void endGame(endGameMsg msg){
        writeMessageToClient(msg);
    }

    /**
     * Retrieves the initial game information from the game controller and sends it to the client
     * in a {@link initialGameInfoMsg}.
     */
    public void initialGameInfo(){
        writeMessageToClient(gameController.getInitialGameInfo());
    }

    private void drawPersonalObjective(SCMsgDrawPersonalObjectives msg) {
        gameController.placeStartingCardSide(msg.getSide(), playerID);
        personalObjectivesMsg answer = new personalObjectivesMsg(gameController.drawPersonalObjectives());
        writeMessageToClient(answer);
    }

    //TODO: possibile miglioramento, non passare la carta obiettivo intera ma magari solo il suo indice
    private void setPersonalObjective(SCMsgPersonalObjective message){
        gameController.setPersonalObjective(playerID, message.getObj());
        gameServer.incrementChosenObjectivesCounter();
    }

    private void getAvailableInHand(){
        writeMessageToClient(new placeableCardsMsg(gameController.getInHandAvailableSides(playerID)));
    }

    private void getAvailablePlacements(SCMsgCardCouple msg){
        writeMessageToClient(new availablePlacementsMsg(gameController.getAvailablePlacements(playerID, msg.getDeployedIndex()), gameController.isResEmpty(), gameController.isGoldEmpty()));
    }

    //TODO: il deployed index non serve che ritorni da qui, la TUI lo ha già, togliere da newInHand
    private void playTurn(SCMsgPlayTurn msg) {
        PlayableCard drawn = gameController.playTurn(playerID, msg.getHandIndex(), msg.getDeployedIndex(), msg.getCorner(), msg.getDrawn());
        writeMessageToClient(new newInHandMsg(drawn));
        gameServer.sendGameInfo();
    }

    /**
     * Converts an {@link Integer} color value to the corresponding {@link PawnColor} enum value.
     *
     * @param  color  the {@link Integer} color value to convert
     * @return        the corresponding {@link PawnColor} enum value
     */
    private PawnColor toColor(int color){
        return PawnColor.values()[color];
    }

    /**
     * When a {@link publicChatMessageMsg} is received, sends the content of the message,
     * together with the sender to the {@link GameServer} which will forward it
     * to all other players.
     *
     * @param msg the {@link publicChatMessageMsg} containing all the necessary information
     */
    private void generalChatMessage(publicChatMessageMsg msg){
        gameServer.sendPublicChat(msg.getSender(), playerID, msg.getMessage());
    }

    /**
     * Sends a chat message to the View.
     * This method is run by the receiver handler.
     *
     * @param  sender    the sender of the message
     * @param  msg       the content of the message
     */
    public void publicChatMessage(String sender, String msg){
        writeMessageToClient(new publicChatMessageMsg(sender, msg));
    }

    /**
     * When a {@link privateChatMessageMsg} is received, sends the content of the message,
     * together with sender and receiver to the {@link GameServer} which will forward it
     * to the designed receiver.
     *
     * @param msg the {@link privateChatMessageMsg} containing all the necessary information
     */
    public void pvtChatMessage(privateChatMessageMsg msg){
        int receiverID = gameController.getPlayerID(msg.getReceiver());
        gameServer.sendPrivateChat(msg.getSender(), msg.getReceiver(), receiverID, msg.getMessage());
    }

    /**
     * Sends a private chat message to the specified receiver.
     * This method is actually ran already by the receiver handler, but for
     * clarity of the chat display, the receiver is forwarded to the View to print it.
     *
     * @param  sender    the sender of the message
     * @param  receiver  the receiver of the message
     * @param  msg       the content of the message
     */
    public void privateChatMessage(String sender, String receiver, String msg) {
        writeMessageToClient(new privateChatMessageMsg(sender, receiver, msg));
    }

    private void killConnection() {
        System.out.println(RED + "Press enter to exit..." + RESET);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
        System.exit(0);
    }
}