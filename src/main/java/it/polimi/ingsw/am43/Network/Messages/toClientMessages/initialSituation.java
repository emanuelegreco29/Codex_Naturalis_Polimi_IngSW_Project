package it.polimi.ingsw.am43.Network.Messages.toClientMessages;

import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is sent from the server to the client at the start
 * of the game.
 */
public class initialSituation extends Message {

    @Serial
    private static final long serialVersionUID = 7250248927382549888L;

    /**
     * The player's hand.
     */
    private final PlayableCard[] inHand;

    /**
     * The starting card of the player.
     */
    private final PlayableCard startingCard;

    /**
     * The common objective cards.
     */
    private final ObjectiveCard[] commonObjectives;

    /**
     * The ID of the player.
     */
    private final int playerID;

    /**
     * Constructor of the message.
     *
     * @param inHand the player's hand
     * @param startingCard the player's starting card
     * @param commonObjectives the common objective cards
     * @param playerID the ID of the player
     */
    public initialSituation(PlayableCard[] inHand, PlayableCard startingCard, ObjectiveCard[] commonObjectives, int playerID) {
        super(MessageType.INITIAL_SITUATION);
        this.inHand = inHand;
        this.startingCard = startingCard;
        this.commonObjectives = commonObjectives;
        this.playerID = playerID;
    }

    /**
     * A method that returns the player's hand.
     *
     * @return the player's hand
     */
    public PlayableCard[] getInHand() {
        return inHand;
    }

    /**
     * A method that returns the player's starting card.
     *
     * @return the player's starting card
     */
    public PlayableCard getStartingCard() {
        return startingCard;
    }

    /**
     * A method that returns the common objective cards.
     *
     * @return the common objective cards
     */
    public ObjectiveCard[] getCommonObjectives() {
        return commonObjectives;
    }

    /**
     * A method that returns the ID of the player.
     *
     * @return the ID of the player
     */
    public int getPlayerID() {
        return playerID;
    }
}
