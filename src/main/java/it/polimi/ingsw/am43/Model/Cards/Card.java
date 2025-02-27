package it.polimi.ingsw.am43.Model.Cards;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents a card.
 * It contains an ID to identify the card and the
 * path to both front and back sides of the card.
 */
public abstract class Card implements Serializable {

    @Serial
    private static final long serialVersionUID = 4896405868574015474L;

    /**
     * The ID that uniquely identifies the card.
     */
    private final int id;

    /**
     * The constructor for a card.
     *
     * @param id              card's id
     */
    protected Card(int id) {
        this.id = id;
    }

    /**
     * A function that returns the id of the card
     *
     * @return         	card's id
     */
    public int getId() {
        return id;
    }

}
