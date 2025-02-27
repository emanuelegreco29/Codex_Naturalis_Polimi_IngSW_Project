package it.polimi.ingsw.am43.View.GUI;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class OnGroundController extends SceneController{
    /**
     * The first card on the ground fxml component
     */
    @FXML
    private ImageView card1;
    /**
     * The button to rotate the first card fxml component
     */
    @FXML
    private Button rotate1;
    /**
     * The second card on the ground fxml component
     */
    @FXML
    private ImageView card2;
    /**
     * The button to rotate the second card fxml component
     */
    @FXML
    private Button rotate2;
    /**
     * The third card on the ground fxml component
     */
    @FXML
    private ImageView card3;
    /**
     * The button to rotate the third card fxml component
     */
    @FXML
    private Button rotate3;
    /**
     * The fourth card on the ground fxml component
     */
    @FXML
    private ImageView card4;
    /**
     * The button to rotate the fourth card fxml component
     */
    @FXML
    private Button rotate4;
    /**
     * The golden deck card fxml component
     */
    @FXML
    private ImageView goldenDeck;
    /**
     * The resource deck card fxml component
     */
    @FXML
    private ImageView resourceDeck;

    /**
     * The images of the cards on the ground
     */
    private Image[] onGroundImages;
    /**
     * The image of the golden deck
     */
    private Image goldenDeckImage;
    /**
     * The image of the resource deck
     */
    private Image resourceDeckImage;

    /**
     * An array of booleans that tells if the card is showing the front or the back
     */
    private boolean[] showingFront = {true,true,true, true};

    /**
     * The game scene controller
     */
    GameSceneController gameSceneController;

    /**
     * Initialize the on ground controller
     */
    @FXML
    public void initialize() {

        onGroundImages = new Image[10];
    }

    /**
     * Update the on ground cards
     * @param onGround the cards on the ground
     * @param goldenDeck the golden deck card
     * @param resourceDeck the resource deck card
     */
    public void updateOnGround(PlayableCard[] onGround, CardSide goldenDeck, CardSide resourceDeck){

        for (int i=0; i<4; i++){
            String path = onGround[i].getFrontPath();
            onGroundImages[2*i] = new Image(getClass().getResource(path).toExternalForm());
            path = onGround[i].getBackPath();
            onGroundImages[2*i+1] = new Image(getClass().getResource(path).toExternalForm());
        }
        card1.setImage(onGroundImages[0]);
        card2.setImage(onGroundImages[2]);
        card3.setImage(onGroundImages[4]);
        card4.setImage(onGroundImages[6]);

        if (goldenDeck!=null) {
            goldenDeckImage = new Image(getClass().getResource(goldenDeck.getPngPath()).toExternalForm());
            this.goldenDeck.setImage(goldenDeckImage);
        }
        if (resourceDeck!=null) {
            resourceDeckImage = new Image(getClass().getResource(resourceDeck.getPngPath()).toExternalForm());
            this.resourceDeck.setImage(resourceDeckImage);
        }


    }

    /**
     * Switch the side of the card
     * @param event the event that triggered the switch
     */
    @FXML
    private void switchSide(ActionEvent event){
        Button source = (Button) event.getSource();
        switch (source.getId()){
            case "rotate1" -> {
                if (showingFront[0]) card1.setImage(onGroundImages[1]);
                else card1.setImage(onGroundImages[0]);
                showingFront[0] = !showingFront[0];
            }
            case "rotate2" -> {
                if (showingFront[1]) card2.setImage(onGroundImages[3]);
                else card2.setImage(onGroundImages[2]);
                showingFront[1] = !showingFront[1];
            }
            case "rotate3" -> {
                if (showingFront[2]) card3.setImage(onGroundImages[5]);
                else card3.setImage(onGroundImages[4]);
                showingFront[2] = !showingFront[2];
            }
            case "rotate4" -> {
                if (showingFront[3]) card4.setImage(onGroundImages[7]);
                else card4.setImage(onGroundImages[6]);
                showingFront[3] = !showingFront[3];
            }
        }
    }

    //make the on ground cards clickable and send the chosen card to the server
    /**
     * Choose the drawn card
     * @param choice the choice of the card
     */
    @FXML
    private void chooseDrawnCard(int choice){

        gameSceneController.chooseDrawnCard(choice);
        //set null the click events
        card1.setOnMouseClicked(null);
        card2.setOnMouseClicked(null);
        card3.setOnMouseClicked(null);
        card4.setOnMouseClicked(null);
        goldenDeck.setOnMouseClicked(null);
        resourceDeck.setOnMouseClicked(null);
    }

    /**
     * Enable the draw card
     */
    public void enableDrawCard(){
        card1.setOnMouseClicked(mouseEvent -> chooseDrawnCard(0));
        card2.setOnMouseClicked(mouseEvent -> chooseDrawnCard(1));
        card3.setOnMouseClicked(mouseEvent -> chooseDrawnCard(2));
        card4.setOnMouseClicked(mouseEvent -> chooseDrawnCard(3));

        goldenDeck.setOnMouseClicked(mouseEvent -> chooseDrawnCard(5));
        resourceDeck.setOnMouseClicked(mouseEvent -> chooseDrawnCard(4));
    }

    /**
     * Set the game scene controller
     * @param gameSceneController the game scene controller
     */
    public void setGameSceneController(GameSceneController gameSceneController) {
        this.gameSceneController = gameSceneController;
    }
}
