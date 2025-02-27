package it.polimi.ingsw.am43.Network.Messages.controllerMessages;

import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * The message periodically sent from the client to the server once
 * connected, in order to detect any disconnection from the server.
 */
public class SCMsgHeartbeat extends Message {

    @Serial
    private static final long serialVersionUID = 7520675176856414799L;

    /**
     * Constructor of the message.
     */
    public SCMsgHeartbeat() {
        super(MessageType.HEARTBEAT);
    }
}
