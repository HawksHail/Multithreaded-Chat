/*
 * By Zion Mantey (zjm160030) and Michael Wilson (mfw150030)
 * CS 4390.002 - Computer Networks | Project
 * Chat System
 */

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientWrite extends Thread {

    // Stored Variables
    private ObjectOutputStream writer;
    private final Socket socket;
    private final Client client;

    // Creates a Client Writer that with the specified socket and user.
    public ClientWrite(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;
        // If the connection is successful, creates a Stream to send messages with. Otherwise, give an error message.
        try {
            writer = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
        }
    }

    // Creates a writing object. Writes the message of the given packet.
    public void write(Packet message) {
        try {
            writer.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ClientWrite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Runs the Client Writer
    @Override
    public void run() {
        Scanner in = new Scanner(System.in);

        // Takes and sets the username specified by the user.
        System.out.print("\nEnter your username: ");
        String username = in.nextLine();
        client.setUsername(username);
        try {
            // Creates an object that allows the client to send messages in the form of packets.
            writer.writeObject(new Packet(Packet.packetType.REQUEST, username, "connect"));

            String text;    // The message sent by the user is stored here.
            do {
                System.out.print(username + ": ");  // Propts user to type with the client's given username.
                System.out.flush(); // Sends out any username characters waiting to be sent immediately.
                text = in.nextLine();	// Takes the input types into the line.
                if (text.equals(".quit")) { // Ends the chat if the user types in '.quit' by sending END packet
                    writer.writeObject(new Packet(Packet.packetType.END, username, text));
                } else {    // Otherwise, sends the typed info to the server as a packet message.
                    writer.writeObject(new Packet(Packet.packetType.MESSAGE, username, text));
                }

            } while (true);	// Loops forever
        } catch (IOException ex) {
            Logger.getLogger(ClientWrite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
