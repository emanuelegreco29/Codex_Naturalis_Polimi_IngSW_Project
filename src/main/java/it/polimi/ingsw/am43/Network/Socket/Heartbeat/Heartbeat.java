package it.polimi.ingsw.am43.Network.Socket.Heartbeat;

import java.util.Timer;

/**
 * This class is used to send a heartbeat message to the server every 1 second.
 */
public class Heartbeat extends Thread {

    /**
     * Runs the heartbeat task in a loop until the thread is interrupted.
     */
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            Timer timer = new Timer();
            timer.schedule(new HeartbeatTask(), 1000);
            timer.cancel();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
        }
    }
}
