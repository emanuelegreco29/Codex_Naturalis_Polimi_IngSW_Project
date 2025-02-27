package it.polimi.ingsw.am43.Network.Socket;

import it.polimi.ingsw.am43.Network.ClientInterface;
import it.polimi.ingsw.am43.Network.ConnectionType;
import it.polimi.ingsw.am43.Network.Message;
import it.polimi.ingsw.am43.Network.Messages.controllerMessages.SCMsgHeartbeat;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class implements the client side of the socket connection.
 * It is used to communicate with the server through the Stream I/O.
 */
public class SocketClient implements ClientInterface {

    @Serial
    private static final long serialVersionUID = 3326117972955786133L;

    /**
     * ANSI color codes for various messages printed from the server
     */
    static String RESET = "\033[0m";
    static String RED = "\033[0;31m"; // Used for ERROR

    /**
     * The {@link Socket} used to communicate with the {@link SocketServer}.
     */
    private Socket socket;

    /**
     * The input and output streams.
     */
    private ObjectInputStream in_obj;
    private ObjectOutputStream out_obj;

    /**
     * Constructor of the class.
     *
     * @param address          Is the address of the {@link SocketServer}
     * @param port             Is the port of the {@link SocketServer}
     */
    public SocketClient(String address, int port) {
        try {
            socket = new Socket(address, port);
            out_obj = new ObjectOutputStream(socket.getOutputStream());
            in_obj = new ObjectInputStream(socket.getInputStream());
            new Thread(this::heartbeat).start();
        } catch (IOException e) {
            System.out.println(RED + "Server appears to be down. Please try again later.\nPress enter to exit..." + RESET);
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            scanner.close();
            System.exit(0);
        }
    }

    /**
     * Reads a new message from the input stream.
     *
     * @return         	The message read from the input stream.
     */
    public synchronized Message readNewMessage()  {
        try {
            if(in_obj.available() >= 0){
                return (Message) in_obj.readObject();
            } else {
                return null;
            }
        } catch (IOException | ClassNotFoundException e) {
//            try {
//                killConnection();
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
        }
        return null;
    }

    /**
     * Writes a new message to the output stream.
     *
     * @param msg         The message to be written to the output stream.
     */
    public void writeNewMessage(Message msg){
        try {
            out_obj.writeObject(msg);
            completeSend();
        } catch (IOException e) {
            try {
                killConnection();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Closes the Socket definitely.
     */
    public void killConnection() throws IOException {
        socket.close();
    }

    /**
     * Flushes and resets the {@link ObjectOutputStream} used for sending data.
     *
     * @throws IOException if an I/O error occurs while flushing or resetting the stream.
     */
    private void completeSend() throws IOException {
        out_obj.flush();
        //out_obj.reset();
    }

    /**
     * Sends a {@link SCMsgHeartbeat} to the {@link SocketServer} at regular intervals to keep the connection alive.
     * The function continuously sends a {@link SCMsgHeartbeat} to the server until the {@link ObjectOutputStream}
     * is closed or an IOException occurs. The function sleeps for 1 second between each heartbeat.
     * If an IOException occurs, the connection to the server is lost and the application is exited.
     * If an InterruptedException occurs, it is thrown as a RuntimeException.
     */
    public void heartbeat()  {
        while(out_obj != null) {
            try {
                out_obj.writeObject(new SCMsgHeartbeat());
                completeSend();
                Thread.sleep(1000);
            } catch (IOException e) {
                System.out.println(RED + "Connection to the server lost!\nGame window will close in 10 seconds." + RESET);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        System.exit(1);
                    }
                }, 10000);
                break;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}