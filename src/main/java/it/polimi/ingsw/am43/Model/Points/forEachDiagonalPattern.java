package it.polimi.ingsw.am43.Model.Points;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Model.Enum.Kingdom;
import it.polimi.ingsw.am43.Model.Player;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents the rule present in some {@link ObjectiveCard}s.
 * This rule states that, if the {@link Player} has placed 3 {@link CardSide}s of a
 * specific {@link Kingdom}, in a diagonal way, he/she gets 2 points.
 * The direction of the diagonal and the {@link Kingdom} are specified by the {@link ObjectiveCard} itself.
 */
public class forEachDiagonalPattern implements PointsRule, Serializable {

    @Serial
    private static final long serialVersionUID = -4321011787399695677L;

    /**
     * The points earned by the {@link Player} for each pattern present in his/her placements matrix
     */
    private final int points;

    /**
     * The {@link Kingdom} of the {@link CardSide}s that can be part of the patter,
     * specified by the {@link ObjectiveCard}.
     */
    private final Kingdom kingdom;

    /**
     *         |==|
     *      |==|
     *   |==|
     *   isTopLeft = false
     *   |==|
     *      |==|
     *         |==|
     *   isTopLeft = true
     * */
    private final boolean isTopLeft;


    /**
     * Constructor for the class
     *
     * @param points the points earned by the {@link Player} for each pattern present in his/her placements matrix
     * @param kingdom the {@link Kingdom} of the {@link CardSide}s that can be part of the patter,
     *                specified by the {@link ObjectiveCard}.
     * @param isTopLeft whether the pattern is on the top left or not
     */
    public forEachDiagonalPattern(int points, Kingdom kingdom, boolean isTopLeft) {
        this.points = points;
        this.kingdom = kingdom;
        this.isTopLeft = isTopLeft;
    }

    /**
     * A function that returns the points of the {@link CardSide}.
     *
     * @return the points computed for the {@link Player}
     */
    @Override
    public int getPoints() {
        return points;
    }

    /**
     * A function that returns the direction of the card.
     */
    public boolean getIsTopLeft() {
        return isTopLeft;
    }

    /**
     * A function that returns the {@link Kingdom} of the card.
     *
     * @return            {@link Kingdom} of the card
     */
    public Kingdom getKingdom() {
        return kingdom;
    }

    /**
     * Computes the points for a {@link Player} based on the placements and deployed {@link CardSide}s,
     * by checking if there are certain diagonal patterns.
     *
     * @param  player   the {@link Player} for whom to compute the points
     * @return          the total points computed for the {@link Player}
     */
    @Override
    public int computePoints(Player player) {
        // Retrieves the matrix of placements and all the placed cards
        int[][] placements = player.getPlacements();
        ArrayList<CardSide> deployed = player.getDeployed();
        int SCORE = 0;

        if (isTopLeft){
            //w = i-j
            int i,j, count;
            for (int w=-80; w<=80; w++){
                //scan diagonally
                count = 0;

                //UPPER part of the matrix
                if (w<=0){
                    for (j=-w;j<=80;j++){
                        i = w+j;

                        //transitions:
                        //if desired card
                        if (placements[i][j]>=0&&deployed.get(placements[i][j]).getKingdom().equals(kingdom)){
                            count++;
                            if (count==3){
                                SCORE += points;
                                count = 0;
                            }
                        } else {
                            count = 0;
                        }
                    }
                }
                //LOWER part of the matrix
                else {
                    for (i=w; i<=80; i++){
                        j = i-w;

                        //transitions:
                        //if desired card
                        if (placements[i][j]>=0&&deployed.get(placements[i][j]).getKingdom().equals(kingdom)){
                            count++;
                            if (count==3){
                                SCORE += points;
                                count = 0;
                            }
                        } else {
                            count = 0;
                        }
                    }
                }

            }

        } else {

            //w = i+j
            int i,j, count;

            for (int w=2; w<=158; w++){
                //scan diagonally
                count = 0;

                //UPPER part of the matrix
                if (w<=80){
                    for (i=w; i>=0; i--){
                        j=w-i;

                        //transitions:
                        //if desired card
                        if (placements[i][j]>=0&&deployed.get(placements[i][j]).getKingdom().equals(kingdom)){
                            count++;
                            if (count==3){
                                SCORE += points;
                                count = 0;
                            }
                        } else {
                            count = 0;
                        }
                    }
                }
                //LOWER part of the matrix
                else {
                    for (i=w-80;i<=80;i++){
                        j=w-i;

                        //transitions:
                        //if desired card
                        if (placements[i][j]>=0&&deployed.get(placements[i][j]).getKingdom().equals(kingdom)){
                            count++;
                            if (count==3){
                                SCORE += points;
                                count = 0;
                            }
                        } else {
                            count = 0;
                        }
                    }
                }
            }
        }

        return SCORE;
    }
}
