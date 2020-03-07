/*
 * By Zion Mantey (zjm160030) and Michael Wilson (mfw150030)
 * CS 4390.002 - Computer Networks | Project
 * Chat System
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerHandler extends Thread {

    // Stored Variables
    private final Socket socket;
    private final Server server;
    private ObjectOutputStream writer;

    // Creates a Server Handler with the specified socket for the specified server.
    public ServerHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    // Runs the Server Handler
    @Override
    public void run() {
        // Tries to run when possible, gives an error if something goes wrong.
        try {
            ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());	// Creates a stream to read socket objects from an Input Stream.

            writer = new ObjectOutputStream(socket.getOutputStream());	// Allows the server to read data from the output stream.
            Packet serverPacket, clientPacket;	// Stored packets from the User and the Server.
            String username = "NEW USER";   // Default Username Set
            do {
                clientPacket = (Packet) reader.readObject();	// Reads in the packet sent by the reader.
//                System.out.println("RECIEVED PACKET " + clientPacket);
                switch (clientPacket.type) {	// Makes choices based on the type of packet received.
                    case REQUEST:   // If a request is sent, changes the default username to the username that sent the packet, shows they connected, and sends that into to the server.
                        username = clientPacket.user;
                        serverPacket = new Packet(Packet.packetType.MESSAGE, "New user connected", username);
                        server.send(serverPacket, this);
                        sendMessage(new Packet(Packet.packetType.ACK, "Server", "Connected!"));
                        break;
                    case ACK:	// Break on ACK or NACK. We don't need to do anything special with them.
                        break;
                    case NACK:
                        break;
                    case HEARBEAT:  // Used to make sure that everything is still connected together.
                        serverPacket = new Packet(Packet.packetType.MESSAGE, "Server", username + " is still connected");
                        System.out.printf("Recived heartbeat from %s%n", username);
                        server.send(serverPacket, this);
                        break;
                    case MESSAGE:   // If a message is sent, sends it to all the other clients.
                        server.send(clientPacket, this);
                        break;
                    case END:	// If an end request is sent, shows who disconnected, removes that user from the server, and prints their leave.
                        serverPacket = new Packet(Packet.packetType.MESSAGE, username, "has disconnected");
                        System.out.printf("%s has disconnected%n", username);
                        server.send(serverPacket, this);
                        server.removeUser(this);
                        writer.writeObject(new Packet(Packet.packetType.END, username, "disconnect"));
                        break;
                    default:	// Case of an unknown packet.
                        System.out.println("Unknown packet");
                }

            } while (clientPacket.type != Packet.packetType.END);   // Loops until a END request is sent.
        } catch (IOException ex) {
            System.out.println("User disconnected unexpectedly");
            server.send(new Packet(Packet.packetType.MESSAGE, "Server", "A client disconnected unexpectedly."), this);
//            System.out.println("Error in UserThread " + ex.getMessage());
//            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally { // After everything is done, close the sockets, and remove this from the remaining users. If there is an error, print it.
            server.removeUser(this);
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Sends a message to the client.
     */
    // Sends a message to each chat client.
    void sendMessage(Packet message) {
        try {
            writer.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
