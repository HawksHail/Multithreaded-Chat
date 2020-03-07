/*
 * By Zion Mantey (zjm160030) and Michael Wilson (mfw150030)
 * CS 4390.002 - Computer Networks
 * Chat System Project
 */

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    // Stored Variables
    private final String hostname;
    private final int port;
    private String username;
    public ClientWrite write;

    // Creates a client with a specified host (IP of the Server) and a specified port number to connect with.
    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    // Runs the client created. Allows interaction with the server.
    public void execute() {
        // Tries to do what it can, gives errors when something fails somewhere.
        try {
            // Creates a socket with the given information.
            Socket socket = new Socket(hostname, port);
            System.out.println("Socket connected");

            // Allows the user to start inputing and reading chat messages with the server.
            write = new ClientWrite(socket, this);
            write.start();
            new ClientRead(socket, this).start();
        } catch (UnknownHostException ex) {
            System.out.println("Server not found " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error " + ex.getMessage());
        }

    }

    // Stores the username of the user - used as an identifier for the chat client.
    void setUsername(String username) {
        this.username = username;
    }

    // Returns the username of the user that clals the command.
    String getUsername() {
        return this.username;
    }

    // Main Method
    public static void main(String[] args) {
        if (args.length < 2) {	// 2 variables must be given. Otherwise, the client will not be able to connect. IP first, followed by port number.
            System.out.println("IP and port number needed");
            return;
        }

        // Stores the given variables to their proper storage.
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        // Creates and launches the client using the given info.
        Client client = new Client(hostname, port);
        client.execute();
    }
}
