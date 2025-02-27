package it.polimi.ingsw.am43.Network.Messages.toClientMessages;

import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is sent from the server to the client
 * when the turn has finished to update the player's hand.
 */
public class newInHandMsg extends Message {

    @Serial
    private static final long serialVersionUID = -4468386818890774037L;

    /**
     * The card drawn from the ground or the deck.
     */
    private final PlayableCard drawn;

    /**
     * Constructor of the message.
     *
     * @param drawn the drawn card
     */
    public newInHandMsg(PlayableCard drawn) {
        super(MessageType.NEWINHAND);
        this.drawn = drawn;
    }

    /**
     * A method that returns the drawn card.
     *
     * @return the drawn card
     */
    public PlayableCard getDrawn() {
        return drawn;
    }
}
