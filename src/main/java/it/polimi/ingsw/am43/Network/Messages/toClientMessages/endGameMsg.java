package it.polimi.ingsw.am43.Network.Messages.toClientMessages;

import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is sent from the server to the client when the game is over.
 * It provides the players' rankings, so that the view can display them before
 * terminating the execution.
 */
public class endGameMsg extends Message {

    @Serial
    private static final long serialVersionUID = 976336491248423925L;

    /**
     * Players' rankings;
     */
    private final int[][] ranking;

    /**
     * Constructor for the message.
     *
     * @param ranking the data structure containing players' rankings
     */
    public endGameMsg(int[][] ranking) {
        super(MessageType.ENDGAME);
        this.ranking = ranking;
    }

    /**
     * A method that returns players' rankings.
     *
     * @return the data structure containing players' rankings
     */
    public int[][] getRanking() {
        return ranking;
    }

}
