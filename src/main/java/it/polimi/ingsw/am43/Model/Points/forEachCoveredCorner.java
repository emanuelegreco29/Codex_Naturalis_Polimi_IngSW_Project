package it.polimi.ingsw.am43.Model.Points;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.Corner;
import it.polimi.ingsw.am43.Model.Player;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents the rule present in some gold {@link CardSide}s.
 * This rule states that, if the {@link Player} places a gold {@link CardSide} with this rule,
 * he/she gets 2 points for each {@link Corner} covered by the gold {@link CardSide}.
 */
public class forEachCoveredCorner implements PointsRule, Serializable {

    @Serial
    private static final long serialVersionUID = -6072836248206932812L;

    /**
     * The points awarded for each covered {@link Corner} to the {@link Player}.
     */
    private final int points;

    /**
     * The constructor of the class.
     *
     * @param  points  the number of points awarded for each covered {@link Corner}
     */
    public forEachCoveredCorner(int points) {
        this.points = points;
    }

    /**
     * Returns the number of points associated with this rule.
     *
     * @return the number of points
     */
    @Override
    public int getPoints() {
        return points;
    }

    /**
     * Computes the points earned by a {@link Player} for each covered {@link Corner} of
     * the deployed gold {@link CardSide}.
     *
     * @param  p  the {@link Player} whose points are being computed
     * @return    the total points earned by the {@link Player} for each covered {@link Corner}
     */
    @Override
    public int computePoints(Player p) {
        // Retrieves the coordinates of the last placed CardSide
        int[] last_deployed_coord = p.getDeployed().getLast().getRelativeCoordinates();
        int x = last_deployed_coord[0]+Player.OFFSET;
        int y = last_deployed_coord[1]+Player.OFFSET;

        // For each corner that is covered by the CardSide, increase the number of points and return them
        return( ((p.getPlacements()[x-1][y-1] != -1)? 1 : 0) +
                ((p.getPlacements()[x-1][y+1] != -1)? 1 : 0) +
                ((p.getPlacements()[x+1][y-1] != -1)? 1 : 0) +
                ((p.getPlacements()[x+1][y+1] != -1)? 1 : 0) ) * this.points;
    }
}
