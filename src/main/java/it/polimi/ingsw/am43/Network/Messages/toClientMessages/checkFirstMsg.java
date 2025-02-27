package it.polimi.ingsw.am43.Network.Messages.toClientMessages;

import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is sent from the server to the client periodically
 * when the player is waiting for the first one to set up the game.
 * If this message has some nicknames inside, the player can proceed.
 */
public class checkFirstMsg extends Message {

    @Serial
    private static final long serialVersionUID = 4135632556609030218L;

    /**
     * Array of nicknames chosen by other players.
     */
    private final String[] nicknames;

    /**
     * Array of indexes of colors chosen by other players.
     */
    private final int[] colors;

    /**
     * Constructor of the message.
     *
     * @param nicknames the nicknames of other players.
     * @param colors the indexes of colors chosen by other players.
     */
    public checkFirstMsg(String[] nicknames, int[] colors) {
        super(MessageType.CHECK_FIRST);
        this.nicknames = nicknames;
        this.colors = colors;
    }

    /**
     * A method that returns the nicknames of other players.
     *
     * @return the nicknames
     */
    public String[] getNicknames() {
        return nicknames;
    }

    /**
     * A method that returns the indexes of colors chosen by other players.
     *
     * @return the indexes
     */
    public int[] getRemaining() {
        return colors;
    }
}
