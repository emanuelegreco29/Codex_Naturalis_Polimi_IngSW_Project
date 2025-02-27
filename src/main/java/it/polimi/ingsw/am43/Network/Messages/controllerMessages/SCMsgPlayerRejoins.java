package it.polimi.ingsw.am43.Network.Messages.controllerMessages;

import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is sent from the client to the server when
 * a player wants to rejoin a game that has been loaded.
 * The message contains the player's pick, meaning what username
 * the player states to have chosen when first joining the game.
 */
public class SCMsgPlayerRejoins extends Message {

    @Serial
    private static final long serialVersionUID = -1432093586326358739L;

    /**
     * The user's pick, it is an integer between 0 and the number
     * of usernames registered in the loaded game. By doing so it's
     * easy to retrieve the username of the player from the array
     * of usernames.
     */
    private final int pick;

    /**
     * Constructor of the message.
     *
     * @param pick  Index of the username chosen by the player
     */
    public SCMsgPlayerRejoins(String pick) {
        super(MessageType.PLAYER_REJOINS);
        this.pick = Integer.parseInt(pick);
    }

    /**
     * Returns the index of the username chosen by the player.
     *
     * @return  The user's pick
     */
    public int getPick() {
        return pick;
    }
}
