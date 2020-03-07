/*
 * By Zion Mantey (zjm160030) and Michael Wilson (mfw150030)
 * CS 4390.002 - Computer Networks | Project
 * Chat System
 */

import java.io.Serializable;

public class Packet implements Serializable {

    // The types of packets that the service supports.
    public enum packetType {
        REQUEST, ACK, NACK, HEARBEAT, MESSAGE, END
    }

    // Stored Variables
    protected packetType type;
    protected final String user;
    protected final String message;

    //used for serialization
    private static final long serialversionUID = 4656772453L;

    // Creates a packet, keeping info of the type of info, the name of the user that sent it, and the message itself.
    public Packet(packetType type, String user, String message) {
        this.type = type;
        this.user = user;
        this.message = message;
    }

    // Allows the packets to be printed as strings, showing the user that send them, and the contents of the message.
    @Override
    public String toString() {
        return user + ": " + message;
    }
}
