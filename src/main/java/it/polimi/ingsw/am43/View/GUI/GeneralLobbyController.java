package it.polimi.ingsw.am43.View.GUI;

import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Network.Messages.controllerMessages.SCMsgGeneralPlayerJoins;
import it.polimi.ingsw.am43.Network.Messages.controllerMessages.SCMsgFirstStillJoining;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the lobby window dedicated to all the players wanting to join after the first one.
 */
public class GeneralLobbyController extends SceneController{

    /**
     * Field to insert the username
     */
    @FXML
    private TextField username;

    /**
     * Group and radio buttons for selecting the pawn color among the remaining.
     */
    @FXML
    private ToggleGroup pawnColors;
    @FXML
    private Button joinGame;
    @FXML
    private RadioButton red;
    @FXML
    private RadioButton blue;
    @FXML
    private RadioButton green;
    @FXML
    private RadioButton yellow;

    /**
     * Selected colours and already chosen usernames
     */
    private boolean colourSelected = false;
    private List<String> usedUsernames;

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

    private final Map<String, Integer> colourMap = new HashMap<>();
    private String user;

    /**
     * FXML initialize method to set up the window.
     */
    @FXML
    public void initialize(){

        username.textProperty().addListener((observable, oldValue, newValue) -> {
            checkValidUser(newValue);
        });

        pawnColors = new ToggleGroup();
        red.setToggleGroup(pawnColors);
        red.setDisable(true);
        blue.setToggleGroup(pawnColors);
        blue.setDisable(true);
        green.setToggleGroup(pawnColors);
        green.setDisable(true);
        yellow.setToggleGroup(pawnColors);
        yellow.setDisable(true);

        username.setDisable(true);
        joinGame.setDisable(true);

        colourMap.put("Red",0);
        colourMap.put("Blue",1);
        colourMap.put("Green",2);
        colourMap.put("Yellow",3);

    }

    /**
     * @return the corresponding index to the selected color
     */
    private int getChosenColour(){
        RadioButton selected = (RadioButton) pawnColors.getSelectedToggle();
        return colourMap.get(selected.getText());
    }

    /**
     * It sets the selected colour while checking if the username is available.
     */
    @FXML
    public void setColourSelected(){
        colourSelected = true;
        checkValidUser(username.getText());
    }

    /**
     * It checks whether a
     * @param username has been already picked by somebody else
     */
    private void checkValidUser(String username){
        if (username.isEmpty() || username.contains(" ") || username.contains("\n") || username.contains("\r") || usedUsernames.contains(username)){
            this.username.getStyleClass().add("text-field-error");
            joinGame.setDisable(true);
        } else {
            this.username.getStyleClass().remove("text-field-error");
            if (colourSelected) joinGame.setDisable(false);
        }
    }

    /**
     * It receives
     * @param colours the available pawn colors and
     * @param usernames the already chosen usernames to set up the lobby
     */
    public void setEnabledColoursAndUsernames(int[] colours, String[] usernames){

        if (usernames!=null){

            usedUsernames = Arrays.asList(usernames);
            username.setDisable(false);

            loading.setVisible(false);
            loadingText.setVisible(false);
            loadingText.setText("Waiting for the other players to join ...");

            red.setDisable(true);
            blue.setDisable(true);
            green.setDisable(true);
            yellow.setDisable(true);
            for(int c : colours){
                switch (PawnColor.values()[c]){
                    case RED -> red.setDisable(false);
                    case BLUE -> blue.setDisable(false);
                    case GREEN -> green.setDisable(false);
                    case YELLOW -> yellow.setDisable(false);
                }
            }

        } else {
            try {
                Thread.sleep(5000);
                gui.writeNewMessage(new SCMsgFirstStillJoining());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * It's associated to the main button to send all the inserted information to the server and join the game
     */
    @FXML
    public void joinGame(){

        gui.writeNewMessage(new SCMsgGeneralPlayerJoins(username.getText(), getChosenColour()));
        this.user = username.getText();
        loading.setVisible(true);
        loadingText.setVisible(true);
        joinGame.setDisable(true);
    }

    /**
     * It sets up the game scene and a
     * @param gui instance of the GUI and
     * @return the generated controller to the GUI class
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
