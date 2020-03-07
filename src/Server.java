/*
 * By Zion Mantey (zjm160030) and Michael Wilson (mfw150030)
 * CS 4390.002 - Computer Networks | Project
 * Chat System
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    // Stored Variables
    private final int port;
    private final List<ServerHandler> userThreads;

    // Creates a Server with the specified Port Number. Keeps a list of Threads, with each user being it's own thread.
    public Server(int port) {
        this.userThreads = new ArrayList<>();
        this.port = port;
    }

    // Runs the Server.
    public void execute() {
        // Tries to run the server unless it can't, then an error is given out.
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port " + port);	// Connection verification message.
            new ServerHeartbeat(this).start();	// Begins keeping track of the hearbeat. Used to make sure all users stay connected to the server.
            while (true) {  // Endless loop so long as the server needs to be running.
                Socket socket = serverSocket.accept();	// Accepts sockets when given. Shows when a user is able to successfully connect.
                System.out.println("User connected");

                ServerHandler user = new ServerHandler(socket, this);    // Creates a Server Handler to take care of the user, then adds as a thread to the user list, then starts it.
                userThreads.add(user);
                user.start();
            }
        } catch (IOException ex) {
            System.out.println("Error accepting " + ex.getMessage());
        }
    }

    // Removes a User from the Thread List when they disconnect in any way.
    public void removeUser(ServerHandler remove) {
        userThreads.remove(remove);
    }

    /**
     * Sends message to all users but the specified one
     */
    void send(Packet message, ServerHandler user) {
//        System.out.println("SENDING " + message + " " + message.type);
        for (ServerHandler User : userThreads) {
            if (User != user) {
                User.sendMessage(message);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    // The Main Method
    public static void main(String[] args) {
        if (args.length < 1) {	// Takes the given parameter and uses it as the port number. Otherwise, this doesn't work.
            System.out.println("Port number needed");
            return;
        }

        // Set port, creates, and launches a server with the given port number.
        int port = Integer.parseInt(args[0]);
        Server server = new Server(port);
        server.execute();
    }
}
