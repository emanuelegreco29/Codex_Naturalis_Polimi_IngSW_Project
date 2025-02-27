package it.polimi.ingsw.am43.View.GUI;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Model.Chat;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Model.Player;
import it.polimi.ingsw.am43.Network.ClientInterface;
import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.toClientMessages.*;
import it.polimi.ingsw.am43.Network.RMI.RMIClient;
import it.polimi.ingsw.am43.Network.Messages.MessageType;
import it.polimi.ingsw.am43.Network.Messages.controllerMessages.SCMsgCardCouple;
import it.polimi.ingsw.am43.Network.Messages.controllerMessages.SCMsgPlay;
import it.polimi.ingsw.am43.Network.Messages.controllerMessages.SCMsgPlayTurn;
import it.polimi.ingsw.am43.Network.Socket.SocketClient;
import it.polimi.ingsw.am43.View.View;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.rmi.NotBoundException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

public class GUI extends Application implements View {
    /**
     * A boolean that indicates if the player is the first player.
     */
    private boolean isFirst;

    /**
     * Interface used by the client to connect.
     */
    private ClientInterface client;

    /**
     * The controller of the scene.
     */
    private static volatile SceneController controller;

    /**
     * Semaphore used to wait for the GUI to be initialized.
     */
    private static final Semaphore semaphore = new Semaphore(0);

    /**
     * The stage of the GUI.
     */
    private Stage stage;

    /**
     * The initial scene of the GUI, used to switch between first player and
     * general player scenes.
     */
    private static volatile Scene initialScene;

    /**
     * The root of the GUI.
     */
    private Parent root;

    /**
     * The cards that the player has in hand.
     */
    private PlayableCard[] inHand;

    /**
     * The starting card of the player.
     */
    private PlayableCard startingCard;

    /**
     * The objective cards common to all players.
     */
    private ObjectiveCard[] commonObjectives;

    /**
     * The secret objective of the player.
     */
    private ObjectiveCard personalObjective;

    /**
     * The number of players in the game.
     */
    private int numPlayers;

    /**
     * The list of deployed cards.
     */
    private ArrayList<CardSide>[] deployed;

    /**
     * The placements matrix of the player.
     */
    private int[][][] placements;

    /**
     * The scores of all the players.
     */
    private int[] scores;

    /**
     * The ID of the player.
     */
    private int playerID;

    /**
     * Array of all players' usernames.
     */
    private String[] usernames;

    /**
     * Array of all players' colors.
     */
    private PawnColor[] usersColors;

    /**
     * Array with all the cards on the ground.
     */
    private PlayableCard[] onGround;

    /**
     * The backside of the card on top of the gold deck.
     */
    private CardSide goldenDeckTop;

    /**
     * The backside of the card on top of the resource deck.
     */
    private CardSide resourceDeckTop;

    //0 -> inHand choice
    //1 -> deployed choice
    //2 -> corner choice
    /**
     * The choices taken by the player during its turn.
     */
    private int[] turnChoices = new int[3];

    /**
     * The queue containing all the incoming messages.
     */
    private static BlockingDeque<Message> msgQueue = new LinkedBlockingDeque<>();

    /**
     * A method that starts the GUI, it loads the FXML file
     * and sets the initial scene.
     *
     * @param stage the stage of the GUI
     */
    @Override
    public void start(Stage stage) {

        this.stage = stage;

        Platform.runLater(()->{
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
                root = fxmlLoader.load();
                initialScene = new Scene(root);
                controller = fxmlLoader.getController();

                setInitialScene();

                semaphore.release();

            } catch (IOException e) {
                System.err.println("Errore nel caricamento del file FXML");
                throw new RuntimeException(e);
            }
        });


    }

    /**
     * A method that sets the initial scene of the GUI.
     */
    private void setInitialScene(){
        stage.setTitle("Codex Naturalis");
        stage.getIcons().add(new Image(getClass().getResource("Images/c.jpg").toExternalForm()));
        stage.setScene(initialScene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();

        stage.setOnCloseRequest(e->{
            this.stage.close();
            System.exit(0);
        });
    }

    public GUI(){}

    /**
     * A constructor that initializes the GUI with the given IP
     * and PORT, and the type of connection.
     *
     * @param inputIP the IP address of the server to connect to
     * @param inputPORT the PORT of the server to connect to
     * @param isSocket a boolean that indicates the type of connection
     */
    public GUI(String inputIP, int inputPORT, boolean isSocket) throws IOException, ClassNotFoundException, NotBoundException {

        new Thread(Application::launch).start();

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        if (isSocket){
            this.client = new SocketClient(inputIP, inputPORT);
        } else {
            this.client = new RMIClient(inputIP, inputPORT);
        }

        new Thread(this::getTraffic).start();
        execute();
    }

    /**
     * A method that is run by a separate {@link Thread}, it takes
     * all the incoming messages and puts them in a queue. If the
     * incoming message is a chat message, immediately parse it
     * without adding it to the queue, to have a real-time chat.
     */
    @Override
    public void getTraffic(){
        while(true){
            Message msg = client.readNewMessage();
            if(msg instanceof publicChatMessageMsg){
                Platform.runLater(()->{
                    ((GameSceneController) controller).updateChat(((publicChatMessageMsg) msg).getMessage(), ((publicChatMessageMsg) msg).getSender(), false);
                });
            } else if (msg instanceof privateChatMessageMsg){
                Platform.runLater(()->{
                    ((GameSceneController) controller).updateChat(((privateChatMessageMsg) msg).getMessage(), ((privateChatMessageMsg) msg).getSender(), true);
                });
            } else {
                try {
                    msgQueue.add(msg);
                } catch (Exception e){
                    printToGui("[ERROR] Could not process Socket message! Maybe someone disconnected!");
                    break;
                }
            }
        }
    }

    /**
     * A method that runs periodically on the main {@link Thread},
     * it takes the messages in the queue and processes them.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void execute() throws IOException {
        while (true){
            try {
                viewHandler(msgQueue.take());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * A method that processes the incoming messages.
     * It analyzes the type of the message and calls the appropriate
     * method.
     *
     * @param message the incoming message to process
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void viewHandler(Message message) throws IOException {
        switch (message.getType()){

            case MessageType.FIRSTPLAYER -> {
                isFirst = true;
                switchToLobby(true, null);
            }
            case MessageType.GENERALPLAYER -> {
                isFirst = false;
                switchToLobby(false, (generalPlayerMsg) message);
            }
            case MessageType.REJOIN_PLAYER -> rejoin((rejoinPlayerMsg) message);
            case MessageType.CANNOT_REJOIN -> alreadyPicked((cannotRejoinMsg) message);
            case MessageType.REQUIRED_DATA -> setRequiredData((requiredDataMsg) message);
            case MessageType.HEARTBEAT -> {}

            case MessageType.NICKNAME_ALREADY_USED -> nickOrColorUsed((nickOrColorAlreadyUsedMsg) message);
            case MessageType.CHECK_FIRST -> firstStillJoining((checkFirstMsg) message);
            case MessageType.INITIAL_SITUATION -> updateLocalInitialSituation((initialSituation) message);
            case MessageType.PERSONALOBJECTIVES -> choosePersonalObjectives((personalObjectivesMsg) message);
            case MessageType.INITIALGAMEINFO -> updateInitialGameInfo((initialGameInfoMsg) message);
            case MessageType.PLEACABLECARDS -> displayPlaceableInHand((placeableCardsMsg) message);
            case MessageType.AVAILABLEPLACEMENTS -> displayAvailablePlacements((availablePlacementsMsg) message);
            case MessageType.GAMEINFO -> displayGameInfo((gameInfoMsg) message);
            case MessageType.NEWINHAND -> updateLocalHand((newInHandMsg) message);
            case MessageType.ENDGAME -> endGame((endGameMsg) message);



            default -> throw new IllegalStateException("Unexpected value: " + message.toString());
        }
    }

    /**
     * A method called when the game comes to an end. It prints out
     * the winner and the scoreboard.
     *
     * @param message the message containing the needed information
     */
    @Override
    public void endGame(endGameMsg message) {
        Platform.runLater(()->{
            ((GameSceneController) controller).displayEndGame(message.getRanking(), usernames);
            //TODO update scoseboard(?) [OPTIONAL]
        });
    }

    /**
     * A method that updates the locally saved hand, based on the placed
     * {@link CardSide} and the drawn {@link PlayableCard}.
     *
     * @param message the message containing the needed information
     */
    @Override
    public void updateLocalHand(newInHandMsg message) {
        PlayableCard drawn = message.getDrawn();
        int played = turnChoices[0]/2; //get the index of the Card from the index of the CardSide
        inHand[played] = drawn;
        Platform.runLater(()->{
            ((GameSceneController) controller).displayInHand(inHand);
        });
    }

    /**
     * A method that updates the local client with all the
     * game information, after each turn. It is used both
     * after a turn or when a game is reloaded (it prints
     * the last turn played to all players).
     * The information shown are:
     * - int of the player who should play next
     * - array of the scores
     * - the last on ground card
     * - the index of the last on ground card
     * - placed of the last player who played
     *
     * @param message the message containing all useful information
     */
    @Override
    public void displayGameInfo(gameInfoMsg message) {
        int index = message.getLastOnGroundIndex();
        this.scores = message.getScores();
        this.goldenDeckTop = message.getGoldenDeckTop();
        this.resourceDeckTop = message.getResourceDeckTop();
        if (index!=-1) {
            this.onGround[message.getLastOnGroundIndex()] = message.getLastOnGround();
        }

        int lastPlayer = message.getPlayerID();

        CardSide placedCardSide = message.getPlacedCardSide();
        updatePlayerSituation(lastPlayer, placedCardSide);

        Platform.runLater(()->{
            ((GameSceneController) controller).updateDeployed(lastPlayer, placedCardSide);
            //TODO showDeployed(lastPlayer);  [OPTIONAL]
            ((GameSceneController) controller).updateScoreboard(scores);
        });

        updateOnGround();
        if(index!=-1) {
            if ((lastPlayer + 1) % numPlayers == this.playerID) {
                printToGui(usernames[lastPlayer] + " played its turn. "+ "Now it is your turn!");
                client.writeNewMessage(new SCMsgPlay());
            } else {
                if(lastPlayer == this.playerID){
                    printToGui("You played your turn! " + "Now it is " + usernames[(lastPlayer + 1) % numPlayers] + "'s turn.");
                }
                else {
                    printToGui(usernames[lastPlayer] + " played its turn. "+ "Now it is " + usernames[(lastPlayer + 1) % numPlayers] + "'s turn.");
                }

            }
        } else{
            printToGui("The game is over!");
        }
    }

    /**
     * A method that prints out the placeable cards in player's hand and asks
     * the player which one it wants to place. After that, it asks
     * for the coordinates of the deployed card on which it wants to place it.
     * The information is later sent to the server.
     *
     * @param message the message containing the placeable cards in player's hand
     */
    @Override
    public void displayPlaceableInHand(placeableCardsMsg message) {

        Platform.runLater(()->{
            ((GameSceneController) controller).allowPlaceableInHandSelection(message.getPlaceableCards());
            ((GameSceneController) controller).displayDeployed();
        });
    }

    /**
     * A method that writes a new message to the server.
     *
     * @param message the message to write
     */
    public void writeNewMessage(Message message){
        client.writeNewMessage(message);
    }

    /**
     * A method that switches the scene to the lobby.
     *
     * @param isFirst a boolean that indicates if the player is the first
     * @param message the message containing the remaining players and their nicknames
     */
    private void switchToLobby(boolean isFirst, generalPlayerMsg message){
        if (isFirst){
            initialScene.setOnKeyPressed(keyEvent -> {
                switch (keyEvent.getCode()){
                    case SPACE, ENTER -> controller = ((InitialSceneController) controller).switchToFirstLobby(keyEvent, this);
                }
            });
        } else {
            initialScene.setOnKeyPressed(keyEvent -> {
                switch (keyEvent.getCode()){
                    case SPACE, ENTER -> controller = ((InitialSceneController) controller).switchToGeneralLobby(keyEvent, this, message.getRemaining(), message.getNicknames());
                }
            });
        }
    }
    /**
     * A method run when a player is rejoining a loaded game.
     * It prints the usernames chosen for this game and asks
     * the player for their choice.
     *
     * @param msg the message containing the usernames
     */
    @Override
    public void rejoin(rejoinPlayerMsg msg){
        initialScene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()){
                case SPACE, ENTER -> controller = ((InitialSceneController) controller).switchToRejoinLobby(keyEvent, this, msg.getPlayers());
            }
        });
    }
    /**
     * A method run if the player, when rejoining, picks a
     * username already picked in the meantime by someone else.
     *
     * @param msg the message containing the usernames
     */
    @Override
    public void alreadyPicked(cannotRejoinMsg msg){
        Platform.runLater(()->{
            ((RejoinLobbyController) controller).setAvailableUsernames(msg.getInvalid(), msg.getPlayers());
        });
    }

    /**
     * A method that restores all the helpful information
     * when a game is loaded and restarted. All these information
     * where present when the player got kicked or when the
     * match crashed, thus are mandatory to proceed the match.
     *
     * @param msg the message containing all necessary data
     */
    @Override
    public void setRequiredData(requiredDataMsg msg) {
        this.inHand = msg.getInHand();
        this.startingCard = msg.getStartingCard();
        this.onGround = msg.getOnGround();
        this.commonObjectives = msg.getCommonObjectives();
        this.personalObjective = msg.getPersonalObjective();
        this.scores = msg.getScores();
        this.placements = msg.getPlacements();
        this.deployed = msg.getDeployed();
        this.usersColors = msg.getColors();
        this.usernames = msg.getUsernames();
        this.numPlayers = msg.getNumPlayers();

        Platform.runLater(()->{
            controller = ((RejoinLobbyController) controller).buildGameScene(this);
            ((GameSceneController) controller).rebuildEntireGameScene(inHand, onGround, commonObjectives,
                    personalObjective, scores, deployed, usersColors, usernames, msg.getFirstPlayer());
        });
    }

    /**
     * A method that sets the local version of player ID.
     *
     * @param playerID the player ID to set
     */
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    /**
     * A method run when a player has inserted a duplicate username or
     * color chosen from another player.
     *
     * @param msg the message containing the usernames and colors used
     */
    @Override
    public void nickOrColorUsed(nickOrColorAlreadyUsedMsg msg){
        Platform.runLater(()->{
            ((GeneralLobbyController) controller).setEnabledColoursAndUsernames(msg.getColors(), msg.getNicknames());
        });
    }
    /**
     * A method that checks periodically if the first
     * player is still setting up the game.
     *
     * @param msg the message containing the usernames and colors
     */
    @Override
    public void firstStillJoining(checkFirstMsg msg){
        Platform.runLater(()->{
            ((GeneralLobbyController) controller).setEnabledColoursAndUsernames(msg.getRemaining(), msg.getNicknames());
        });
    }

    /**
     * A method that saved the initial data structure, consisting of
     * the starting card, hands and common objectives.
     * Lastly, it asks the server for the potential objective cards
     * to choose from for the secret objective.
     *
     * @param message the message containing all necessary data
     */
    @Override
    public void updateLocalInitialSituation(initialSituation message){

        //salva dati locali
        this.startingCard = message.getStartingCard();
        this.inHand = message.getInHand();
        this.commonObjectives = message.getCommonObjectives();
        this.playerID = message.getPlayerID();

        //costruire GameScene
        Platform.runLater(()->{
            if (isFirst){
                controller = ((FirstLobbyController) controller).buildGameScene(this);
            } else {
                controller = ((GeneralLobbyController) controller).buildGameScene(this);
            }

            //chiama metodi controller
            ((GameSceneController) controller).displayInitialSituation(inHand, commonObjectives);
            ((GameSceneController) controller).displayStartingSides(startingCard);
            ((GameSceneController) controller).setPlayerID(playerID);

        });
    }

    /**
     * A method run when the player has to choose its secret
     * objective. It shows the two personal objective cards
     * drawn, asks which one the player wants to choose,
     * send the message to the server and, lastly, it calls
     * the next method.
     *
     * @param msg the message containing the personal objective cards
     */
    @Override
    public void choosePersonalObjectives(personalObjectivesMsg msg){
        Platform.runLater(()->{
            ((GameSceneController) controller).displayPersonalObjectives(msg.getPersonalObjectives());
        });
    }
    /**
     * A method that updates the local client with all the
     * initial game information. Only used when a new  game is started.
     *
     * @param msg the message containing the initial game info
     */
    @Override
    public void updateInitialGameInfo(initialGameInfoMsg msg) {
        CardSide[] starting = msg.getStartingSides();
        int first = msg.getFirst();
        this.usernames = msg.getUsernames();
        this.usersColors = msg.getColors();
        this.numPlayers = starting.length;
        this.deployed = new ArrayList[numPlayers];

        this.onGround = msg.getOnGround();
        this.goldenDeckTop = msg.getGoldenDeckTop();
        this.resourceDeckTop = msg.getResourceDeckTop();
        updateOnGround();
        enableCommands();

        this.placements = new int[numPlayers][81][81];

        for (int i=0; i<numPlayers; i++){
            this.deployed[i] = new ArrayList<CardSide>();
            updatePlayerSituation(i,starting[i]);
        }

        Platform.runLater(()->{
            ((GameSceneController) controller).initializePinionsAndPanes(usernames, usersColors, first);
            ((GameSceneController) controller).initializeChat(usernames, usersColors);
            for (int i=0; i<numPlayers;i++) ((GameSceneController) controller).updateDeployed(i, starting[i]);
            ((GameSceneController) controller).displayDeployed();
        });

        if (first == this.playerID){
            printToGui("Now it is your turn!");
            client.writeNewMessage(new SCMsgPlay());
        } else {
            printToGui("Now it is " + usernames[first]+ "'s turn. Please wait for your turn!");

        }



    }

    /**
     * A method that updates the on ground cards of the player with the local copy of the cards.
     *
     */
    private void updateOnGround(){
        Platform.runLater(()->{
            ((GameSceneController) controller).updateOnGround(onGround, goldenDeckTop, resourceDeckTop);
        });
    }

    /**
     * A method that enables the OnGround and Deployed buttons.
     *
     */
    public void enableCommands(){
        Platform.runLater(()->{
            ((GameSceneController) controller).enableCommands();
        });
    }

    /**
     * A method that prints a message to the GUI.
     * @param message the message to print
     */
    public void printToGui(String message){
        Platform.runLater(()->{
            ((GameSceneController) controller).printToGui(message);
        });
    }

    /**
     * A method that updates the local situation of a player
     * when he/she places a card. It also updates the placements
     * of that player.
     *
     * @param playerID the ID of the player who placed the card
     * @param placedCardSide the card that was placed
     */
    @Override
    public void updatePlayerSituation(int playerID, CardSide placedCardSide){
        this.deployed[playerID].add(placedCardSide);
        int x = placedCardSide.getRelativeCoordinates()[0] + Player.OFFSET;
        int y = placedCardSide.getRelativeCoordinates()[1] + Player.OFFSET;
        this.placements[playerID][x][y] = placedCardSide.getDeployedID();
    }

    /**
     * A method that saves the in hand card to deploy chosen by the player.
     *
     * @param index the inHand index of the card to deploy
     */
    public void chooseInHandCard(int index) {
        turnChoices[0] = index;
        printToGui("Choose a card on the table on which to place your selection");
    }
    /**
     * A method that saves the deployed card chosen by the player.
     *
     * @param index the deployed index of the card to deploy
     */
    public void chooseDeployedCard(int index){
        turnChoices[1] = index;
        client.writeNewMessage(new SCMsgCardCouple(turnChoices[1]));
    }

    /**
     * A method that prints out the available corner placements to the player,
     * who then has to decide one. After insertion, the information is forwarded to the server.
     * If the player inserts "-1", he's sent back to choosing a card to place.
     *
     * @param msg the message containing the corners on which placement is possible
     */
    @Override
    public void displayAvailablePlacements(availablePlacementsMsg msg){
        printToGui("Select a corner to perform the placement");
        Platform.runLater(()->{
            ((GameSceneController) controller).drawClickableCorners(msg.getAvailablePlacements());
        });
    }

    /**
     * A method that saves the corner chosen by the player.
     *
     * @param index the index of the corner chosen
     */
    public void chooseCorner(int index){
        turnChoices[2] = index;

    }

    /**
     * A method that plays the turn with the chosen cards and corner and drawn card.
     *
     * @param drawn the index of the drawn card
     */
    public void chooseDrawnCard(int drawn) {
        client.writeNewMessage(new SCMsgPlayTurn(turnChoices[0],turnChoices[1],turnChoices[2],drawn));
        ((GameSceneController) controller).displayDeployed();
    }

    /**
     * A method that sends a chat message to the server.
     * @param from the sender of the message
     * @param to the receiver of the message (ALL if public)
     * @param message the message to send
     */
    public void sendChatMessage(String from, String to, String message){
        if (to.equals("ALL")){
            client.writeNewMessage(new publicChatMessageMsg(from, message));
        } else {
            client.writeNewMessage(new privateChatMessageMsg(from, to, message));
        }
    }
}
