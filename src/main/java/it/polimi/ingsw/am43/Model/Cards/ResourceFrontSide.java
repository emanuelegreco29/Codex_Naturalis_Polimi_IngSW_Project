package it.polimi.ingsw.am43.Model.Cards;

import it.polimi.ingsw.am43.Model.Enum.CardType;
import it.polimi.ingsw.am43.Model.Enum.Kingdom;
import it.polimi.ingsw.am43.Model.Points.PointsRule;

import java.io.Serial;
import java.io.Serializable;

/**
 * The front side of a {@link PlayableCard} of RESOURCE {@link CardType}.
 * It contains the {@link Kingdom}, the {@link PointsRule} and the {@link Corner}s of the card.
 */
public class ResourceFrontSide extends CardSide implements Serializable {

    @Serial
    private static final long serialVersionUID = -1530169211661740911L;

    /**
     * The {@link PointsRule} associated to the card.
     */
    private final PointsRule rule;

    /**
     * The {@link Kingdom} of the card.
     */
    private final Kingdom kingdom;

    /**
     * Constructor for the front {@link CardSide} of a {@link PlayableCard} of RESOURCE {@link CardType}.
     *
     * @param corners              The {@link Corner}s of the card.
     * @param kingdom              The {@link Kingdom} of the card.
     * @param rule                 The {@link PointsRule} associated to the card.
     */
    public ResourceFrontSide(Corner[] corners, Kingdom kingdom, PointsRule rule, String pngPath) {
        super(corners, pngPath);
        this.kingdom = kingdom;
        this.rule = rule;
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

    /**
     * A function that returns the {@link PointsRule} of the card side.
     *
     * @return            {@link PointsRule} associated to the card side
     */
    @Override
    public PointsRule getPointsRule() {
        return rule;
    }
}
