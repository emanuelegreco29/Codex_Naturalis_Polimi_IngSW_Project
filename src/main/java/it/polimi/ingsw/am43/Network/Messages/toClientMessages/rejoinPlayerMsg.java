package it.polimi.ingsw.am43.Network.Messages.toClientMessages;

import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is sent from the server to the client
 * when a player wants to rejoin a loaded game.
 * These usernames will be shown to the player and
 * he/she will have to pick one.
 */
public class rejoinPlayerMsg extends Message {

    @Serial
    private static final long serialVersionUID = 2143431730936895322L;

    /**
     * The usernames chosen by the players in the loaded game.
     */
    private final String[] players;

    /**
     * Constructor for the message.
     *
     * @param players the usernames
     */
    public rejoinPlayerMsg(String[] players) {
        super(MessageType.REJOIN_PLAYER);
        this.players = players;
    }

    /**
     * A method that returns the usernames chosen by the players in the loaded game.
     *
     * @return the usernames
     */
    public String[] getPlayers() {
        return players;
    }
}
