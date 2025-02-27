package it.polimi.ingsw.am43.View.GUI;

import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardController extends SceneController{
    /**
     * The scoreboard pane
     */
    @FXML
    private AnchorPane scoreboardPane;
    /**
     * The pinions ImageView the players
     */
    @FXML
    private ImageView points0;
    @FXML
    private ImageView points1;
    @FXML
    private ImageView points2;
    @FXML
    private ImageView points3;
    /**
     * The pinions of the players
     */
    private ImageView[] pinions = {points0,points1,points2,points3};
    /**
     * The game scene controller
     */
    private GameSceneController gameSceneController;

    /**
     * Set the game scene controller
     * @param gameSceneController the game scene controller
     */
    public void setGameSceneController(GameSceneController gameSceneController) {
        this.gameSceneController = gameSceneController;
    }

    /**
     * The positions of the pinions on the scoreboard for every score
     */
    private int[][] scoreboardPos = {
            {49,437}, {107,437}, {164,437},
            {193,384}, {136,384}, {78,384}, {20,384},
            {20,332}, {78,332}, {136,332}, {193,332},
            {193,278}, {136,278}, {78,278}, {20,278},
            {20,226}, {78,226}, {136,226}, {193,226},
            {193,173}, {107,150}, {20,173},
            {20,120}, {20,68}, {54,25},
            {107,15}, {160,25}, {193,68}, {193,120}, {107,81}
    };

    /**
     * Initialize the pinions on the scoreboard
     * @param colors the colors of the players
     */
    public void initializePinions(PawnColor[] colors){

        for (int i=0; i<colors.length;i++){
            String path = colors[i].toGUIColor();
            pinions[i] = new ImageView(new Image(getClass().getResource(path).toExternalForm()));
            pinions[i].setPreserveRatio(true);
            pinions[i].setFitWidth(46);
            pinions[i].setLayoutX(scoreboardPos[0][0]);
            pinions[i].setLayoutY(scoreboardPos[0][1]-i*5);
            int finalI = i;
            pinions[i].setOnMouseClicked(mouseEvent -> setDeployedPane(finalI));

            scoreboardPane.getChildren().add(pinions[i]);
        }
    }

    /**
     * Redraw the pinions on the scoreboard
     * @param newScores the new scores
     */
    public void redrawPinions(int[] newScores){
        for (int i=0; i<newScores.length;i++){
            if (newScores[i]>29){
                newScores[i] = 29;
            }
        }

        Map<Integer, Integer> occupied = new HashMap<>();

        for (int i=0; i<newScores.length;i++){
            int presences = occupied.getOrDefault(newScores[i],0);
            pinions[i].setLayoutX(scoreboardPos[newScores[i]][0]);
            pinions[i].setLayoutY(scoreboardPos[newScores[i]][1]-5*presences);

            occupied.put(newScores[i],presences+1);
        }
    }

    /**
     * Switch to the deployed pane of the player
     * @param playerID the player ID
     */
    private void setDeployedPane(int playerID){
        gameSceneController.setDeployedPane(playerID);
        gameSceneController.simpleSwitchToDeployed();
    }
}
