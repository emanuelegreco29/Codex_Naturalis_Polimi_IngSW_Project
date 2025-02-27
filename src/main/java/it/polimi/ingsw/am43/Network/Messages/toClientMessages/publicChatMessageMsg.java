package it.polimi.ingsw.am43.Network.Messages.toClientMessages;

import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is exchanged between server and client
 * when a player sends a public message to all the others.
 */
public class publicChatMessageMsg extends Message {

    @Serial
    private static final long serialVersionUID = 2706693125987562207L;

    /**
     * The content of the message.
     */
    private final String message;

    /**
     * The username of the sender of the message.
     */
    private final String sender;

    /**
     * Constructor of the message.
     *
     * @param sender the username of the sender
     * @param message the content of the message
     */
    public publicChatMessageMsg(String sender, String message) {
        super(MessageType.GENERAL_MSG);
        this.message = message;
        this.sender = sender;
    }

    /**
     * A method that returns the content of the message
     *
     * @return the content of the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * A method that returns the username of the sender
     *
     * @return the username of the sender
     */
    public String getSender() {
        return sender;
    }
}
