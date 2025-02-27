package it.polimi.ingsw.am43.Network.Messages.controllerMessages;

import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is sent from the client to the server when
 * the turn has finished and all the information and modifications
 * made by the player are sent to the server.
 */
public class SCMsgPlayTurn extends Message {

    @Serial
    private static final long serialVersionUID = -790464647976617436L;

    /**
     * The index of the card that player wants to place.
     */
    private final int handIndex;

    /**
     * The index of the {@link it.polimi.ingsw.am43.Model.Cards.CardSide} on which the player wants to place.
     */
    private final int deployedIndex;

    /**
     * The index of the {@link it.polimi.ingsw.am43.Model.Cards.Corner} on which the card is placed.
     */
    private final int corner;

    /**
     * The index of the drawn {@link it.polimi.ingsw.am43.Model.Cards.PlayableCard} from the ground.
     */
    private final int drawn;

    /**
     * Constructor of the message.
     *
     * @param handIndex index of the card that player wants to place
     * @param deployedIndex index of the card on which the player wants to place
     * @param corner index of the corner on which the card is placed
     * @param drawn index of the card on ground to draw
     */
    public SCMsgPlayTurn(int handIndex, int deployedIndex, int corner, int drawn) {
        super(MessageType.PLAYTURN);
        this.handIndex = handIndex;
        this.deployedIndex = deployedIndex;
        this.corner = corner;
        this.drawn = drawn;
    }

    /**
     * Returns the index of the {@link it.polimi.ingsw.am43.Model.Cards.PlayableCard} that the player wants to place.
     *
     * @return index of the card to place
     */
    public int getHandIndex() {
        return handIndex;
    }

    /**
     * Returns the index of the {@link it.polimi.ingsw.am43.Model.Cards.CardSide} on which the player wants to place.
     *
     * @return index of the card on which the player wants to place
     */
    public int getDeployedIndex() {
        return deployedIndex;
    }

    /**
     * Returns the index of the {@link it.polimi.ingsw.am43.Model.Cards.Corner} on which the card is placed.
     *
     * @return index of the {@link it.polimi.ingsw.am43.Model.Cards.Corner} on which the card is placed
     */
    public int getCorner() {
        return corner;
    }

    /**
     * Returns the index of the {@link it.polimi.ingsw.am43.Model.Cards.PlayableCard} to draw from the ground
     * or deck (if not empty).
     *
     * @return the index of the card to draw
     */
    public int getDrawn() {
        return drawn;
    }

}
