package it.polimi.ingsw.am43.View.GUI;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.util.ArrayList;

/**
 * Controller of the deployed Pane in the center of the screen.
 * It manages the graphic display of the deployed situation of all the players,
 * allowing the player to switch between them whenever he/she wants during the game.
 */
public class DeployedController extends SceneController{

    /**
     * The main scroll pane to handle the enlarging dimensions of the deployed pane
     */
    @FXML
    private ScrollPane deployedScrollPANE;

    /**
     * The number of players
     */
    private int nPlayers;
    /**
     * The index of the first player
     */
    private int first;
    /**
     * The index of the player associated to the current client
     */
    private int thisPlayer;

    /**
     * The array containing the deployed panes of all the players, corresponding by index
     */
    @FXML
    private Pane[] playersPanes;
    /**
     * The array of the images of the pinions
     */
    @FXML
    private ImageView[] pinions;
    /**
     * The image of the black pinion
     */
    @FXML
    private ImageView firstPinion;

    /**
     * Relevant dimensions of the panes
     */
    private final int[] minPaneDim = {700,498};
    /**
     * Relevant distances between two cards
     */
    private final int[] distances = {93,48};
    /**
     * Relevant dimensions of a card
     */
    private final int[] cardDim = {120,80};

    private int[][] TL;
    private int[][] BR;
    /**
     * Current dimensions of a player's pane
     */
    private int[][] dimPane;
    /**
     * Coordinates of the center of the panes (for each player)
     */
    private double[][] CENTER;
    /**
     * Coordinates of the barycenter of the cards (for each player), to be placed in the center of the pane
     */
    private double[][] BARYCENTER;
    /**
     * Coordinates of the starting card (for each player)
     */
    private double[][] STARTING;

    /**
     * Already drawn cards
     */
    private ArrayList<ImageView>[] drawnCards;
    private int[] chosen;

    /**
     * The GameSceneController
     */
    private GameSceneController gameSceneController;

    /**
     * Small rectangles to be drawn on cards to click on the corners
     */
    private Pane[] clickableCorners;
    /**
     * Little cross to cancel the choice
     */
    private ImageView cancelButton;

    public void setGameSceneController(GameSceneController gameSceneController) {
        this.gameSceneController = gameSceneController;
    }

    /**
     * Method to initialize all the players' pane and the pinions to be shown on the screen according to
     * @param color array of players' colors
     * @param first the index of the first player
     * @param thisPlayer the index of this client's player
     */
    public void initializePanes(PawnColor[] color, int first, int thisPlayer){

        this.nPlayers = color.length;
        this.thisPlayer = thisPlayer;

        playersPanes = new Pane[nPlayers];
        pinions = new ImageView[nPlayers];

        TL = new int[nPlayers][2];
        BR = new int[nPlayers][2];
        dimPane = new int[nPlayers][2];
        CENTER = new double[nPlayers][2];
        BARYCENTER = new double[nPlayers][2];
        STARTING = new double[nPlayers][2];

        drawnCards = new ArrayList[nPlayers];
        chosen = new int[nPlayers];

        clickableCorners = new Pane[4];

        for (int j=0; j<nPlayers;j++){

            TL[j][0] = 0;
            TL[j][1] = 0;
            BR[j][0] = 0;
            BR[j][1] = 0;
            dimPane[j][0] = minPaneDim[0];
            dimPane[j][1] = minPaneDim[1];
            CENTER[j][0] = (double) minPaneDim[0] /2;
            CENTER[j][1] = (double) minPaneDim[1] /2;

            BARYCENTER[j] = new double[]{0, 0};
            STARTING[j] = new double[2];
            STARTING[j][0] = CENTER[j][0];
            STARTING[j][1] = CENTER[j][1];

            playersPanes[j] = new Pane();
            playersPanes[j].setPrefSize(dimPane[j][0],dimPane[j][1]);
            playersPanes[j].getStyleClass().add("casino-table");

            drawnCards[j] = new ArrayList<>();
        }

        //inizializza per this giocatore
        cancelButton = new ImageView(new Image(getClass().getResource("Images/cancel.png").toExternalForm()));
        cancelButton.setPreserveRatio(true);
        cancelButton.setFitWidth(30);
        cancelButton.setOnMouseClicked(mouseEvent -> {
            cancelDeployed();
            enableCardChoice();
            //TODO decidere cosa fa la crocetta
            //gameSceneController.askNewInHandSelection();
        });
        cancelButton.setVisible(false);
        playersPanes[thisPlayer].getChildren().add(cancelButton);

        for (int i=0; i<4; i++){
            clickableCorners[i] = new Pane();
            clickableCorners[i].setPrefSize(30,30);
            clickableCorners[i].getStyleClass().add("clickable-pane");

            int finalI = i;
            clickableCorners[i].setOnMouseClicked(mouseEvent -> clickCorner(finalI));

            clickableCorners[i].setVisible(false);
            playersPanes[thisPlayer].getChildren().add(clickableCorners[i]);
        }

        positionDeployedPinion(color, first);
        deployedScrollPANE.setContent(playersPanes[thisPlayer]);
    }

    /**
     * To come back to the choice of the card on which to deploy
     */
    public void cancelDeployed(){
        cancelButton.setVisible(false);
        for (int i=0; i<4; i++){
            clickableCorners[i].setVisible(false);
        }
    }

    /**
     * To draw the pinion on each pane according to
     * @param color array of players' colors
     * @param first the index of the first player
     */
    private void positionDeployedPinion(PawnColor[] color, int first){

        this.first = first;

        String path = (PawnColor.BLACK.toGUIColor());
        firstPinion = new ImageView(new Image(getClass().getResource(path).toExternalForm()));
        firstPinion.setPreserveRatio(true);
        firstPinion.setFitWidth(40);
        firstPinion.setLayoutX(STARTING[first][0]-20);
        firstPinion.setLayoutY(STARTING[first][1]-25+(double) cardDim[1] /2);
        firstPinion.setViewOrder(0);

        for (int i=0; i<color.length; i++){

            path = color[i].toGUIColor();
            pinions[i] = new ImageView(new Image(getClass().getResource(path).toExternalForm()));
            pinions[i].setPreserveRatio(true);
            pinions[i].setFitWidth(40);
            pinions[i].setLayoutX(STARTING[i][0]-20);
            pinions[i].setLayoutY(STARTING[i][1]-20+(double) cardDim[1] /2);
            pinions[i].setViewOrder(0);
            playersPanes[i].getChildren().add(pinions[i]);

            if (i==first) playersPanes[i].getChildren().add(firstPinion);
        }
    }

    /**
     * This method draw a
     * @param newSide card side on the
     * @param playerID player's pane
     * */
    public void drawCardSide(int playerID, CardSide newSide){

        double[] oldCenter = new double[2];
        double[] oldBarycenter = new double[2];
        oldCenter[0] = CENTER[playerID][0];
        oldCenter[1] = CENTER[playerID][1];

        oldBarycenter[0] = BARYCENTER[playerID][0];
        oldBarycenter[1] = BARYCENTER[playerID][1];


        int relX, relY;
        relX = newSide.getRelativeCoordinates()[1];
        relY = newSide.getRelativeCoordinates()[0];

        TL[playerID][0] = Math.min(TL[playerID][0],relX);
        TL[playerID][1] = Math.min(TL[playerID][1],relY);
        BR[playerID][0] = Math.max(BR[playerID][0],relX);
        BR[playerID][1] = Math.max(BR[playerID][1],relY);
        BARYCENTER[playerID][0] = ((double) (BR[playerID][0] + TL[playerID][0]) / 2)*distances[0];
        BARYCENTER[playerID][1] = ((double) (BR[playerID][1] + TL[playerID][1]) / 2)*distances[1];

        dimPane[playerID][0] = Math.max(minPaneDim[0], (BR[playerID][0]-TL[playerID][0])*distances[0]+2*cardDim[0]);
        dimPane[playerID][1] = Math.max(minPaneDim[1], (BR[playerID][1]-TL[playerID][1])*distances[1]+2*cardDim[1]);
        playersPanes[playerID].setPrefSize(dimPane[playerID][0],dimPane[playerID][1]);

        CENTER[playerID][0] = (double) dimPane[playerID][0] /2;
        CENTER[playerID][1] = (double) dimPane[playerID][1] /2;

        double[] vector = new double[2];
        vector[0] = -(CENTER[playerID][0]-oldCenter[0])+(BARYCENTER[playerID][0]-oldBarycenter[0]);
        vector[1] = -(CENTER[playerID][1]-oldCenter[1])+(BARYCENTER[playerID][1]-oldBarycenter[1]);

        STARTING[playerID][0] = STARTING[playerID][0] - vector[0];
        STARTING[playerID][1] = STARTING[playerID][1] - vector[1];

        for (ImageView i : drawnCards[playerID]){
            i.setLayoutX(i.getLayoutX()-vector[0]);
            i.setLayoutY(i.getLayoutY()-vector[1]);
        }

        pinions[playerID].setLayoutX(STARTING[playerID][0]-20);
        pinions[playerID].setLayoutY(STARTING[playerID][1]-20+(double) cardDim[1] /2);
        if (playerID==first) {
            firstPinion.setLayoutX(STARTING[playerID][0]-20);
            firstPinion.setLayoutY(STARTING[playerID][1]-25+(double) cardDim[1] /2);
        }

        //disegno nuova carta
        String path = newSide.getPngPath();
        ImageView newImage = new ImageView(new Image(getClass().getResource(path).toExternalForm()));
        newImage.setPreserveRatio(true);
        newImage.setFitWidth(cardDim[0]);
        newImage.setFitHeight(cardDim[1]);
        newImage.setViewOrder(1);
        drawnCards[playerID].add(newImage);

        newImage.setLayoutX(STARTING[playerID][0]+relX*distances[0]-60);
        newImage.setLayoutY(STARTING[playerID][1]+relY*distances[1]-40);

        playersPanes[playerID].getChildren().add(newImage);

        deployedScrollPANE.setVvalue(deployedScrollPANE.getVmax()/2);
        deployedScrollPANE.setHvalue(deployedScrollPANE.getHmax()/2);
    }

    /**
     * This method enables the clickable cards and links them to the choice of them
     */
    public void enableCardChoice(){
        for (int i=0; i<drawnCards[thisPlayer].size(); i++){
            int finalI = i;
            drawnCards[thisPlayer].get(i).setOnMouseClicked(mouseEvent -> {
                System.out.println(finalI);
                gameSceneController.chooseDeployedCard(finalI);
                chosen[thisPlayer] = finalI;
                for (ImageView c : drawnCards[thisPlayer]){
                    c.setOnMouseClicked(null);
                }
            });
        }
    }

    /**
     * This method enables the clickable corners and links them to the choice of them according to the
     * @param available ones received by the server.
     */
    public void drawClickableCorners(boolean[] available){
        //metti la carta al centro
        cancelButton.setLayoutX(drawnCards[thisPlayer].get(chosen[thisPlayer]).getLayoutX()-15+ (double) cardDim[0] /2);
        cancelButton.setLayoutY(drawnCards[thisPlayer].get(chosen[thisPlayer]).getLayoutY()-15+ (double) cardDim[1] /2);
        cancelButton.setViewOrder(0);
        cancelButton.setVisible(true);

        //sposta e abilita quadratini
        for (int i=0; i<4; i++){
            clickableCorners[i].setLayoutX(drawnCards[thisPlayer].get(chosen[thisPlayer]).getLayoutX()+(i%2)*(cardDim[0]-30));
            clickableCorners[i].setLayoutY(drawnCards[thisPlayer].get(chosen[thisPlayer]).getLayoutY()+(i/2)*(cardDim[1]-30));
            clickableCorners[i].setViewOrder(0);
        }
        for (int i=0; i<available.length; i++){
            clickableCorners[i].setVisible(available[i]);
        }

    }

    /**
     * This method communicates the choice of a corner to the gameSceneController.
      * @param index the index of the selected corner
     */
    private void clickCorner(int index){
        cancelButton.setVisible(false);
        gameSceneController.chooseCorner(index);
        for (int i=0; i<4; i++){
            clickableCorners[i].setVisible(false);
        }
    }

    /**
     * Set the content of the scroll pane on this (client) player
     */
    public void setOnThisPlayer(){
        playersPanes[thisPlayer].getStyleClass().clear();
        playersPanes[thisPlayer].getStyleClass().add("casino-table");
        deployedScrollPANE.setContent(playersPanes[thisPlayer]);
        deployedScrollPANE.setVvalue(deployedScrollPANE.getVmax()/2);
        deployedScrollPANE.setHvalue(deployedScrollPANE.getHmax()/2);
    }

    /**
     * Set the content of the scroll pane on the
     * @param playerID index
     */
    public void setOnPlayerID(int playerID){
        if (playerID != thisPlayer){
            playersPanes[playerID].getStyleClass().clear();
            playersPanes[playerID].getStyleClass().add("casino-table-others");
        }
        deployedScrollPANE.setContent(playersPanes[playerID]);
        deployedScrollPANE.setVvalue(deployedScrollPANE.getVmax()/2);
        deployedScrollPANE.setHvalue(deployedScrollPANE.getHmax()/2);
    }

    /**
     * This method is used when loading an existing game. It redraws all the deployed sides for each player.
     * @param colors the pawn colors
     * @param firstPlayer the index of the first player
     * @param thisPlayer the index of this player
     * @param deployed the array of the cards to be drawn
     */
    public void redrawAllDeployedPanes(PawnColor[] colors, int firstPlayer, int thisPlayer, ArrayList<CardSide>[] deployed){

        first = firstPlayer;
        System.out.println(first);
        initializePanes(colors, first, thisPlayer);

        for (int i=0; i<deployed.length;i++){
            for (CardSide c : deployed[i]){
                drawCardSide(i,c);
            }
        }
        setOnThisPlayer();
    }
}
