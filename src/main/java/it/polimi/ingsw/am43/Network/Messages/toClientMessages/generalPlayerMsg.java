package it.polimi.ingsw.am43.Network.Messages.toClientMessages;

import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is sent from the server to the client
 * when a player, that is not the first, joins the game.
 */
public class generalPlayerMsg extends Message {

    @Serial
    private static final long serialVersionUID = 5771407813405019226L;

    /**
     * The array of remaining colors indexes.
     */
    private final int[] remaining;

    /**
     * The array of taken nicknames.
     */
    private final String[] nicknames;

    /**
     * Constructor of the message.
     *
     * @param remaining The array of remaining colors indexes.
     * @param nicknames The array of taken nicknames.
     */
    public generalPlayerMsg(int[] remaining, String[] nicknames) {
        super(MessageType.GENERALPLAYER);
        this.remaining = remaining;
        this.nicknames = nicknames;
    }

    /**
     * A method that returns the array of remaining colors indexes.
     *
     * @return The array of remaining colors indexes.
     */
    public int[] getRemaining() {
        return remaining;
    }

    /**
     * A method that returns the array of taken nicknames.
     *
     * @return The array of taken nicknames.
     */
    public String[] getNicknames() {
        return nicknames;
    }
}
