package it.polimi.ingsw.am43.Network.Messages.controllerMessages;

import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is sent from the client to the server when the player
 * has chosen the side of the starting card that it wants to place and has
 * to ask the server for two {@link it.polimi.ingsw.am43.Model.Cards.ObjectiveCard}s.
 */
public class SCMsgDrawPersonalObjectives extends Message {

    @Serial
    private static final long serialVersionUID = 3358290944965987565L;

    /**
     * The side of the starting card.
     */
    private final int side;

    /**
     * Constructor of the message.
     *
     * @param side  Side of the starting card to place on the ground.
     */
    public SCMsgDrawPersonalObjectives(int side) {
        super(MessageType.DRAW_PERSONAL_OBJECTIVES);
        this.side = side;
    }

    /**
     * Returns the side of the starting card.
     *
     * @return side of the card
     */
    public int getSide() {
        return side;
    }
}
