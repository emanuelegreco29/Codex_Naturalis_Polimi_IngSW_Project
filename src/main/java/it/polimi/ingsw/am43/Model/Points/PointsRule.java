package it.polimi.ingsw.am43.Model.Points;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Player;

/**
 * This interface is used to compute points, awarded to a {@link Player},
 * if he/she places a {@link CardSide}.
 * The interface is implemented by several classes, each dedicated to a specific rule.
 * The following classes implement this interface:
 *   - {@link forEachCoveredCorner}
 *   - {@link forEachDiagonalPattern}
 *   - {@link forEachLPattern}
 *   - {@link forEachResourceSet}
 *   - {@link simplePoints}
 */
public interface PointsRule {

    /**
     * Retrieves the points associated with the rule.
     *
     * @return the points value
     */
    int getPoints();

    /**
     * A method to compute points for a {@link Player}.
     *
     * @param  player   the {@link Player} for whom points are computed
     * @return          the computed points for the {@link Player}
     */
    int computePoints(Player player);
}
