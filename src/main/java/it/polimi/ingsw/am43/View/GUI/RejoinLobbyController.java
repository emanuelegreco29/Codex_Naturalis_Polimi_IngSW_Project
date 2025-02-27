package it.polimi.ingsw.am43.View.GUI;

import it.polimi.ingsw.am43.Network.Messages.controllerMessages.SCMsgPlayerRejoins;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This controller manages the lobby for the loading of a previous game.
 */
public class RejoinLobbyController extends SceneController{

    /**
     * The choice box for selecting the available usernames
     */
    @FXML
    private ChoiceBox<String> availableUsernames;
    /**
     * the loading roll
     */
    @FXML
    private ProgressIndicator loading;
    /**
     * The rejoin button
     */
    @FXML
    private Button rejoinButton;
    /**
     * The error label to indicate a username has been already selected
     */
    @FXML
    private Label error;

    /**
     * The player ID
     */
    private int playerID;
    /**
     * The selected username
     */
    private String user;
    /**
     * A map containing the valid usernames and linking them to the relative index
     */
    private Map<String, Integer> validUsernames = new HashMap<>();

    /**
     * FXML initialize method
     */
    @FXML
    public void initialize(){

        rejoinButton.setDisable(true);

        loading.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        loading.setVisible(false);

        error.setVisible(false);

    }

    /**
     * The method linked to the rejoin button
     */
    @FXML
    public void rejoin(){
        playerID = validUsernames.get(user);
        gui.setPlayerID(playerID);
        gui.writeNewMessage(new SCMsgPlayerRejoins(String.valueOf(playerID)));
        loading.setVisible(true);
    }

    /**
     * This method sets the content of the choice box of the usernames.
     * @param invalid list of invalid usernames
     * @param players list of available usernames
     */
    public void setAvailableUsernames(List<Integer> invalid, String[] players){
        rejoinButton.setDisable(true);

        if (!invalid.isEmpty()) {
            error.setVisible(true);
            loading.setVisible(false);
        }

        validUsernames.clear();
        for (int i=0; i<players.length; i++){
            if(!invalid.contains(i)){
                validUsernames.put(players[i],i);
            }
        }

        this.availableUsernames.getItems().clear();
        this.availableUsernames.getItems().addAll(validUsernames.keySet());
        this.availableUsernames.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            rejoinButton.setDisable(false);
            user = newValue;
        });
    }

    /**
     * Method that builds the Game scene previously created
     * @param gui the instance of the GUI class
     * @return the generated gameSceneController to the GUI class
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
            controller.setPlayerID(playerID);
            controller.setUsername(user);
            controller.setOnCloseConfirm(stage);
            stage.show();

            return controller;

        } catch (IOException e) {
            System.err.println("Errore nel caricamento del file FXML");
            throw new RuntimeException(e);
        }
    }
}
