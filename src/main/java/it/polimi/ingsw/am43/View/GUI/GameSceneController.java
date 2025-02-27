package it.polimi.ingsw.am43.View.GUI;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Network.Messages.controllerMessages.SCMsgDrawPersonalObjectives;
import it.polimi.ingsw.am43.Network.Messages.controllerMessages.SCMsgPersonalObjective;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is the controller of the game scene and controls every section
 * of the game scene. It directly interacts with the GUI and the controllers
 * of every section of the scene.
 */
public class GameSceneController extends SceneController{
    /**
     * The player ID
     */
    private int playerID;

    /**
     * The username
     */
    private String username;

    /**
     * The bottom pane
     */
    @FXML
    private AnchorPane bottomPANE;

    /**
     * The in hand controller
     */
    private InHandController inHandController;

    /**
     * The main pane
     */
    @FXML
    private AnchorPane mainPANE;

    /**
     * The top box
     */
    @FXML
    private HBox topBOX;

    /**
     * The scoreboard controller
     */
    private ScoreboardController scoreboardController;

    /**
     * The objectives controller
     */
    private ObjectivesController objectivesController;

    /**
     * The choosable elements
     */
    @FXML
    private ImageView choosable1;
    @FXML
    private ImageView choosable2;

    /**
     * The on ground VBox
     */
    @FXML
    private VBox ongroundVBOX;

    /**
     * The on ground controller
     */
    private OnGroundController onGroundController;

    /**
     * The deployed pane
     */
    @FXML
    private ScrollPane deployedScrollPane;

    /**
     * The deployed controller
     */
    private DeployedController deployedController;

    /**
     * The end game VBox
     */
    @FXML
    private VBox endGameVBOX;

    /**
     * The end game controller
     */
    private EndGameController endGameController;

    /**
     * Set the stage
     * @param stage the stage
     */
    public void setOnCloseConfirm(Stage stage){

        setStage(stage);

        this.stage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You are going to terminate the match for all the players. Are you sure?", ButtonType.YES, ButtonType.NO);
            alert.setHeaderText(null);
            alert.setTitle("Confirm");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    stage.close();
                    System.exit(0);
                }
            });
        });
    }

    /**
     * Initialize the controller loading the fxml file of every section
     */
    @FXML
    public void initialize(){

        //carica tutte le parti della scena

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InHand.fxml"));
            bottomPANE.getChildren().add(loader.load());
            inHandController = loader.getController();
            inHandController.setGameSceneController(this);



        } catch (IOException e) {
            System.err.println("Errore nel caricamento del file FXML");
            throw new RuntimeException(e);
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Scoreboard.fxml"));
            topBOX.getChildren().add(loader.load());
            scoreboardController = loader.getController();

            scoreboardController.setGameSceneController(this);

        } catch (IOException e) {
            System.err.println("Errore nel caricamento del file FXML");
            throw new RuntimeException(e);
        }

        mainPANE = new AnchorPane();
        mainPANE.setPrefSize(715,504);
        mainPANE.getStyleClass().add("casino-table");
        topBOX.getChildren().add(mainPANE);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Objectives.fxml"));
            topBOX.getChildren().add(loader.load());
            objectivesController = loader.getController();
            objectivesController.setGameSceneController(this);

        } catch (IOException e) {
            System.err.println("Errore nel caricamento del file FXML");
            throw new RuntimeException(e);
        }


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OnGround.fxml"));
            ongroundVBOX = loader.load();
            onGroundController = loader.getController();
            onGroundController.setGameSceneController(this);

        } catch (IOException e) {
            System.err.println("Errore nel caricamento del file FXML");
            throw new RuntimeException(e);
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Deployed.fxml"));
            deployedScrollPane = loader.load();
            deployedController = loader.getController();
            deployedController.setGameSceneController(this);

        } catch (IOException e) {
            System.err.println("Errore nel caricamento del file FXML");
            throw new RuntimeException(e);
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EndGame.fxml"));
            endGameVBOX = loader.load();
            endGameController = loader.getController();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento del file FXML");
            throw new RuntimeException(e);
        }

    }

    /**
     * Set the player ID
     * @param playerID the player ID
     */
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    /**
     * Set the username
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Enable the deployed and on ground buttons
     */
    public void enableCommands() {
        objectivesController.enableCommands();
    }

    /**
     * Display the initial situation
     * @param inHand the cards in hand
     * @param commonObjectives the common objectives
     */
    public void displayInitialSituation(PlayableCard[] inHand, ObjectiveCard[] commonObjectives){
        inHandController.displayInHand(inHand);
        objectivesController.setCommonObjectives(commonObjectives);
    }

    /**
     * Display the starting sides to choose from
     * @param startingCard the starting card
     */
    public void displayStartingSides(PlayableCard startingCard){

        choosable1 = new ImageView(new Image(getClass().getResource(startingCard.getFrontPath()).toExternalForm()));
        choosable1.setPreserveRatio(true);
        choosable1.setFitWidth(210);
        choosable1.setLayoutX(120);
        choosable1.setLayoutY(180);
        choosable2 = new ImageView(new Image(getClass().getResource(startingCard.getBackPath()).toExternalForm()));
        choosable2.setPreserveRatio(true);
        choosable2.setFitWidth(210);
        choosable2.setLayoutX(382);
        choosable2.setLayoutY(180);

        mainPANE.getChildren().add(choosable1);
        mainPANE.getChildren().add(choosable2);

        choosable1.setOnMouseClicked(mouseEvent -> chooseStartingSide(0));
        choosable2.setOnMouseClicked(mouseEvent -> chooseStartingSide(1));

    }

    /**
     * Choose the starting side
     * @param choice the choice
     */
    private void chooseStartingSide(int choice){
        gui.writeNewMessage(new SCMsgDrawPersonalObjectives(choice));
        mainPANE.getChildren().clear();
    }

    /**
     * Display the personal objectives
     * @param objectives the objectives
     */
    public void displayPersonalObjectives(ObjectiveCard[] objectives){
        String path1 = objectives[0].getFrontPath();
        choosable1 = new ImageView(new Image(getClass().getResource(path1).toExternalForm()));
        choosable1.setPreserveRatio(true);
        choosable1.setFitWidth(210);
        choosable1.setLayoutX(120);
        choosable1.setLayoutY(180);

        String path2 = objectives[1].getFrontPath();
        choosable2 = new ImageView(new Image(getClass().getResource(path2).toExternalForm()));
        choosable2.setPreserveRatio(true);
        choosable2.setFitWidth(210);
        choosable2.setLayoutX(382);
        choosable2.setLayoutY(180);

        mainPANE.getChildren().add(choosable1);
        mainPANE.getChildren().add(choosable2);

        choosable1.setOnMouseClicked(mouseEvent -> choosePersonalObjective(objectives[0]));
        choosable2.setOnMouseClicked(mouseEvent -> choosePersonalObjective(objectives[1]));

    }

    /**
     * Initialize the pinions, the panes and the deployed switch
     * @param usernames the usernames
     * @param colors the colors
     * @param first the first player
     */
    public void initializePinionsAndPanes(String[] usernames, PawnColor[] colors, int first){
        this.username = usernames[playerID];
        scoreboardController.initializePinions(colors);
        inHandController.initializeDeployedSwitch(usernames,colors);
        deployedController.initializePanes(colors, first, playerID);
    }

    /**
     * Display the on ground cards
     */
    public void displayOnGround(){
        mainPANE.getChildren().clear();
        mainPANE.getChildren().add(ongroundVBOX);
    }

    /**
     * Display the deployed cards
     */
    public void displayDeployed() {
        deployedController.setOnThisPlayer();
        mainPANE.getChildren().clear();
        mainPANE.getChildren().add(deployedScrollPane);
    }

    /**
     * Switch to the deployed pane
     */
    public void simpleSwitchToDeployed(){
        mainPANE.getChildren().clear();
        mainPANE.getChildren().add(deployedScrollPane);
    }

    /**
     * Update the on ground cards
     * @param onGround the on ground cards
     * @param goldenDeck the golden deck
     * @param resourceDeck the resource deck
     */
    public void updateOnGround(PlayableCard[] onGround, CardSide goldenDeck, CardSide resourceDeck){
        onGroundController.updateOnGround(onGround, goldenDeck, resourceDeck);
    }

    /**
     * Update the deployed cards
     * @param playerID the player ID
     * @param newSide the new side
     */
    public void updateDeployed(int playerID, CardSide newSide){
        deployedController.drawCardSide(playerID, newSide);
    }

    /**
     * Choose the personal objective
     * @param choice the choice of the user
     */
    private void choosePersonalObjective(ObjectiveCard choice){
        gui.writeNewMessage(new SCMsgPersonalObjective(choice));
        mainPANE.getChildren().clear();

        setPersonalObjective(choice);
    }

    /**
     * Set the personal objective
     * @param choice the choice of the user
     */
    public void setPersonalObjective(ObjectiveCard choice){
        objectivesController.setPersonalObjective(choice);
    }

    /**
     * Allow the player to choose a card from the hand
     * @param placeableCards the placeable cards
     */
    public void allowPlaceableInHandSelection(Boolean[] placeableCards) {
        printToGui("Choose a card from your hand to deploy it");
        inHandController.allowPlaceableInHandSelection(placeableCards);
    }

    /**
     * Choose the in hand card to play
     * @param index the index of the card
     */
    public void chooseInHandCard(int index){
        gui.chooseInHandCard(index);
        deployedController.setOnThisPlayer();
        deployedController.enableCardChoice();
    }

    /**
     * Choose the deployed card to place the inHand card onto
     * @param index the index of the card
     */
    public void chooseDeployedCard(int index){
        gui.chooseDeployedCard(index);
    }

    /**
     * Choose the corner to place the card
     * @param index the index of the corner
     */
    public void chooseCorner(int index){
        gui.chooseCorner(index);
        inHandController.clearAllEffects();
        inHandController.cancelSelected();
        printToGui("Choose a card you want to place from the ones on the ground. You can also choose to draw a card from one of the decks.");
        displayOnGround();
        onGroundController.enableDrawCard();
    }

    /**
     * Draws the available corners in the deployed pane for the user to choose one
     * @param available the available corners
     */
    public void drawClickableCorners(boolean[] available){
        boolean alo = false; //at least one
        for (boolean b : available)
            if (b) {
                alo = true;
                break;
            }
        if (!alo) {
            askNewInHandSelection();
            printToGui("There are no available placements for the selected cards! Choose another couple of cards.");

        } else {
            deployedController.drawClickableCorners(available);
        }

    }

    /**
     * Print a message to the GUI
     * @param message the message
     */
    public void printToGui(String message) {
        inHandController.printToGui(message);
    }

    /**
     * Choose the drawn card
     * @param choice the choice of the card
     */
    public void chooseDrawnCard(int choice) {
        gui.chooseDrawnCard(choice);

    }

    /**
     * Update the scoreboard
     * @param scores the scores
     */
    public void updateScoreboard(int[] scores) {
        scoreboardController.redrawPinions(scores);
    }

    /**
     * Display the inHand cards
     * @param inHand the inHand cards
     */
    public void displayInHand(PlayableCard[] inHand) {
        inHandController.displayInHand(inHand);
    }

    /**
     * Ask the player to choose a card from the hand
     */
    public void askNewInHandSelection() {
        printToGui("Choose a card from your hand to deploy it");
        inHandController.askNewInHandSelection();
    }

    /**
     * Resets the deployed choice
     */
    public void cancelDeployed(){
        deployedController.cancelDeployed();
    }

    /**
     * Display the end game
     * @param scores the scores
     * @param usernames the usernames
     */
    public void displayEndGame(int[][] scores, String[] usernames){
        mainPANE.getChildren().clear();
        endGameController.showScores(scores, usernames);
        mainPANE.getChildren().add(endGameVBOX);
    }

    /**
     * Show the deployed pane of the player
     * @param playerID the player ID
     */
    public void setDeployedPane(int playerID){
        deployedController.setOnPlayerID(playerID);
    }

    /**
     * Initialize the chat
     * @param usernames the usernames of the other players
     * @param usersColors the users colours of the other players
     */
    public void initializeChat(String[] usernames, PawnColor[] usersColors){
        PawnColor[] other_colors = Arrays.stream(usersColors).filter(a -> !a.equals(usersColors[playerID])).toArray(PawnColor[]::new);
        String[] other_names = Arrays.stream(usernames).filter(a -> !a.equals(username)).toArray(String[]::new);
        inHandController.initializeChat(other_names, other_colors);
    }

    /**
     * Update the chat
     * @param newMessage the new message
     * @param sender the sender
     * @param isPriv if the message is private
     */
    public void updateChat(String newMessage, String sender, boolean isPriv){
        inHandController.updateChatTextFlow(newMessage, sender, isPriv);
    }

    /**
     * Send a chat message
     * @param to the receiver
     * @param message the message
     */
    public void sendChatMessage(String to, String message){
        gui.sendChatMessage(username, to, message);
    }

    /**
     * Rebuild the entire game scene
     * @param inHand the inHand cards
     * @param onGround the onGround cards
     * @param commonObjectives the common objectives
     * @param personalObjective the personal objective
     * @param scores the scores
     * @param deployed the deployed cards
     * @param usersColors the users colours
     * @param usernames the usernames
     * @param firstPlayer the first player
     */
    public void rebuildEntireGameScene(PlayableCard[] inHand, PlayableCard[] onGround,
                                       ObjectiveCard[] commonObjectives, ObjectiveCard personalObjective,
                                       int[] scores, ArrayList<CardSide>[] deployed, PawnColor[] usersColors,
                                       String[] usernames, int firstPlayer){

        //scoreboard
        scoreboardController.initializePinions(usersColors);
        scoreboardController.redrawPinions(scores);

        //onground
        onGroundController.updateOnGround(onGround, onGround[0].getBackside(), onGround[1].getBackside());
        //deployed
        deployedController.redrawAllDeployedPanes(usersColors, firstPlayer, playerID, deployed);
        displayDeployed();

        //objectives
        objectivesController.setPersonalObjective(personalObjective);
        objectivesController.setCommonObjectives(commonObjectives);
        objectivesController.enableCommands();

        //inhand - bottom
        inHandController.displayInHand(inHand);
        inHandController.initializeDeployedSwitch(usernames, usersColors);
        inHandController.initializeChat(Arrays.stream(usernames).filter(a -> !a.equals(username)).toArray(String[]::new), Arrays.stream(usersColors).filter(a -> !a.equals(usersColors[playerID])).toArray(PawnColor[]::new));
    }
}
