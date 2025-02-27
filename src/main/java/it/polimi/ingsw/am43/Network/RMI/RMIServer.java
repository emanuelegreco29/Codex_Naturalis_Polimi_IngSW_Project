package it.polimi.ingsw.am43.Network.RMI;

import it.polimi.ingsw.am43.Controller.GameController;
import it.polimi.ingsw.am43.Model.Cards.ObjectiveCard;
import it.polimi.ingsw.am43.Model.Cards.PlayableCard;
import it.polimi.ingsw.am43.Model.Enum.PawnColor;
import it.polimi.ingsw.am43.Network.ClientInterface;
import it.polimi.ingsw.am43.Network.GameServer;
import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.toClientMessages.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class RMIServer extends UnicastRemoteObject implements RMIServerInterface {
    static String RED = "\033[0;31m"; // Used for ERROR

    private final GameServer gameServer;
    private final GameController gameController;
    private boolean serverRunning;
    private final Map<Integer, Long> clientHeartbeats;


    long HEARTBEAT_TIMEOUT = 3000;

    public RMIServer(GameServer gameServer, GameController controller) throws RemoteException {
        super();
        this.gameServer = gameServer;
        this.gameController = controller;
        this.serverRunning = true; // Assume server is initially running
        this.clientHeartbeats = new HashMap<>();
    }

    /**
     * Registers the client and its heartbeats, and adds it as a new player for the {@link GameServer}.
     *
     * @param clientCallback is the remote instance of the client.
     * @return the player ID if registration is successful, -1 otherwise.
     */
    @Override
    public synchronized int registerClient(ClientInterface clientCallback) throws RemoteException {
        if (isServerRunning()) {
            final int pID = gameServer.addRMIClient(clientCallback.hashCode());
            clientHeartbeats.put(pID, System.currentTimeMillis());
            return pID;
        } else {
            // Handle registration when server is not running
            System.out.println(RED +  "RMI Server is down. Unable to register client.");
        }
        return -1;
    }

    /**
     * Logs a new heartbeat for a player.
     *
     * @param pID is the player ID that sends the heartbeat
     */
    @Override
    public synchronized void heartbeat(int pID) throws RemoteException {
        clientHeartbeats.put(pID, System.currentTimeMillis());
    }

    /**
     * @return if server is running
     */
    public synchronized boolean isServerRunning() {
        return serverRunning;
    }

    /**
     * set server as running or not.
     */
    public synchronized void setServerRunning(boolean serverRunning) {
        this.serverRunning = serverRunning;
    }

    /**
     * @return whether the {@link GameServer} is accepting new clients.
     */
    @Override
    public boolean isAcceptingNewClients() throws RemoteException {
        return gameServer.isAcceptingNewClients();

    }

    /**
     * @return the number of clients in the {@link GameServer}.
     */
    public int getClientsCounter() throws RemoteException {
        return gameServer.getClientsCounter();
    }

    /**
     * Sets the number of players for the game. Called when the first player sets the game up.
     */
    @Override
    public void setNumberOfPlayers(int numberOfPlayers) throws RemoteException {
        gameServer.setNumberOfPlayers(numberOfPlayers);
    }

    /**
     * Gives the {@link GameServer} a request to try starting the match.
     */
    @Override
    public void tryStartingMatch() throws RemoteException {
        gameServer.tryStartingMatch();
    }

    /**
     * Gives the {@link GameServer} a request to try starting the match.
     */
    @Override
    public void tryRestartingMatch() throws RemoteException {
        gameServer.tryRestartingMatch();
    }

    /**
     * Requests a {@link gameInfoMsg} from the server.
     */
    @Override
    public void sendGameInfo() throws RemoteException {
        gameServer.sendGameInfo();
    }

    /**
     * @param pick is the playerID picked by the player.
     * @return all the initial data to start a loaded game.
     */
    @Override
    public requiredDataMsg getRequiredData(int pick) throws RemoteException {
        return new requiredDataMsg(gameController.getPlayers()[pick].getInHand(),
                gameController.getPlayers()[pick].getStartingCard(),
                gameController.getOnGround(),
                gameController.getCommonObjectives(),
                gameController.getPlayers()[pick].getPersonalObjective(),
                gameController.getScores(),
                gameController.getAllPlacements(),
                gameController.getAllDeployed(),
                gameController.getUsersColors(),
                gameController.getPlayerNames(),
                gameController.getNumPlayers(),
                gameController.getFirstPlayer());
    }

    /**
     * Try to rejoin an existing game with {@param pick} as the selected player ID.
     */
    @Override
    public void rejoinPlayer(int pick) throws RemoteException {
        gameController.rejoinPlayer(pick);
    }

    /**
     * Checks if {@param pick} is already taken and {@return} the username of the picked index if it's free.
     */
    @Override
    public String isAlreadyTaken(int pick) throws RemoteException {
        String username = gameController.getPlayerNames()[pick];
        if (!gameController.getRejoinedPlayers().contains(username)) {
            return username;
        }
        return null;
    }

    /**
     * Tries to fetch game info for the {@param playerID} from the {@link GameController}.
     *
     * @return a {@link gameInfoMsg} if there's new info,
     * with also a {@link endGameMsg} if the game is in the final turn.
     * @throws RemoteException if there's no new info.
     */
    // Se non sta finendo il gioco manda solo gameInfo, altrimenti manda sia gameInfo che endGame
    public Message[] getGameInfo(int playerID) throws RemoteException {
        if (gameServer.hasNewInfo(playerID)) {
            gameInfoMsg gameInfo = gameController.getGameInfo();
            if (gameInfo.getLastOnGroundIndex() == -1) {
                return new Message[]{gameInfo, gameServer.getEndGameInfo()};
            }
            return new Message[]{gameInfo};
        } else {
            throw new RemoteException("Game not updated yet");
        }
    }

    /**
     * @return whether the game is loaded from an existing one or was created new.
     */
    @Override
    public boolean getLoaded() throws RemoteException {
        return gameController.getLoaded();
    }

    /**
     * Checks if the game has started.
     *
     * @return the {@link initialSituation} for {@param playerId} if the game has started.
     * @throws Exception otherwise.
     */
    @Override
    public Message getInitialSituation(int playerID) throws Exception {
        if (!gameServer.isAcceptingNewClients() && gameController.getGame().getCommonObjectives()[1] != null) {
            return gameController.getInitialSituation(playerID);
        } else {
            throw new Exception("Game not started yet");
        }
    }

    /**
     * @return the Personal objective for {@param playerID}.
     */
    @Override
    public ObjectiveCard getPersonalObjective(int playerID) throws RemoteException {
        return gameController.getPersonalObjective(playerID);
    }

    /**
     * Sends a new public chat message.
     *
     * @param sender   is the sender username.
     * @param senderId is the sender's ID.
     * @param message  is the text message.
     */
    @Override
    public void sendPublicChat(String sender, int senderId, String message) throws RemoteException {
        gameServer.sendPublicChat(sender, senderId, message);
    }

    /**
     * Sends a new private chat message
     *
     * @param msg contains the sender's username, the receiver's username and ID, and the text message.
     */
    @Override
    public void sendPrivateChat(Message msg) throws RemoteException {
        privateChatMessageMsg pvtChat = (privateChatMessageMsg) msg;
        int receiverID = gameController.getPlayerID(pvtChat.getReceiver());
        gameServer.sendPrivateChat(pvtChat.getSender(), pvtChat.getReceiver(), receiverID, pvtChat.getMessage());
    }

    /**
     * Fetches for new messages for {@param playerID}
     *
     * @return the oldest unread message.
     */
    @Override
    public Message receiveChat(int playerID) throws Exception {
        Message msg = gameServer.getMessage(playerID);
        if (msg == null) {
            throw new Exception("No message received");
        }
        return msg;
    }

    /**
     * Checks if the game has started.
     *
     * @return the {@link initialGameInfoMsg} if the game has started.
     * @throws Exception otherwise.
     */
    @Override
    public Message getInitialGameInfo() throws Exception {
        if (gameServer.getChosenObjectivesCounter() == gameServer.getNumberOfPlayers()) {
            return gameController.getInitialGameInfo();
        } else {
            throw new Exception("Game not started yet");
        }
    }

    /**
     * Initializes the player slots at game setup by the first player.
     *
     * @param n        is the number of players.
     * @param username belongs to the first player.
     * @param color    belongs to the first player.
     */
    @Override
    public void initializePlayers(int n, String username, PawnColor color) throws RemoteException {
        gameController.initializePlayers(n, username, color);
    }

    /**
     * @return the names of the players joined.
     */
    @Override
    public String[] getPlayerNames() throws RemoteException {
        return gameController.getPlayerNames();
    }

    /**
     * @return the indexes of the players already rejoined.
     */
    public List<Integer> getRejoinedIndexes() throws RemoteException {
        return gameController.getRejoinedIndexes();
    }

    /**
     * @return the pawn colors still available
     */
    @Override
    public int[] getAvailablePawnColors() throws RemoteException {
        return gameController.getAvailablePawnColors();
    }

    /**
     * Add a new player to {@link GameController}, with {@param username} and {@param playerColor}
     */
    @Override
    public void addPlayer(String username, PawnColor playerColor) throws RemoteException {
        gameController.addPlayer(username, playerColor);
    }

    /**
     * Tries to place {@param side} as the visible side of the starting card for the player {@param playerID}.
     */
    @Override
    public void placeStartingCardSide(int side, int playerID) throws RemoteException {
        gameController.placeStartingCardSide(side, playerID);
    }

    /**
     * @return the two possible choices for a personal objective drawn from the {@link GameController}
     */
    @Override
    public ObjectiveCard[] drawPersonalObjectives() throws RemoteException {
        return gameController.drawPersonalObjectives();
    }

    /**
     * Sends {@link GameServer} the chosen personal objective for player {@param id} and {@param obj} as the choice.
     */
    @Override
    public void setPersonalObjective(int id, ObjectiveCard obj) throws RemoteException {
        gameController.setPersonalObjective(id, obj);
        gameServer.incrementChosenObjectivesCounter();
    }

    /**
     * @return from {@link GameController} the available card sides in {@param playerID}'s hand.
     */
    @Override
    public Boolean[] getInHandAvailableSides(int playerID) throws RemoteException {
        return gameController.getInHandAvailableSides(playerID);
    }

    /**
     * @return the available placements from {@link GameController} for {@param playerID} at {@param deployIndex}.
     */
    @Override
    public availablePlacementsMsg getAvailablePlacements(int playerID, int deployIndex) throws RemoteException {
        return new availablePlacementsMsg(
                gameController.getAvailablePlacements(playerID, deployIndex),
                gameController.isResEmpty(),
                gameController.isGoldEmpty()
        );
    }

    /**
     * @return the result from the turn played by {@param playerID} by placing the card at {@param handIndex},
     * on corner {@param corner} of the card at {@param deployedIndex}, and drawing the card {@param drawn}.
     */
    @Override
    public PlayableCard playTurn(int playerID, int handIndex, int deployedIndex, int corner, int drawn) throws RemoteException {
        return gameController.playTurn(playerID, handIndex, deployedIndex, corner, drawn);
    }

    /**
     * Sends a heartbeat to the client, with an exception if the closing call is active.
     */
    public void sendHeartbeat() throws RemoteException {
        if (gameServer.rmiHasToClose) {
            throw new RemoteException("Server is down");
        }
    }

    /**
     * Checks, for each {@link RMIClient}, how much time has passed since last heartbeat.
     * If more than 3 seconds have passed, shut {@link GameServer} and all clients down.
     */
    public void checkClientHeartbeats() throws RemoteException {
        long currentTime = System.currentTimeMillis();
        for (int pID : clientHeartbeats.keySet()) {
            long lastHeartbeat = clientHeartbeats.get(pID);
            if (currentTime - lastHeartbeat > HEARTBEAT_TIMEOUT) {
                System.err.println(RED + "Client " + pID + " disconnected.\nSystem shut down.");
                setServerRunning(false);
                gameServer.stop();
            }
        }
    }
}
