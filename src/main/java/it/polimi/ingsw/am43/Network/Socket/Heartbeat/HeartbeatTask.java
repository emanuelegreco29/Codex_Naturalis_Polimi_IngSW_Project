package it.polimi.ingsw.am43.Network.Socket.Heartbeat;

import java.util.TimerTask;

/**
 * This class is used to send heartbeat messages to the server.
 */
public class HeartbeatTask extends TimerTask {

    /**
     * Overrides the run method from the TimerTask class to print "Heartbeat!" to the console.
     */
    @Override
    public void run() {
        System.out.println("Heartbeat!");
    }
}