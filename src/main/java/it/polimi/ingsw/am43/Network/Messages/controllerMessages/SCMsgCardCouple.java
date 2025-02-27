package it.polimi.ingsw.am43.Network.Messages.controllerMessages;

import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * The message sent from the client to the server when the player
 * has chosen the card that it wants to place and on top of
 * which card. The message contains the index of the already
 * placed card, on which the player wants to place the new one.
 * This message lets the controller check what corners
 * are available on the deployedIndex card.
 */
public class SCMsgCardCouple extends Message {

    @Serial
    private static final long serialVersionUID = -5029619713937357312L;

    /**
     * The index of the {@link it.polimi.ingsw.am43.Model.Cards.CardSide} on which the
     * {@link it.polimi.ingsw.am43.Model.Player} wants to place the new {@link it.polimi.ingsw.am43.Model.Cards.CardSide}.
     */
    private final int deployedIndex;

    /**
     * Constructor of the message.
     *
     * @param deployedIndex  index of the card on which the player wants to place
     */
    public SCMsgCardCouple(int deployedIndex) {
        super(MessageType.COUPLECARDS);
        this.deployedIndex = deployedIndex;
    }

    /**
     * Returns the index of the card on which the player wants to place.
     *
     * @return the index of the deployed card
     */
    public int getDeployedIndex() {
        return deployedIndex;
    }

}
