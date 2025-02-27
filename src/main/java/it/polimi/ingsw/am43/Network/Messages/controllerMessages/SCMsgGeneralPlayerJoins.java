package it.polimi.ingsw.am43.Network.Messages.controllerMessages;

import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is sent from the client to the server when a player that
 * is NOT the first, wants to join the game. It contains the username and the
 * color so that the {@link it.polimi.ingsw.am43.Controller.GameController} can
 * check if the player is allowed to join the game.
 */
public class SCMsgGeneralPlayerJoins extends Message {

    @Serial
    private static final long serialVersionUID = -4467735630165439018L;

    /**
     * The username of the player that has joined.
     */
    private final String username;

    /**
     * The {@link it.polimi.ingsw.am43.Model.Enum.PawnColor} of the player that has joined.
     */
    private final int color;

    /**
     * Constructor of the message.
     *
     * @param username username of the player
     * @param color {@link it.polimi.ingsw.am43.Model.Enum.PawnColor} of the player
     */
    public SCMsgGeneralPlayerJoins(String username, int color) {
        super(MessageType.GENERALPLAYERJOINS);
        this.username = username;
        this.color = color;
    }

    /**
     * Returns the username chosen by the player.
     *
     * @return username of the player
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the color chosen by the player.
     *
     * @return the {@link it.polimi.ingsw.am43.Model.Enum.PawnColor} chosen
     */
    public int getColor() {
        return color;
    }
}
