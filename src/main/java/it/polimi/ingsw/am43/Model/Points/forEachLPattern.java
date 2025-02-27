package it.polimi.ingsw.am43.Model.Points;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.Corner;
import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Model.Enum.Kingdom;
import it.polimi.ingsw.am43.Model.Player;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents the rule present in some {@link ObjectiveCard}s.
 * This rule states that, if the {@link Player} places 3 {@link CardSide}s having specific {@link Kingdom}s,
 * in a particular pattern, resembling an "L" shape, he/she gets 3 points for each pattern.
 */
public class forEachLPattern implements PointsRule, Serializable {

    @Serial
    private static final long serialVersionUID = 5057292534680683322L;

    /**
     * The points awarded to the {@link Player} if a pattern is found.
     */
    private final int points;

    /**
     * The direction of the "L". Same logic as in {@link Corner}
     */
    private final int direction;

    /**
     * The {@link Kingdom}s of the 3 {@link PlayableCard}s.
     * The first one is the main {@link Kingdom}, the second one is the different {@link Kingdom}
     */
    private final Kingdom[] res;

    /**
     * Constructor of the class
     *
     * @param points         The points awarded to the {@link Player}
     * @param direction      The direction of the "L"
     * @param res            The {@link Kingdom}s of the 3 {@link PlayableCard}s
     */
    public forEachLPattern(int points, int direction, Kingdom[] res) {
        this.points = points;
        this.direction = direction;
        this.res = res;
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
     * Returns the direction of the "L".
     *
     * @return  the direction of the "L"
     */
    public int getDirection() {
        return direction;
    }

    /**
     * Returns the array of {@link Kingdom}s representing the
     * {@link Kingdom}s of the 3 {@link PlayableCard}s needed to satisfy the pattern.
     *
     * @return  the array of {@link Kingdom}
     */
    public Kingdom[] getRes() {
        return res;
    }

    /**
     * Computes the points for a {@link Player} based on the placements and deployed {@link CardSide}s,
     * by checking if there are certain patterns of two
     * card "one above the other" and another one, of different {@link Kingdom}, on a specific {@link Corner}.
     *
     * @param  player   the {@link Player} for whom to compute the points
     * @return          the total points computed for the {@link Player}
     */
    @Override
    public int computePoints(Player player) {
        // Read the player's placements and deployed cards
        int[][] placements = player.getPlacements();
        ArrayList<CardSide> deployed = player.getDeployed();
        int SCORE = 0;

        // Set the search directions
        int NS = this.direction/2;
        int WE = (this.direction%2)*2 - 1;

        int i,j;
        int count = 0;
        //scan column-wise
        for (j=0; j<81; j++){

            //top-down
            if (NS==1){
                for (i=j%2;i<80;i+=2){

                    //transitions
                    if (placements[i][j]>=0 && deployed.get(placements[i][j]).getKingdom().equals(res[0])){
                        count++;
                        if (count==2) {
                            if (placements[i+1][j+WE]>=0 && deployed.get(placements[i+1][j+WE]).getKingdom().equals(res[1])) {
                                SCORE += points;
                                count = 0;
                            } else count--;
                        }
                    } else count = 0;
                }
            } else {
                for (i=80-(j%2);i>0;i-=2){

                    //transitions
                    if (placements[i][j]>=0&&deployed.get(placements[i][j]).getKingdom().equals(res[0])){
                        count++;
                        if (count==2) {
                            if (placements[i-1][j+WE] >= 0 && deployed.get(placements[i-1][j+WE]).getKingdom().equals(res[1])) {
                                SCORE += points;
                                count = 0;
                            } else count--;
                        }
                    } else count = 0;
                }
            }
        }

        return SCORE;
    }
}
