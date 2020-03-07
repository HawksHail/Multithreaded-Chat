/*
 * By Zion Mantey (zjm160030) and Michael Wilson (mfw150030)
 * CS 4390.002 - Computer Networks | Project
 * Chat System
 */

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerHeartbeat extends Thread {

    // Stored Variable
    private final Server server;

    // Creates a Heartbeat for the specified server.
    public ServerHeartbeat(Server server) {
        this.server = server;
    }

    // Runs the Heartbeat Signal
    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
        while (true) {	// So long as the heartbeat is running.
            try {
                Thread.sleep(1000 * 90); // Send a heartbeat every 90 seconds.
            } catch (InterruptedException ex) {	// If there is an error with the heartbeat, send an error message.
                Logger.getLogger(ServerHeartbeat.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Checking heartbeat");	// Used for testing - gives a physical heartbeat message in the server.
            server.send(new Packet(Packet.packetType.HEARBEAT, "Server", "Heartbeat"), null);
        }
    }
}
