package it.polimi.ingsw.am43.Network.Messages.toClientMessages;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is sent from the server to the client
 * when the game state has changed. In particular, after a turn
 * has been played or after a game has been loaded successfully and
 * all the players have rejoined.
 */
public class gameInfoMsg extends Message {

    @Serial
    private static final long serialVersionUID = 5964149010500931661L;

    /**
     * The ID of the player that has played the last turn.
     */
    private final int playerID;

    /**
     * The array of scores of all the players.
     */
    private final int[] scores;

    /**
     * The new card on the ground.
     */
    private final PlayableCard lastOnGround;

    /**
     * The index of the new card on the ground.
     */
    private final int lastOnGroundIndex;

    /**
     * The {@link CardSide} that has been placed by the player.
     */
    private final CardSide placedCardSide;

    /**
     * The backside of the card on top of the gold deck.
     */
    private final CardSide goldenDeckTop;

    /**
     * The backside of the card on top of the resource deck.
     */
    private final CardSide resourceDeckTop;

    /**
     * Constructor of the message.
     *
     * @param playerID the ID of the player who last played
     * @param scores the scores of all the players
     * @param onGround the new card on the ground
     * @param lastOnGroundIndex the index of the new card on the ground
     * @param placedCardSide the {@link CardSide} that has been placed by the player
     * @param goldenDeckTop the backside of the card on top of the gold deck
     * @param resourceDeckTop the backside of the card on top of the resource deck
     */
    public gameInfoMsg(int playerID, int[] scores, PlayableCard onGround, int lastOnGroundIndex, CardSide placedCardSide, CardSide goldenDeckTop, CardSide resourceDeckTop) {
        super(MessageType.GAMEINFO);
        this.playerID = playerID;
        this.scores = scores;
        this.lastOnGround = onGround;
        this.lastOnGroundIndex = lastOnGroundIndex;
        this.placedCardSide = placedCardSide;
        this.goldenDeckTop = goldenDeckTop;
        this.resourceDeckTop = resourceDeckTop;
    }

    /**
     * A method that returns the ID of the player who last played.
     *
     * @return the ID of the player
     */
    public int getPlayerID() {
        return playerID;
    }

    /**
     * A method that returns the scores of all the players.
     *
     * @return the scores
     */
    public int[] getScores() {
        return scores;
    }

    /**
     * A method that returns the new card on the ground.
     *
     * @return the new card
     */
    public CardSide getPlacedCardSide() {
        return placedCardSide;
    }

    /**
     * A method that returns the index of the new card on the ground.
     *
     * @return the index of the new card
     */
    public PlayableCard getLastOnGround() {
        return lastOnGround;
    }

    /**
     * A method that returns the index of the new card on the ground.
     *
     * @return the index of the new card
     */
    public int getLastOnGroundIndex() {
        return lastOnGroundIndex;
    }

    /**
     * A method that returns the backside of the card on top of the gold deck.
     *
     * @return the backside of the card
     */
    public CardSide getGoldenDeckTop() {
        return goldenDeckTop;
    }

    /**
     * A method that returns the backside of the card on top of the resource deck.
     *
     * @return the backside of the card
     */
    public CardSide getResourceDeckTop() {
        return resourceDeckTop;
    }
}
