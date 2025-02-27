package it.polimi.ingsw.am43.Network.Messages.controllerMessages;

import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

/**
 * This message is sent from the client to the server when the user has
 * picked its own secret {@link ObjectiveCard}. The message if sent so that
 * the {@link it.polimi.ingsw.am43.Controller.GameController} can set it.
 */
public class SCMsgPersonalObjective extends Message {

    @Serial
    private static final long serialVersionUID = 3634247213541669554L;

    /**
     * The {@link ObjectiveCard} chosen by the player.
     */
    private final ObjectiveCard obj;

    /**
     * Constructor of the message.
     *
     * @param obj the {@link ObjectiveCard} chosen by the player.
     */
    public SCMsgPersonalObjective(ObjectiveCard obj) {
        super(MessageType.PERSONAL_OBJECTIVE_CHOICE);
        this.obj = obj;
    }

    /**
     * Returns the chosen {@link ObjectiveCard}.
     *
     * @return the chosen {@link ObjectiveCard}
     */
    public ObjectiveCard getObj() {
        return obj;
    }
}
