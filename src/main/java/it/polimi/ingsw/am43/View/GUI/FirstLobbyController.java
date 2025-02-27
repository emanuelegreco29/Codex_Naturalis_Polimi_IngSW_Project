package it.polimi.ingsw.am43.View.GUI;

import it.polimi.ingsw.am43.Network.Messages.controllerMessages.SCMsgFirstPlayerJoins;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.*;

public class FirstLobbyController extends SceneController{

    /**
     * Field to insert the username
     */
    @FXML
    private TextField username;

    /**
     * Spinner to select the number of players
     */
    @FXML
    private Spinner<Integer> numPlayers;

    /**
     * Group and radio buttons for selecting the pawn color
     */
    @FXML
    private ToggleGroup pawnColors;
    @FXML
    private Button startGame;
    @FXML
    private RadioButton red;
    @FXML
    private RadioButton blue;
    @FXML
    private RadioButton green;
    @FXML
    private RadioButton yellow;

    /**
     * Selected colours
     */
    private boolean colourSelected = false;

    /**
     * Loading roll
     */
    @FXML
    private ProgressIndicator loading;

    /**
     * Label for the loading roll
     */
    @FXML
    private Label loadingText;

    /**
     * Map for the colours
     */
    private final Map<String, Integer> colourMap = new HashMap<>();

    /**
     * The username
     */
    private String user;

    /**
     * Initializes the controller
     */
    @FXML
    public void initialize(){

        username.textProperty().addListener((observable, oldValue, newValue) -> {
            checkValidUser(newValue);
        });

        SpinnerValueFactory<Integer> range = new SpinnerValueFactory.IntegerSpinnerValueFactory(2,4,1);
        numPlayers.setValueFactory(range);

        startGame.setDisable(true);
        loading.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        loading.setVisible(false);
        loadingText.setVisible(false);

        pawnColors = new ToggleGroup();
        red.setToggleGroup(pawnColors);
        blue.setToggleGroup(pawnColors);
        green.setToggleGroup(pawnColors);
        yellow.setToggleGroup(pawnColors);

        colourMap.put("Red",0);
        colourMap.put("Blue",1);
        colourMap.put("Green",2);
        colourMap.put("Yellow",3);

    }

    /**
     * Gets the colour chosen by the player
     * @return the chosen colour
     */
    private int getChosenColour(){
        RadioButton selected = (RadioButton) pawnColors.getSelectedToggle();
        return colourMap.get(selected.getText());
    }

    /**
     * Sets the colour selected and checks if the username is valid
     */
    @FXML
    public void setColourSelected(){
        colourSelected = true;
        checkValidUser(username.getText());
    }

    /**
     * Checks if the username is valid
     * @param username the username
     */
    private void checkValidUser(String username){
        if (username.isEmpty() || username.contains(" ") || username.contains("\n") || username.contains("\r")){
            this.username.getStyleClass().add("text-field-error");
            startGame.setDisable(true);
        } else {
            this.username.getStyleClass().remove("text-field-error");
            if (colourSelected) startGame.setDisable(false);
        }
    }

    /**
     * Starts the game
     * @param event the event
     */
    @FXML
    public void startGame(ActionEvent event){

        gui.writeNewMessage(new SCMsgFirstPlayerJoins(username.getText(), getChosenColour(), numPlayers.getValue()));
        this.user = username.getText();
        loading.setVisible(true);
        loadingText.setVisible(true);
        startGame.setDisable(true);
    }

    /**
     * Builds the game scene
     * @param gui the GUI
     * @return the controller of the game scene
     */
    public GameSceneController buildGameScene(GUI gui){

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GameScene.fxml"));
            root = loader.load();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Codex Naturalis - "+this.user);
            stage.centerOnScreen();

            root.lookupAll(".split-pane-divider").forEach(div -> {
                div.addEventFilter(MouseEvent.MOUSE_PRESSED, MouseEvent::consume);
                div.addEventFilter(MouseEvent.MOUSE_DRAGGED, MouseEvent::consume);
                div.addEventFilter(MouseEvent.MOUSE_RELEASED, MouseEvent::consume);
            });

            GameSceneController controller = loader.getController();
            controller.setGui(gui);
            controller.setOnCloseConfirm(stage);
            stage.show();

            return controller;

        } catch (IOException e) {
            System.err.println("Errore nel caricamento del file FXML");
            throw new RuntimeException(e);
        }
    }
}
