package it.polimi.ingsw.am43.View.GUI;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;


public class EndGameController extends SceneController{
    /**
     * The first user label
     */
    @FXML
    private Label user0;

    /**
     * The second user label
     */
    @FXML
    private Label user1;

    /**
     * The third user label
     */
    @FXML
    private Label user2;

    /**
     * The fourth user label
     */
    @FXML
    private Label user3;

    /**
     * The first objective counter label
     */
    @FXML
    private Label private0;

    /**
     * The second objective counter label
     */
    @FXML
    private Label private1;

    /**
     * The third objective counter label
     */
    @FXML
    private Label private2;

    /**
     * The fourth objective counter label
     */
    @FXML
    private Label private3;

    /**
     * The first points label
     */
    @FXML
    private Label points0;

    /**
     * The second points label
     */
    @FXML
    private Label points1;

    /**
     * The third points label
     */
    @FXML
    private Label points2;

    /**
     * The fourth points label
     */
    @FXML
    private Label points3;

    /**
     * The winner label
     */
    @FXML
    private Label winnerLabel;

    /**
     * The line to show when there are at least 3 players
     */
    @FXML
    private AnchorPane line3;

    /**
     * The line to show when there are 4 players
     */
    @FXML
    private AnchorPane line4;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
    }

    /**
     * Show the scores of the players
     * @param scores the scores of the players
     * @param usernames the usernames of the players
     */
    public void showScores (int[][] scores, String[] usernames){
        //scores[i][0] = player index
        //scores[i][1] = game points
        //scores[i][2] = objective counter
        user0.setText(usernames[scores[0][0]]);
        private0.setText(String.valueOf(scores[0][2]));
        points0.setText(String.valueOf(scores[0][1]));
        user1.setText(usernames[scores[1][0]]);
        private1.setText(String.valueOf(scores[1][2]));
        points1.setText(String.valueOf(scores[1][1]));
        switch(scores.length){
            case 3:
                user2.setText(usernames[scores[2][0]]);
                private2.setText(String.valueOf(scores[2][2]));
                points2.setText(String.valueOf(scores[2][1]));
                line4.setVisible(false);
                break;
            case 4:
                user2.setText(usernames[scores[2][0]]);
                private2.setText(String.valueOf(scores[2][2]));
                points2.setText(String.valueOf(scores[2][1]));
                user3.setText(usernames[scores[3][0]]);
                private3.setText(String.valueOf(scores[3][2]));
                points3.setText(String.valueOf(scores[3][1]));
                break;
            default:
                line3.setVisible(false);
                line4.setVisible(false);
                break;
        }
        winnerLabel.setText("The winner is " + usernames[scores[0][0]] + "!");

    }




}
