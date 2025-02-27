package it.polimi.ingsw.am43.Model.Cards;

import it.polimi.ingsw.am43.Model.Enum.Kingdom;
import it.polimi.ingsw.am43.Model.Enum.Symbol;
import it.polimi.ingsw.am43.Model.Points.PointsRule;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * This class represents the front side of a gold {@link PlayableCard}.
 * It contains the list of required resources and the {@link Kingdom} of the card, as well as
 * the {@link PointsRule} of the {@link CardSide}.
 */
public class GoldFrontSide extends CardSide implements Serializable {

    @Serial
    private static final long serialVersionUID = 4099332817241497539L;

    /**
     * List of {@link Symbol}s representing the required resources
     * to place the card.
     */
    private final List<Symbol> requiredResources;

    /**
     * The {@link Kingdom} of the card.
     */
    private final Kingdom kingdom;

    /**
     * The {@link PointsRule} of this {@link CardSide}.
     */
    private final PointsRule rule;

    /**
     * Constructor of the class.
     *
     * @param corners         {@link Corner}s of the card
     * @param kingdom         {@link Kingdom} of the card
     * @param requiredResources List of {@link Symbol}s representing the required resources
     * @param rule            {@link PointsRule} of the card
     */
    public GoldFrontSide(Corner[] corners, Kingdom kingdom, List<Symbol> requiredResources, PointsRule rule, String pngPath) {
        super(corners, pngPath);
        this.kingdom = kingdom;
        this.requiredResources = requiredResources;
        this.rule = rule;
    }

    /**
     * Get the list of {@link Symbol}s representing
     * the required resources to place the card.
     *
     * @return         	 list of {@link Symbol}s representing the required resources
     */
    @Override
    public List<Symbol> getRequiredResources() { return requiredResources; }

    /**
     * A function that returns the {@link Kingdom} of the card.
     *
     * @return            {@link Kingdom} of the card
     */
    @Override
    public Kingdom getKingdom() {
        return kingdom;
    }

    /**
     * A function that returns the {@link PointsRule} of the card side.
     *
     * @return            {@link PointsRule} associated to the card side
     */
    @Override
    public PointsRule getPointsRule() {
        return rule;
    }
    /**
     * A function that checks if the {@link it.polimi.ingsw.am43.Model.Player} has enough resources to place the card.
     *
     * @param  playerRes array of player's resources
     * @return          true if enough resources, false otherwise
     */
    @Override
    public boolean checkGoldRequisites(int[] playerRes) {
        // If the card to be placed has no requirements, all good
        if (this.requiredResources.isEmpty()) {
            return true;
        }

        assert(playerRes != null);
        // Convert requiredResources to int[4] with counts of each symbol
        int[] requiredCounts = new int[4];
        for (Symbol symbol : this.requiredResources) {
            requiredCounts[symbol.ordinal()]++;
        }

        // Check if there are enough available resources for each required resource
        for (int i = 0; i < requiredCounts.length; i++) {
            if (requiredCounts[i] > playerRes[i]) {
                return false; // Not enough available resources
            }
        }
        return true;
    }
}
