package it.polimi.ingsw.am43.Network.RMI;

import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Network.ClientInterface;
import it.polimi.ingsw.am43.Network.Message;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMIServerInterface extends Remote, Serializable {
    int registerClient(ClientInterface client) throws RemoteException;
    void heartbeat(int pID) throws RemoteException;
    void sendHeartbeat() throws RemoteException;

    //SERVER ACTIONS
    boolean isAcceptingNewClients() throws RemoteException;
    int getClientsCounter() throws RemoteException;
    void setNumberOfPlayers(int numberOfPlayers) throws RemoteException;
    void tryStartingMatch() throws RemoteException;
    void tryRestartingMatch() throws RemoteException;
    void sendGameInfo() throws RemoteException;
    Message getInitialSituation(int playerID) throws Exception;
    Message getInitialGameInfo() throws Exception;
    ObjectiveCard getPersonalObjective(int playerID) throws RemoteException;
    void sendPublicChat(String sender, int senderId, String message) throws RemoteException;
    void sendPrivateChat(Message message) throws RemoteException;
    Message receiveChat(int playerID) throws Exception;

    //CONTROLLER ACTIONS
    void initializePlayers(int n, String username, PawnColor color) throws RemoteException;
    String[] getPlayerNames() throws RemoteException;
    List<Integer> getRejoinedIndexes() throws RemoteException;
    int[] getAvailablePawnColors() throws RemoteException;
    void addPlayer(String username, PawnColor playerColor) throws RemoteException;
    void placeStartingCardSide(int side, int playerID) throws RemoteException;
    ObjectiveCard[] drawPersonalObjectives() throws RemoteException;
    void setPersonalObjective(int id, ObjectiveCard obj) throws RemoteException;
    Boolean[] getInHandAvailableSides(int playerID) throws RemoteException;
    Message getAvailablePlacements (int playerID, int deployIndex) throws RemoteException;
    PlayableCard playTurn(int playerID, int handIndex, int deployedIndex, int corner, int drawn) throws RemoteException;
    Message[] getGameInfo(int playerID) throws RemoteException;
    boolean getLoaded() throws RemoteException;
    Message getRequiredData(int pick) throws RemoteException;
    String isAlreadyTaken(int pick) throws RemoteException;
    void rejoinPlayer(int pick) throws RemoteException;
}
