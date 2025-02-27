package it.polimi.ingsw.am43.Network.Messages.toClientMessages;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is sent from the server to the client when
 * the game starts. It provides all the initial information of the
 * game.
 */
public class initialGameInfoMsg extends Message {

    @Serial
    private static final long serialVersionUID = -8282704599079438755L;

    /**
     * The sides of the starting card, which the player has to choose in order
     * to place one.
     */
    private final CardSide[] startingSides;

    /**
     * The cards on the ground.
     */
    private final PlayableCard[] onGround;

    /**
     * The first player playing its turn.
     */
    private final int first;

    /**
     * The array of the usernames of the players.
     */
    private final String[] usernames;

    /**
     * The colors chosen by the players.
     */
    private final PawnColor[] colors;

    /**
     * The backside of the card on top of the golden deck.
     */
    private final CardSide goldenDeckTop;

    /**
     * The backside of the card on top of the resource deck.
     */
    private final CardSide resourceDeckTop;

    /**
     * Constructor of the message.
     *
     * @param startingSides the sides of the starting card
     * @param onGround the cards on the ground
     * @param first the first player
     * @param usernames the usernames
     * @param colors the colors
     * @param goldenDeckTop the backside of the card on top of the golden deck
     * @param resourceDeckTop the backside of the card on top of the resource deck
     */
    public initialGameInfoMsg(CardSide[] startingSides, PlayableCard[] onGround, int first, String[] usernames, PawnColor[] colors, CardSide goldenDeckTop, CardSide resourceDeckTop) {
        super(MessageType.INITIALGAMEINFO);
        this.startingSides = startingSides;
        this.onGround = onGround;
        this.first = first;
        this.usernames = usernames;
        this.colors = colors;
        this.goldenDeckTop = goldenDeckTop;
        this.resourceDeckTop = resourceDeckTop;
    }

    /**
     * A method that returns the sides of the starting card.
     *
     * @return the sides of the starting card
     */
    public CardSide[] getStartingSides() {
        return startingSides;
    }

    /**
     * A method that returns the cards on the ground.
     *
     * @return the cards on the ground
     */
    public PlayableCard[] getOnGround() {
        return onGround;
    }

    /**
     * A method that returns the first player.
     *
     * @return the first player
     */
    public int getFirst() {
        return first;
    }

    /**
     * A method that returns the usernames.
     *
     * @return the usernames
     */
    public String[] getUsernames() {
        return usernames;
    }

    /**
     * A method that returns the colors.
     *
     * @return the colors
     */
    public PawnColor[] getColors() {
        return colors;
    }

    /**
     * A method that returns the backside of the card on top of the golden deck.
     *
     * @return the backside of the card on top of the golden deck
     */
    public CardSide getGoldenDeckTop() {
        return goldenDeckTop;
    }

    /**
     * A method that returns the backside of the card on top of the resource deck.
     *
     * @return the backside of the card on top of the resource deck
     */
    public CardSide getResourceDeckTop() {
        return resourceDeckTop;
    }
}
