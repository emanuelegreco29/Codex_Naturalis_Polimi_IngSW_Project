package it.polimi.ingsw.am43.Network.Socket;


import it.polimi.ingsw.am43.Controller.GameController;
import it.polimi.ingsw.am43.Network.GameServer;
import it.polimi.ingsw.am43.Network.Messages.toClientMessages.endGameMsg;
import it.polimi.ingsw.am43.Network.Messages.toClientMessages.gameInfoMsg;

import java.io.IOException;
import java.net.*;
import java.rmi.ServerException;
import java.util.*;

/**
 * This class represents a server that accepts new connections through TCP
 * Sockets. The server has an IP address and a port, as well as a list of
 * {@link Handler}s to handle the connections with all the {@link SocketClient}s.
 */
public class SocketServer extends Thread /*implements ServerInterface*/ {

    /**
     * A {@link ServerSocket} for accepting new connections.
     */
    private ServerSocket serverSocket;

    /**
     * The default port of the server.
     */
    public static final int PORT = 1098;

    /**
     * A list of connected {@link SocketClient}s, each one
     * handled by a dedicated {@link Handler}.
     */
    private List<Handler> handlers;

    /**
     * A boolean that indicates if the server is still running.
     */
    public boolean isRunning;

    /**
     * A reference to the {@link GameController} that handles the game.
     */
    private GameController gameController;

    /**
     * A reference to the {@link GameServer} that handles the game.
     */
    private GameServer gameServer;

    /**
     * ANSI color codes for various messages printed from the server
     */
    static String YELLOW = "\033[0;33m"; // Used for SERVER messages
    static String RESET = "\033[0m";
    static String RED = "\033[0;31m"; // Used for ERROR

    /**
     * Creates a new {@link SocketServer} with the specified address and port.
     *
     * @param address the address of the server
     * @param port    the port of the server
     * @throws IOException if an I/O error occurs when creating the server
     */
    public SocketServer(String address, int port, GameController game, GameServer gameServer) throws IOException {
        try {
            serverSocket = new ServerSocket(port, 0, InetAddress.getByName(address));
            handlers = new ArrayList<>();
            isRunning = true;

            this.gameController = game;
            this.gameServer = gameServer;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * While the server is still up, listens for changes in the client number (new ones connecting, old ones disconnecting).
     * Adds connected {@link SocketClient} to the list and starts it.
     */
    @Override
    public void run() {
        // While the server is still up
        System.out.println(YELLOW + "[SERVER] Socket Server started." + RESET);
        while (!Thread.currentThread().isInterrupted() && !serverSocket.isClosed() && gameServer.isAcceptingNewClients()){
            // Accept new clients only under condition
            if (gameServer.isAcceptingNewClients()) {
                try {
                    Socket client = this.serverSocket.accept();
                    // Let player join or rejoin, whether the game is loaded or a new one
                    if(!gameController.getLoaded()) {
                        registerClient(client);
                    } else {
                        rejoin(client);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * A function that closes all the {@link Handler}s connected to the server.
     *
     * @throws IOException   If an I/O error occurs when closing the {@link Handler}.
     */
    public void closeClients() throws IOException {
        System.out.println(YELLOW + "[SERVER] Closing all clients..." + RESET);
        //Close all clients
        for (Handler clientHandler: handlers) {
            if(!clientHandler.getClient().isClosed()){
                clientHandler.stopHandler();
            }
        }
        //Check if clients are closed and clear the list
        for (Handler clientHandler: handlers) {
            if (!clientHandler.isClosed() || !clientHandler.getClient().isClosed()) {
                throw new ServerException(RED + "[ERROR] A socket client is still alive" + RESET);
            }
        }
        handlers.clear();
    }

    /**
     * Adds a new client, connected through a socket connection, to the server.
     *
     * @param  client  the client socket to register
     * @throws IOException  if an I/O error occurs while adding the socket client
     */
    private void registerClient(Socket client) throws IOException {
        if(gameServer.isAcceptingNewClients()){
            int id = gameServer.addSocketClient(client);
            // Assign a personal handler to the client
            handlers.add(new Handler(client, id, gameController, gameServer));
            handlers.getLast().start();
            // Avoid to add more players than requested
            if(gameServer.getClientsCounter()==gameServer.getNumberOfPlayers()) gameServer.setAccepting(false);
            // Different messages for first and other players
            if (gameServer.getClientsCounter() == 1) handlers.getLast().firstPlayer();
            else handlers.getLast().generalPlayer();
            System.out.println(YELLOW + "[SERVER] New socket client connected: " + handlers.getLast().getTitle() + RESET);
        }
    }

    /**
     * Rejoins an existing client, connected through a socket connection, to the server.
     *
     * @param  client  the client socket to rejoin
     * @throws IOException  if an I/O error occurs while rejoining the socket client
     */
    private void rejoin(Socket client) throws IOException {
        if(gameServer.isAcceptingNewClients() && gameServer.getClientsCounter() < gameServer.getNumberOfPlayers()){
            int id = gameServer.addSocketClient(client);
            handlers.add(new Handler(client, id, gameController, gameServer));
            handlers.getLast().start();
            handlers.getLast().rejoin();
            System.out.println(YELLOW + "[SERVER] Socket client reconnected: " + handlers.getLast().getTitle() + RESET);
        }
    }

    /**
     * Sends the initial situations to all connected handlers.
     */
    public void sendInitialSituations(){
        for (Handler h : handlers){
            h.initialSituation();
        }
    }

    /**
     * Sends the game information to all connected handlers.
     */
    public void sendGameInfo(gameInfoMsg msg){
        for (Handler h : handlers){
            h.gameInfo(msg);
        }
    }

    public void sendEndGameInfo(endGameMsg msg){
        for (Handler h : handlers){
            h.endGame(msg);
        }
    }

    /**
     * Sends the initial game information to all connected handlers.
     */
    public void sendInitialGameInfo(){
        for (Handler h : handlers){
            h.initialGameInfo();
        }
    }

    /**
     * Sends a public chat message from the sender to all connected clients.
     *
     * @param  sender   the sender of the message
     * @param  senderID the ID of the sender
     * @param  msg      the content of the message
     */
    public void sendPublicChat(String sender, int senderID, String msg) {
        for (Handler h : handlers) {
            if(h.getPlayerID() != senderID) {
                h.publicChatMessage(sender, msg);
            }
        }
    }

    /**
     * Sends a private chat message from the sender to the receiver.
     *
     * @param  sender   the sender of the message
     * @param  receiver the receiver of the message
     * @param  receiverID the ID of the receiver
     * @param  msg      the content of the message
     */
    public void sendPrivateChat(String sender, String receiver, int receiverID, String msg) {
        for (Handler h : handlers) {
            if(h.getPlayerID() == receiverID) {
                h.privateChatMessage(sender, receiver, msg);
            }
        }
    }
}