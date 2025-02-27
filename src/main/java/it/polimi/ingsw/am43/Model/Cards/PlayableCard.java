package it.polimi.ingsw.am43.Model.Cards;

import it.polimi.ingsw.am43.Model.Enum.CardType;
import it.polimi.ingsw.am43.Model.Enum.Kingdom;
import it.polimi.ingsw.am43.Model.Player;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents a {@link Card} that can be played by a {@link Player}
 * during a turn.
 * It contains a {@link CardType}, a {@link Kingdom} and two {@link CardSide}s.
 */
public class PlayableCard extends Card implements Serializable {

    @Serial
    private static final long serialVersionUID = 2773584342823419389L;

    /**
     * The {@link CardSide} for the front of the card
     */
    private final CardSide frontside;

    /**
     * The {@link CardSide} for the back of the card
     */
    private final CardSide backside;

    /**
     * The {@link CardType} of the card
     */
    private final CardType type;

    /**
     * The {@link Kingdom} of the card
     */
    private final Kingdom kingdom;

    /**
     * Constructor for a PlayableCard.
     *
     * @param id                 The ID of the card
     * @param front              The front {@link CardSide} of the card
     * @param back               The back {@link CardSide} of the card
     * @param type               The {@link CardType} of the card
     * @param kingdom            The {@link Kingdom} of the card
     */
    public PlayableCard(int id, CardSide front, CardSide back, CardType type, Kingdom kingdom) {
        super(id);
        this.frontside = front;
        this.backside = back;
        this.type = type;
        this.kingdom = kingdom;
    }

    /**
     * A function that returns the front card's {@link CardSide}.
     *
     * @return         	{@link CardSide} for the front of the card
     */
    public CardSide getFrontside() {
        return frontside;
    }

    /**
     * A function that returns the back card's {@link CardSide}.
     *
     * @return         	{@link CardSide} for the back of the card
     */
    public CardSide getBackside() {
        return backside;
    }

    /**
     * A function that returns the {@link CardType} of the card.
     *
     * @return         	{@link CardType} of the card
     */
    public CardType getType() {
        return type;
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
     * A function that returns the path of the card's front side.
     *
     * @return         	String that contains the path
     */
    public String getFrontPath() {
        return frontside.getPngPath();
    }

    /**
     * A function that returns the path of the card's backside.
     *
     * @return         	String that contains the path
     */
    public String getBackPath() {
        return backside.getPngPath();
    }
}
