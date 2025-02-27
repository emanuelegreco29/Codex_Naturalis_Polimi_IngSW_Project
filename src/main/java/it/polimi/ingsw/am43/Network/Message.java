package it.polimi.ingsw.am43.Network;

import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;
import java.io.Serializable;

/**
 * A class that represents a message exchangeable between the client
 * and the server through Socket connection.
 */
public abstract class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1473127631721019840L;

    /**
     * The type of the message.
     */
    private final MessageType type;

    /**
     * Constructor of the class.
     *
     * @param type the type of the message
     */
    public Message(MessageType type) {
        this.type = type;
    }

    /**
     * Returns the type of the message.
     *
     * @return the type of the message
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Returns a string representation of the type of the message.
     *
     * @return a string representation of the type of the message
     */
    @Override
    public String toString() {
        return type.toString();
    }
}
