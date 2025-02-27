package it.polimi.ingsw.am43.Network.Messages.toClientMessages;

import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;
import java.util.List;

/**
 * This message is sent from the server to the client when
 * the player, who is trying to rejoin a loaded game, has input
 * a nickname already chosen in the meantime by someone else.
 */
public class cannotRejoinMsg extends Message {

    @Serial
    private static final long serialVersionUID = 5043461703073540767L;

    /**
     * List of invalid indexes, these will not be printed
     */
    private final List<Integer> invalid;

    /**
     * Array of usernames of the players of the game.
     */
    private final String[] players;

    /**
     * Constructor of the message.
     *
     * @param invalid the invalid indexes
     * @param players the usernames of the players
     */
    public cannotRejoinMsg(List<Integer> invalid, String[] players) {
        super(MessageType.CANNOT_REJOIN);
        this.invalid = invalid;
        this.players = players;
    }

    /**
     * A method that returns the invalid indexes
     *
     * @return the indexes
     */
    public List<Integer> getInvalid() {
        return invalid;
    }

    /**
     * A method that returns the usernames of the players of the game.
     *
     * @return the usernames
     */
    public String[] getPlayers() {
        return players;
    }
}
