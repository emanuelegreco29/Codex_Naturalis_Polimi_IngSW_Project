package it.polimi.ingsw.am43.View.GUI;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class SceneController {

    protected Stage stage;
    protected Scene scene;
    protected Parent root;
    protected GUI gui;

    @FXML
    public void initialize(){}

    /**
     * Set the GUI
     * @param gui the GUI
     */
    public void setGui(GUI gui) {
        this.gui = gui;
    }

    /**
     * Set the stage
     * @param stage the stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Set the scene
     * @param scene the scene
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    }

}
