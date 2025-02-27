package it.polimi.ingsw.am43.Network.Messages.toClientMessages;

import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

public class nickOrColorAlreadyUsedMsg extends Message {

    @Serial
    private static final long serialVersionUID = -7765986604111404768L;

    /**
     * The array of the nicknames already used by other players.
     */
    private final String[] nicknames;

    /**
     * The array of indexes of colors not picked yet.
     */
    private final int[] colors;

    /**
     * Constructor of the message.
     *
     * @param nicknames  the nicknames already used
     * @param colors     the indexes of colors not picked yet
     */
    public nickOrColorAlreadyUsedMsg(String[] nicknames, int[] colors) {
        super(MessageType.NICKNAME_ALREADY_USED);
        this.nicknames = nicknames;
        this.colors = colors;
    }

    /**
     * A method that returns the nicknames already used.
     *
     * @return the nicknames
     */
    public String[] getNicknames() {
        return nicknames;
    }

    /**
     * A method that returns the indexes of colors not picked yet.
     *
     * @return the indexes
     */
    public int[] getColors() {
        return colors;
    }
}
