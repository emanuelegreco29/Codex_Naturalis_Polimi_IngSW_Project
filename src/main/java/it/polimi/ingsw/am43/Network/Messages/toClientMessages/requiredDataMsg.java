package it.polimi.ingsw.am43.Network.Messages.toClientMessages;

import it.polimi.ingsw.am43.Model.Cards.CardSide;
import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;
import java.util.ArrayList;

/**
 * This message is sent from the server to the client when
 * the game has been loaded and all the players have rejoined.
 * It contains all the necessary information to proceed the match.
 */
public class requiredDataMsg extends Message {

    @Serial
    private static final long serialVersionUID = 1523264094922365647L;

    /**
     * The player's hand.
     */
    private final PlayableCard[] inHand;

    /**
     * The player's starting card.
     */
    private final PlayableCard startingCard;

    /**
     * The cards on the ground.
     */
    private final PlayableCard[] onGround;

    /**
     * The common objective cards.
     */
    private final ObjectiveCard[] commonObjectives;

    /**
     * The player's secret objective.
     */
    private final ObjectiveCard personalObjective;

    /**
     * The scores of all the players.
     */
    private final int[] scores;

    /**
     * The placements of all the players.
     */
    private final int[][][] placements;

    /**
     * The deployed cards of all the players.
     */
    private final ArrayList<CardSide>[] deployed;

    /**
     * The colors chosen by the players.
     */
    private final PawnColor[] colors;

    /**
     * The usernames of all the players.
     */
    private final String[] usernames;

    /**
     * The number of players.
     */
    private final int num_players;

    /**
     * The first player.
     */
    private final int firstPlayer;

    /**
     * Constructor of the message.
     *
     * @param inHand            the player's hand
     * @param startingCard      the player's starting card
     * @param onGround          the cards on the ground
     * @param commonObjectives  the common objective cards
     * @param personalObjective the player's secret objective
     * @param scores            the scores of all the players
     * @param placements        the placements of all the players
     * @param deployed          the deployed cards of all the players
     * @param colors            the colors chosen by the players
     * @param usernames         the usernames of all the players
     * @param num_players       the number of players
     * @param firstPlayer       the first player index
     */
    public requiredDataMsg(PlayableCard[] inHand, PlayableCard startingCard,
                           PlayableCard[] onGround, ObjectiveCard[] commonObjectives,
                           ObjectiveCard personalObjective, int[] scores, int[][][] placements,
                           ArrayList<CardSide>[] deployed, PawnColor[] colors,
                           String[] usernames, int num_players, int firstPlayer) {
        super(MessageType.REQUIRED_DATA);
        this.inHand = inHand;
        this.startingCard = startingCard;
        this.onGround = onGround;
        this.commonObjectives = commonObjectives;
        this.personalObjective = personalObjective;
        this.scores = scores;
        this.placements = placements;
        this.deployed = deployed;
        this.colors = colors;
        this.usernames = usernames;
        this.num_players = num_players;
        this.firstPlayer = firstPlayer;
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
     * A method that returns the cards on the ground.
     *
     * @return the cards on the ground
     */
    public PlayableCard[] getOnGround() {
        return onGround;
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
     * A method that returns the player's secret objective.
     *
     * @return the player's secret objective
     */
    public ObjectiveCard getPersonalObjective() {
        return personalObjective;
    }

    /**
     * A method that returns the scores of all the players.
     *
     * @return the scores of all the players
     */
    public int[] getScores() {
        return scores;
    }

    /**
     * A method that returns the placements of all the players.
     *
     * @return the placements of all the players
     */
    public int[][][] getPlacements() {
    	return placements;
    }

    /**
     * A method that returns the deployed cards of all the players.
     *
     * @return the deployed cards of all the players
     */
    public ArrayList<CardSide>[] getDeployed() {
    	return deployed;
    }

    /**
     * A method that returns the colors chosen by the players.
     *
     * @return the colors chosen by the players
     */
    public PawnColor[] getColors() {
    	return colors;
    }

    /**
     * A method that returns the usernames of all the players.
     *
     * @return the usernames of all the players
     */
    public String[] getUsernames() {
    	return usernames;
    }

    /**
     * A method that returns the number of players.
     *
     * @return the number of players
     */
    public int getNumPlayers() {
    	return num_players;
    }

    /**
     * A method that returns the first player index.
     *
     * @return the first player index
     */
    public int getFirstPlayer() {
        return firstPlayer;
    }
}
