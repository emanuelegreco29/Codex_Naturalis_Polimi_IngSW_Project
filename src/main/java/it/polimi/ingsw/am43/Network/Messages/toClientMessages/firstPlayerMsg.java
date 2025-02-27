package it.polimi.ingsw.am43.Network.Messages.toClientMessages;

import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is sent from the server to the client
 * of the first player entering the game. It triggers the set-up.
 */
public class firstPlayerMsg extends Message {

    @Serial
    private static final long serialVersionUID = 799944444385664276L;

    /**
     * Constructor of the message.
     */
    public firstPlayerMsg() {
        super(MessageType.FIRSTPLAYER);
    }
}
