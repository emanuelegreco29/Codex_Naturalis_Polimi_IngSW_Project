package it.polimi.ingsw.am43.Network.Messages.toClientMessages;

import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is sent from the server to the client when the
 * player has to pick its secret objective. It contains the
 * two possible {@link ObjectiveCard}s to choose from.
 */
public class personalObjectivesMsg extends Message {

    @Serial
    private static final long serialVersionUID = -150176765787099494L;

    /**
     * The two objective cards to choose from.
     */
    private final ObjectiveCard[] personalObjectives;

    /**
     * Constructor of the message.
     *
     * @param personalObjectives    The two possible objective cards.
     */
    public personalObjectivesMsg(ObjectiveCard[] personalObjectives) {
        super(MessageType.PERSONALOBJECTIVES);
        this.personalObjectives = personalObjectives;
    }

    /**
     * A method that returns the two potential personal objectives.
     *
     * @return   The two potential personal objectives.
     */
    public ObjectiveCard[] getPersonalObjectives() {
        return personalObjectives;
    }
}
