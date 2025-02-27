package it.polimi.ingsw.am43.View.GUI;

import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


public class InHandController extends SceneController{
    /**
     * The anchor pane of the bottom panel
     */
    @FXML
    private AnchorPane handAnchor;
    /**
     * The menu button to switch between deployed cards of each player
     */
    @FXML
    private MenuButton deployedSwitch;
    /**
     * The menu items of the deployed switch
     */
    private MenuItem[] menuItems;

    /**
     * The first card in hand fxml component
     */
    @FXML
    private ImageView card1;
    /**
     * The button to rotate the first card fxml component
     */
    @FXML
    private Button rotate1;
    /**
     * The second card in hand fxml component
     */
    @FXML
    private ImageView card2;
    /**
     * The button to rotate the second card fxml component
     */
    @FXML
    private Button rotate2;
    /**
     * The third card in hand fxml component
     */
    @FXML
    private ImageView card3;
    /**
     * The button to rotate the third card fxml component
     */
    @FXML
    private Button rotate3;
    /**
     * The label to display messages
     */
    @FXML
    private Label label;
    /**
     * The boolean array to check if the cards are showing the front or the back
     */
    private boolean[] showingFront = {true,true,true};
    /**
     * The image array of the cards
     */
    private Image[] images;
    /**
     * The game scene controller
     */
    private GameSceneController gameSceneController;
    /**
     * The color adjust to make the cards black and white
     */
    ColorAdjust blackAndWhite;
    /**
     * The glow effect to highlight the selected card
     */
    Glow glow = new Glow();
    /**
     * The boolean to check if the cards are selectable
     */
    private Boolean cardSelection = false;
    /**
     * The boolean array to check if the cards are placeable
     */
    private Boolean[] placeableCards = {true, true, true, true, true, true};
    /**
     * The selected card
     */
    private int selected;
    /**
     * The text flow to display the chat
     */
    @FXML
    private TextFlow chatFlow;
    /**
     * The scroll pane of the chat
     */
    @FXML
    private ScrollPane scrollFlow;
    /**
     * The text field to write the message
     */
    @FXML
    private TextField message;
    /**
     * The choice box to select the receiver of the message
     */
    @FXML
    private ChoiceBox<String> sendTo;
    /**
     * The pawn colors of the users
     */
    private PawnColor[] usersColors;
    /**
     * The image view of the send button
     */
    @FXML
    private ImageView sendButton;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {

        images = new Image[6];
        blackAndWhite = new ColorAdjust();
        blackAndWhite.setSaturation(-1);
        glow.setLevel(0.8);
        selected=-1;

        sendTo.getItems().add("ALL");
        sendTo.setValue("ALL");

        ImageView icon = new ImageView(new Image(getClass().getResource("Images/wizard.jpg").toExternalForm()));
        icon.setPreserveRatio(true);
        icon.setFitWidth(30);
        deployedSwitch = new MenuButton("", icon);
        deployedSwitch.setLayoutX(10);
        deployedSwitch.setLayoutY(10);
        deployedSwitch.setPrefSize(30,30);
        deployedSwitch.setVisible(false);

        handAnchor.getChildren().add(deployedSwitch);
    }

    /**
     * Initialize the deployed switch
     * @param usernames the usernames of the players
     * @param colors the colors of the players
     */
    public void initializeDeployedSwitch(String[] usernames, PawnColor[] colors){

        menuItems = new MenuItem[usernames.length];

        for (int i=0; i<colors.length;i++){
            ImageView icon = new ImageView(new Image(getClass().getResource(colors[i].toGUIColor()).toExternalForm()));
            icon.setPreserveRatio(true);
            icon.setFitWidth(13);
            menuItems[i] = new MenuItem(usernames[i], icon);

            int finalI = i;
            menuItems[i].setOnAction(event -> setDeployedPane(finalI));

            deployedSwitch.getItems().add(menuItems[i]);
        }

        deployedSwitch.setVisible(true);
    }

    private void setDeployedPane(int playerID){
        gameSceneController.setDeployedPane(playerID);
        gameSceneController.simpleSwitchToDeployed();
    }

    public void displayInHand(PlayableCard[] inHand){
        showingFront = new boolean[]{true,true,true};
        for (int i=0; i<3; i++){
            images[2*i] = new Image(getClass().getResource(inHand[i].getFrontPath()).toExternalForm());
            images[2*i+1] = new Image(getClass().getResource(inHand[i].getBackPath()).toExternalForm());
        }

        card1.setImage(images[0]);
        card2.setImage(images[2]);
        card3.setImage(images[4]);
    }

    /**
     * Switch the side of the card
     * @param event the event
     */
    @FXML
    private void switchSide(ActionEvent event){
        Button source = (Button) event.getSource();

        //switch sides of the card
        switch (source.getId()){
            case "rotate1" -> {
                if (showingFront[0]) card1.setImage(images[1]);
                else card1.setImage(images[0]);
                showingFront[0] = !showingFront[0];
            }
            case "rotate2" -> {
                if (showingFront[1]) card2.setImage(images[3]);
                else card2.setImage(images[2]);
                showingFront[1] = !showingFront[1];
            }
            case "rotate3" -> {
                if (showingFront[2]) card3.setImage(images[5]);
                else card3.setImage(images[4]);
                showingFront[2] = !showingFront[2];
            }
        }

        //if the card is selectable, and the events
        if (cardSelection){
            switch (source.getId()){
                case "rotate1" -> {
                    if ((showingFront[0] && placeableCards[0]) || (!showingFront[0]&& placeableCards[1])){
                        card1.setEffect(null);
                        card1.setOnMouseClicked(mouseEvent -> clickInHand(0));
                        card1.setOnMouseEntered(e -> card1.setEffect(glow));
                        card1.setOnMouseExited(e -> card1.setEffect(null));
                    }
                    else{
                        card1.setEffect(blackAndWhite);
                        card1.setOnMouseClicked(null);
                        card1.setOnMouseEntered(null);
                        card1.setOnMouseExited(null);
                    }
                }
                case "rotate2" -> {
                    if ((showingFront[1] && placeableCards[2]) || (!showingFront[1]&& placeableCards[3])) {
                        card2.setEffect(null);
                        card2.setOnMouseClicked(mouseEvent -> clickInHand(1));
                        card2.setOnMouseEntered(e -> card2.setEffect(glow));
                        card2.setOnMouseExited(e -> card2.setEffect(null));
                    }
                    else {
                        card2.setEffect(blackAndWhite);
                        card2.setOnMouseClicked(null);
                        card2.setOnMouseEntered(null);
                        card2.setOnMouseExited(null);
                    }

                }
                case "rotate3" -> {
                    if ((showingFront[2] && placeableCards[4]) || (!showingFront[2]&& placeableCards[5])) {
                        card3.setEffect(null);
                        card3.setOnMouseClicked(mouseEvent -> clickInHand(2));
                        card3.setOnMouseEntered(e -> card3.setEffect(glow));
                        card3.setOnMouseExited(e -> card3.setEffect(null));
                    }
                    else {
                        card3.setEffect(blackAndWhite);
                        card3.setOnMouseClicked(null);
                        card3.setOnMouseEntered(null);
                        card3.setOnMouseExited(null);
                    }
                }
            }
        }

        //mark only the selected cardside
        if (selected!=-1 && !cardSelection){
            //if the selected cardside is visible, add the class
            if (showingFront[selected/2] && selected%2==0 || !showingFront[selected/2] && selected%2==1){
                switch (selected/2){
                    case 0 -> {
                        card1.setEffect(glow);
                        card1.setOnMouseClicked(mouseEvent -> deselect());
                    }
                    case 1 -> {
                        card2.setEffect(glow);
                        card2.setOnMouseClicked(mouseEvent -> deselect());
                    }
                    case 2 -> {
                        card3.setEffect(glow);
                        card3.setOnMouseClicked(mouseEvent -> deselect());
                    }
                }
            } else {
                switch (selected/2){
                    case 0 -> {
                        card1.setEffect(null);
                        card1.setOnMouseClicked(null);
                    }
                    case 1 -> {
                        card2.setEffect(null);
                        card2.setOnMouseClicked(null);
                    }
                    case 2 -> {
                        card3.setEffect(null);
                        card3.setOnMouseClicked(null);
                    }
                }
            }
        }

    }

    /**
     * Deselect the selected card
     */
    public void deselect(){
        clearAllEffects();
        //TODO block deployed selection
        gameSceneController.cancelDeployed();
        askNewInHandSelection();
    }

    /**
     * Allow the selection of the cards
     * @param placeableCards the placeable cards
     */
    public void allowPlaceableInHandSelection(Boolean[] placeableCards) {
        this.placeableCards = placeableCards;
        askNewInHandSelection();
    }

    /**
     * Set the game scene controller
     * @param gameSceneController the game scene controller
     */
    public void setGameSceneController(GameSceneController gameSceneController) {
        this.gameSceneController = gameSceneController;
    }

    /**
     * Click on the card in hand
     * @param index the index of the card
     */
    private void clickInHand(int index){
        clearAllEffects();
        cardSelection= false;
        selected= 2*index+(showingFront[index] ? 0 : 1);
        switch(index){
            case 0 -> {
                card1.setEffect(glow);
                card1.setOnMouseClicked(mouseEvent -> deselect());
            }
            case 1 -> {
                card2.setEffect(glow);
                card2.setOnMouseClicked(mouseEvent -> deselect());
            }
            case 2 -> {
                card3.setEffect(glow);
                card3.setOnMouseClicked(mouseEvent -> deselect());
            }
        }
        gameSceneController.chooseInHandCard(2*index + (showingFront[index] ? 0 : 1));
    }

    /**
     * Clear all the effects of the cards
     */
    public void clearAllEffects(){
        card1.setOnMouseClicked(null);
        card2.setOnMouseClicked(null);
        card3.setOnMouseClicked(null);
        card1.setEffect(null);
        card2.setEffect(null);
        card3.setEffect(null);
        card1.setOnMouseEntered(null);
        card2.setOnMouseEntered(null);
        card3.setOnMouseEntered(null);
        card1.setOnMouseExited(null);
        card2.setOnMouseExited(null);
        card3.setOnMouseExited(null);
    }

    /**
     * Cancel the selected card
     */
    public void cancelSelected() {
        this.selected = -1;
    }

    /**
     * Print a message to the label for the user to read it
     * @param message the message
     */
    public void printToGui(String message) {
        label.setText(message);
        label.setLayoutX((handAnchor.getWidth() - label.getWidth()) / 2);
    }

    /**
     * Ask the user to select a card from the hand
     */
    public void askNewInHandSelection() {
        cardSelection = true;
        selected=-1;
        clearAllEffects();

        for (int i=0; i<6; i+=2){
            if(placeableCards[i] && showingFront[i/2] || placeableCards[i+1] && !showingFront[i/2]){
                switch (i){
                    case 0 ->{
                        card1.setOnMouseClicked(mouseEvent -> clickInHand(0));
                        card1.setEffect(null);
                        card1.setOnMouseEntered(e -> card1.setEffect(glow));
                        card1.setOnMouseExited(e -> card1.setEffect(null));
                    }
                    case 2 -> {
                        card2.setOnMouseClicked(mouseEvent -> clickInHand(1));
                        card2.setEffect(null);
                        card2.setOnMouseEntered(e -> card2.setEffect(glow));
                        card2.setOnMouseExited(e -> card2.setEffect(null));
                    }
                    case 4 -> {
                        card3.setOnMouseClicked(mouseEvent -> clickInHand(2));
                        card3.setEffect(null);
                        card3.setOnMouseEntered(e -> card3.setEffect(glow));
                        card3.setOnMouseExited(e -> card3.setEffect(null));
                    }
                }
            } else{
                switch (i){
                    case 0 ->{
                        card1.setOnMouseClicked(null);
                        card1.setEffect(blackAndWhite);
                        card1.setOnMouseEntered(null);
                        card1.setOnMouseExited(null);

                    }
                    case 2 -> {
                        card2.setOnMouseClicked(null);
                        card2.setEffect(blackAndWhite);
                        card2.setOnMouseEntered(null);
                        card2.setOnMouseExited(null);
                    }
                    case 4 -> {
                        card3.setOnMouseClicked(null);
                        card3.setEffect(blackAndWhite);
                        card3.setOnMouseEntered(null);
                        card3.setOnMouseExited(null);
                    }
                }
            }


        }
    }

    /**
     * Initialize the chat
     * @param usernames the usernames of the players
     * @param usersColors the colors of the users
     */
    public void initializeChat(String[] usernames, PawnColor[] usersColors){
        this.usersColors = usersColors;
        sendTo.getItems().addAll(usernames);
        sendTo.setDisable(false);
        sendButton.setDisable(false);
        message.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) sendMessage();
        });
    }

    /**
     * Update the chat text flow
     * @param newMessage the new message
     * @param sender the sender
     * @param isPriv if the message is private
     */
    public void updateChatTextFlow(String newMessage, String sender, boolean isPriv){
        String color;
        if (sender.equals("YOU") || sender.startsWith("YOU to ")) color = "BLACK";
        else color = usersColors[sendTo.getItems().indexOf(sender)-1].toString();
        Text incipit = new Text(isPriv ? "[PRIVATE] " : "> ");
        Text s = new Text(sender);
        s.setStyle("-fx-font-weight: bold; -fx-fill: " + color + ";");
        Text message = new Text(": "+newMessage+"\n");
        chatFlow.getChildren().addAll(incipit, s, message);
        scrollFlow.setVvalue(scrollFlow.getVmax());
    }

    /**
     * Send a message
     */
    @FXML
    public void sendMessage(){
        if (!message.getText().isEmpty()) {
            String receiver = sendTo.getValue();
            gameSceneController.sendChatMessage(receiver, message.getText());
            if (receiver.equals("ALL")) updateChatTextFlow(message.getText(), "YOU", false);
            else updateChatTextFlow(message.getText(), "YOU to " + receiver, true);
            message.setText("");
        }
    }
}
