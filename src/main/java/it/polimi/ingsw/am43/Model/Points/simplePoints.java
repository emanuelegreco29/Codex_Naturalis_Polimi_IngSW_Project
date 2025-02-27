package it.polimi.ingsw.am43.Model.Points;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Player;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents the rule only valid for some front sides of some Resource Cards.
 * These cards give exactly one point to the player if placed on their front side.
 * It contains the points awarded to the player.
 */
public class simplePoints implements PointsRule, Serializable {

    @Serial
    private static final long serialVersionUID = 648718669474812206L;

    /**
     * The points awarded from the {@link CardSide}.
     */
    private final int points;

    /**
     * A constructor that initializes the points of the {@link CardSide}.
     * This rule is only valid for some front sides of some Resource Cards.
     *
     * @param  points  the points awarded from the {@link CardSide}
     */
    public simplePoints(int points) {
        this.points = points;
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
     * A method that returns the points of the {@link CardSide} (valid only for the
     * 1 point resource cards).
     *
     * @param  player  the {@link Player} for whom the points are computed
     * @return         the points computed for the {@link Player}
     */
    @Override
    public int computePoints(Player player) {
        return points;
    }
}