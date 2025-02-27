package it.polimi.ingsw.am43.Model.Points;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Model.Enum.Symbol;
import it.polimi.ingsw.am43.Model.Player;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents the rule present in some {@link ObjectiveCard}s.
 * This rule states that, if the {@link Player} has sufficient resources to create a specified set
 * he/she gets 2 or 3 points, depending on the size of the set.
 * The objects' {@link Symbol}s and quantities are specified by the {@link ObjectiveCard} itself.
 */
public class forEachResourceSet implements PointsRule, Serializable{

    @Serial
    private static final long serialVersionUID = 1007616939320036259L;

    /**
     * The points earned by the {@link Player} for each resource
     * set that he can make up.
     */
    private final int points;

    /**
     * A list of {@link Symbol}s making up the resource set.
     */
    private final List<Symbol> resourceSet;

    /**
     * The constructor of the class.
     *
     * @param  points  the points earned by the {@link Player}
     * @param  resourceSet  a list of {@link Symbol}s making up the resource set
     */
    public forEachResourceSet(int points, List<Symbol> resourceSet) {
        this.points = points;
        this.resourceSet = resourceSet;
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
     * A function that returns the required set of {@link Symbol}s
     * corresponding to the resources needed to satisfy the rule.
     *
     * @return the list of {@link Symbol}s representing the resources of the set
     */
    public List<Symbol> getResourceSet() {
        return resourceSet;
    }

    /**
     * Computes the points earned by a {@link Player} based on their resource
     * quantity and resource set.
     *
     * @param  p  the {@link Player} whose points are being computed
     * @return    the total points earned by the {@link Player}
     */
    @Override
    public int computePoints(Player p) {
        // Create a new array with the same values of the player's resource array
        int[] res_copy = Arrays.copyOf(p.getRes_qty(), p.getRes_qty().length);
        int[] res_index = this.resourceSet.stream().mapToInt(Symbol::toInt).toArray();

        // If the player doesn't have any resources, return 0
        if (res_index.length == 0) return 0;

        boolean flag = true;
        int counter =0;
        // Search for player's resources
        while(flag){
            for (int resIndex : res_index) {
                if (res_copy[resIndex] == 0) {
                    flag = false;
                    break;
                }
                res_copy[resIndex]--;
            }
            if (flag){
                counter++;
            }
        }
        return this.points* counter ;
    }
}
