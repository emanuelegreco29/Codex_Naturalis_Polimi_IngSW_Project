package it.polimi.ingsw.am43.Network.Messages.toClientMessages;

import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.MessageType;

import java.io.Serial;

public class placeableCardsMsg extends Message {
    @Serial
    private static final long serialVersionUID = 2556183498557243198L;
    private final Boolean[] placeableCards;


    public placeableCardsMsg(Boolean[] placeableCards) {
        super(MessageType.PLEACABLECARDS);
        this.placeableCards = placeableCards;
    }


    public Boolean[] getPlaceableCards() {
        return placeableCards;
    }


}
