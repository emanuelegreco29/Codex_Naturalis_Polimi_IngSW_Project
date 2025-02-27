package it.polimi.ingsw.am43.Network;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote, Serializable {

    Message readNewMessage();

    void writeNewMessage(Message msg);
}
