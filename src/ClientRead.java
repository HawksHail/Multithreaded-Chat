/*
 * By Zion Mantey (zjm160030) and Michael Wilson (mfw150030)
 * CS 4390.002 - Computer Networks | Project
 * Chat System
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientRead extends Thread {

    // Stored Variables
    private ObjectInputStream reader;
    private final Socket socket;
    private final Client client;

    // Creates an object that reads user input from a specified client, given a socket with their message.
    public ClientRead(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;
        // If the connection is successful, creates a Stream to read messages from. Otherwise, give an error message.
        try {
            reader = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.out.println("Error getting input stream " + ex.getMessage());
        }
    }

    // Runs the Client Reader
    @Override
    public void run() {
        boolean run = true; // Runs until specified otherwise. A constant loop looking for specific inputs.
        while (run) {
            try {
                Packet response = (Packet) reader.readObject();
//                System.out.println("\nPACKET " + response.type);
                switch (response.type) {
                    case REQUEST:	// Break on REQUEST, ACK, or NACK. We don't need to do anything special with them.
                        break;
                    case ACK:
                        System.out.println("\nConnected to the server!");
                        if (client.getUsername() != null) {     // replace username prompt
                            System.out.print(client.getUsername() + ": ");
                        }
                        break;
                    case NACK:
                        break;
                    case HEARBEAT:  // Hearbeats check to make sure the connection is fine.
//                        System.out.println("\n" + response);
                        client.write.write(new Packet(Packet.packetType.HEARBEAT, client.getUsername(), "heartbeat"));
                        break;
                    case MESSAGE:   // Reads a message and prints it with the username of the user that sent it.
                        System.out.println("\n" + response);
                        if (client.getUsername() != null) {     // replace username prompt
                            System.out.print(client.getUsername() + ": ");
                        }
                        break;
                    case END:	// Ends the loop and closes the client.
                        run = false;
                        break;
                    default:	// Error message for an unknown packet type.
                        System.out.println("\nUnknown packet: " + response);
                }

            } catch (IOException ex) {
//                Logger.getLogger(ClientRead.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("\nServer shutdown unexpectedly");
                run = false;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClientRead.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassCastException ex) {
                System.out.println("Error reading packet");
            }
        }
        try {	// Once done, try to close the socket, or print an error saying it couldn't happen.
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientRead.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.exit(0);	// Closes the client when finished.
    }
}
