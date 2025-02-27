package it.polimi.ingsw.am43.Model.Cards;

import it.polimi.ingsw.am43.Model.Enum.Kingdom;
import it.polimi.ingsw.am43.Model.Enum.Symbol;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * This class represents the backside of a {@link PlayableCard}.
 * It contains the list of fixed resources, which are the permanent
 * {@link Symbol} on the back of the card and the {@link Kingdom} of the card.
 */
public class BackSide extends CardSide implements Serializable {

    @Serial
    private static final long serialVersionUID = -5998729953829110312L;

    /**
     * A list of {@link Symbol}, representing the permanent resources
     * printed on the back of the card.
     */
    private final List<Symbol> fixedResources;

    /**
     * The {@link Kingdom} of the card.
     */
    private final Kingdom kingdom;

    /**
     * Constructor for the BackSide of a {@link PlayableCard}.
     *
     * @param corners         Array of {@link Corner}
     * @param kingdom         The {@link Kingdom} of the card
     * @param fixedResources  List of {@link Symbol}, representing the permanent resources
     */
    public BackSide(Corner[] corners, Kingdom kingdom, List<Symbol> fixedResources, String pngPath) {
        super(corners, pngPath);
        this.fixedResources = fixedResources;
        this.kingdom = kingdom;
    }

    /**
     * A function that retrieves the list of {@link Symbol}s
     * which represent the fixed resources.
     *
     * @return         list of {@link Symbol}s representing the fixed resources
     */
    @Override
    public List<Symbol> getFixedResources() {
        return fixedResources;
    }

    /**
     * A function that returns the {@link Kingdom} of the card.
     *
     * @return            {@link Kingdom} of the card
     */
    @Override
    public Kingdom getKingdom() {
        return kingdom;
    }
}
