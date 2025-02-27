package it.polimi.ingsw.am43.Network.Messages.toClientMessages;

import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is sent from the server to the client during
 * the player's turn. It contains all the corners of a card on which
 * a placement is possible, as well as two booleans stating if
 * the Resource and Gold decks are empty or not.
 */
public class availablePlacementsMsg extends Message {

    @Serial
    private static final long serialVersionUID = 3361236641192879284L;

    /**
     * Boolean array stating if a corner is ok to place on or not
     */
    private final boolean[] availablePlacements;

    /**
     * Boolean stating if the resource deck is empty
     */
    private final boolean resEmpty;

    /**
     * Boolean stating if the gold deck is empty
     */
    private final boolean goldEmpty;

    /**
     * Constructor of the message.
     *
     * @param availablePlacements  corners on which placement is possible
     * @param resEmpty             boolean stating if the resource deck is empty
     * @param goldEmpty            boolean stating if the gold deck is empty
     */
    public availablePlacementsMsg(boolean[] availablePlacements, boolean resEmpty, boolean goldEmpty) {
        super(MessageType.AVAILABLEPLACEMENTS);
        this.availablePlacements = availablePlacements;
        this.resEmpty = resEmpty;
        this.goldEmpty = goldEmpty;
    }


    /**
     * A method that returns the corners on which placement is possible.
     *
     * @return  Booleans of corners on which placement is possible
     */
    public boolean[] getAvailablePlacements() {
        return availablePlacements;
    }

    /**
     * A method that returns if the resource deck is empty.
     *
     * @return if the resource deck is empty
     */
    public boolean isResEmpty() {
        return resEmpty;
    }

    /**
     * A method that returns if the gold deck is empty.
     *
     * @return if the gold deck is empty
     */
    public boolean isGoldEmpty() {
        return goldEmpty;
    }
}
