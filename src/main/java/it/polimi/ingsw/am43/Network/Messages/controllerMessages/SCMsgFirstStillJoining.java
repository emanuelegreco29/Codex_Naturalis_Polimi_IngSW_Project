package it.polimi.ingsw.am43.Network.Messages.controllerMessages;

import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is sent from the client to the server
 * when the first player of the game is still setting up
 * the parameters, thus all the other players connected
 * have to wait before doing anything.
 */
public class SCMsgFirstStillJoining extends Message {

    @Serial
    private static final long serialVersionUID = -7257953881970551519L;

    /**
     * Constructor of the message.
     */
    public SCMsgFirstStillJoining() {
        super(MessageType.FIRST_STILL_JOINING);
    }
}
