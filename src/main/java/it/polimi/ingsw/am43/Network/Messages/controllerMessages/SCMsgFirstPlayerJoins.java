package it.polimi.ingsw.am43.Network.Messages.controllerMessages;

import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is sent from the client to the server when
 * the first player finishes setting up the game. It contains
 * the {@link it.polimi.ingsw.am43.Model.Player}'s username, {@link it.polimi.ingsw.am43.Model.Enum.PawnColor}
 * and the number of players that will participate in the game.
 */
public class SCMsgFirstPlayerJoins extends Message {

    @Serial
    private static final long serialVersionUID = 2839915679124490974L;

    /**
     * The username chosen by the first player of the game.
     */
    private final String username;

    /**
     * The color chosen by the first player of the game.
     * It is an index of {@link it.polimi.ingsw.am43.Model.Enum.PawnColor}.
     */
    private final int color;

    /**
     * The number of players that will participate in the game
     * created by the player
     */
    private final int numPlayers;

    /**
     * Constructor of the message.
     *
     * @param username  username of the first player
     * @param color   color of the first player
     * @param numPlayers  number of players for the game created
     */
    public SCMsgFirstPlayerJoins(String username, int color, int numPlayers) {
        super(MessageType.FIRSTPLAYERJOINS);
        this.username = username;
        this.color = color;
        this.numPlayers = numPlayers;
    }

    /**
     * Returns the username of the first player.
     *
     * @return username of the first player
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the color (in index form) of the first player.
     *
     * @return Index of the color of the first player
     */
    public int getColor() {
        return color;
    }

    /**
     * Returns the number of players that will participate in the game.
     *
     * @return number of players
     */
    public int getNumPlayers() {
        return numPlayers;
    }
}
