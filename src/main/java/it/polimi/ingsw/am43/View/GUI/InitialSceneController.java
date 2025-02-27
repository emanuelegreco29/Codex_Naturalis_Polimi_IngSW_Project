package it.polimi.ingsw.am43.View.GUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class InitialSceneController extends SceneController{
    /**
     * The stage
     */
    private Stage stage;

    /**
     * The scene
     */
    private Scene scene;

    /**
     * The root
     */
    private Parent root;


    public InitialSceneController() {}

    /**
     * Switch to the first lobby
     * @param event the key event
     * @param gui the gui
     * @return the first lobby controller
     */
    public FirstLobbyController switchToFirstLobby(KeyEvent event, GUI gui) {

        this.gui = gui;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FirstLobby.fxml"));
            root = loader.load();
            stage = (Stage) ((Scene) event.getSource()).getWindow();

            scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

            FirstLobbyController controller = loader.getController();
            controller.setGui(gui);
            controller.setStage(stage);

            return controller;

        } catch (IOException e) {
            System.err.println("Errore nel caricamento del file FXML");
            throw new RuntimeException(e);
        }

    }

    /**
     * Switch to the general lobby
     * @param event the key event
     * @param gui the gui
     * @param colours the colours already chosen
     * @param users the users already in the lobby
     * @return the general lobby controller
     */
    public GeneralLobbyController switchToGeneralLobby(KeyEvent event, GUI gui, int[] colours, String[] users) {

        this.gui = gui;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GeneralLobby.fxml"));
            root = loader.load();
            stage = (Stage) ((Scene) event.getSource()).getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

            GeneralLobbyController controller = loader.getController();
            controller.setGui(gui);
            controller.setStage(stage);
            controller.setEnabledColoursAndUsernames(colours, users);

            return controller;

        } catch (IOException e) {
            System.err.println("Errore nel caricamento del file FXML");
            throw new RuntimeException(e);
        }

    }

    /**
     * Switch to the rejoin lobby
     * @param event the key event
     * @param gui the gui
     * @param usernames the usernames of the players
     * @return the rejoin lobby controller
     */
    public RejoinLobbyController switchToRejoinLobby(KeyEvent event, GUI gui, String[] usernames){

        this.gui = gui;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RejoinLobby.fxml"));
            root = loader.load();
            stage = (Stage) ((Scene) event.getSource()).getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

            RejoinLobbyController controller = loader.getController();
            controller.setGui(gui);
            controller.setStage(stage);
            controller.setAvailableUsernames(new ArrayList<>(), usernames);

            return controller;

        } catch (IOException e) {
            System.err.println("Errore nel caricamento del file FXML");
            throw new RuntimeException(e);
        }
    }
}
