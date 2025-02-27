package it.polimi.ingsw.am43.Network.Messages.controllerMessages;

import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is sent from the client to the server when it is the turn of
 * the player sending the message. It triggers the server to send all the
 * messages containing the useful information to let the player play its turn.
 */
public class SCMsgPlay extends Message {

    @Serial
    private static final long serialVersionUID = 1238612795817567511L;

    /**
     * Constructor of the message.
     */
    public SCMsgPlay() {
        super(MessageType.PLAY);
    }


}
