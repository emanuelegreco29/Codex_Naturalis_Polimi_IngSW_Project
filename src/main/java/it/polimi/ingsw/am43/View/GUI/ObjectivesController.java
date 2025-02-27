package it.polimi.ingsw.am43.View.GUI;

import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ObjectivesController extends SceneController{
    /**
     * The first common objective card fxml component
     */
    @FXML
    private ImageView commonObj1;
    /**
     * The second common objective card fxml component
     */
    @FXML
    private ImageView commonObj2;
    /**
     * The personal objective card fxml component
     */
    @FXML
    private ImageView personalObj;

    /**
     * The images of the objectives
     */
    private Image[] images;

    /**
     * The button to display the deployed cards
     */
    @FXML
    private Button deployedButton;
    /**
     * The button to display the on ground cards
     */
    @FXML
    private Button onGroundButton;

    /**
     * The game scene controller
     */
    private GameSceneController gameSceneController;

    /**
     * Enable the deployed and on ground buttons
     */
    public void enableCommands() {
        onGroundButton.setDisable(false);
        deployedButton.setDisable(false);
    }

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize(){
        onGroundButton.setDisable(true);
        deployedButton.setDisable(true);
        images = new Image[3];
    }

    /**
     * Set the common objectives
     * @param commonObjectives the common objectives images
     */
    public void setCommonObjectives(ObjectiveCard[] commonObjectives){
        String path;
        for (int i=0; i<2; i++){
            path = commonObjectives[i].getFrontPath();
            images[i] = new Image(getClass().getResource(path).toExternalForm());
        }

        commonObj1.setImage(images[0]);
        commonObj2.setImage(images[1]);
    }

    /**
     * Set the personal objective
     * @param personalObjective the personal objective image
     */
    public void setPersonalObjective(ObjectiveCard personalObjective){
        String path = personalObjective.getFrontPath();
        images[2] = new Image(getClass().getResource(path).toExternalForm());

        personalObj.setImage(images[2]);
    }

    /**
     * Set the game scene controller
     * @param gameSceneController the game scene controller
     */
    public void setGameSceneController(GameSceneController gameSceneController){
        this.gameSceneController = gameSceneController;
    }

    /**
     * Display the deployed cards
     */
    @FXML
    public void onDeployedButtonClicked(){
        gameSceneController.displayDeployed();
    }

    /**
     * Display the on ground cards
     */
    @FXML
    public void onOnGroundButtonClicked(){
        gameSceneController.displayOnGround();
    }
}
