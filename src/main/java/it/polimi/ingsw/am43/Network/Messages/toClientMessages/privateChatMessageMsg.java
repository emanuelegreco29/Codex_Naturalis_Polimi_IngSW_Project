package it.polimi.ingsw.am43.Network.Messages.toClientMessages;

import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is exchanged between server and client
 * when a player sends a private message to someone else.
 */
public class privateChatMessageMsg extends Message {

    @Serial
    private static final long serialVersionUID = 4135632556609030218L;

    /**
     * The username of the sender of the message.
     */
    private final String sender;

    /**
     * The username of the receiver of the message.
     */
    private final String receiver;

    /**
     * The message content.
     */
    private final String message;

    /**
     * Constructor of the message.
     *
     * @param sender the username of the sender
     * @param receiver the username of the receiver
     * @param message the content of the message
     */
    public privateChatMessageMsg(String sender, String receiver, String message) {
        super(MessageType.PRIVATE_MSG);
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    /**
     * A method that returns the username of the sender of the message.
     *
     * @return the username of the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * A method that returns the content of the message.
     *
     * @return the content of the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * A method that returns the username of the receiver of the message.
     *
     * @return the username of the receiver
     */
    public String getReceiver() {
        return receiver;
    }
}
