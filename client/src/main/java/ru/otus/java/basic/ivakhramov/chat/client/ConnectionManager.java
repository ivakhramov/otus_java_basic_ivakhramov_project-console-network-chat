package ru.otus.java.basic.ivakhramov.chat.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * The ConnectionManager class manages the connection to the chat server.
 * It is responsible for sending and receiving messages, as well as closing the connection.
 */
public class ConnectionManager {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private static Logger LOGGER = LogManager.getLogger(ConnectionManager.class);

    /**
     * Constructor of the ConnectionManager class.
     *
     * @param host server address
     * @param port server port
     */
    public ConnectionManager(String host, int port){
        try {
            socket = new Socket(host, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Connection failed: " + e.getMessage());
            LOGGER.error("Connection failed" + e);
        }
    }

    /**
     * Sends a message to the server.
     *
     * @param message message to send
     */
    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            LOGGER.error("Error sending message" + e);
        }
    }

    /**
     * Receives a message from the server.
     *
     * @return received message
     */
    public String receiveMessage() {
        try {
            return in.readUTF();
        } catch (IOException e) {
            System.err.println("Error receiving message: " + e.getMessage());
            LOGGER.error("Error receiving message" + e);
            return null;
        }
    }

    /**
     * Closes the connection and releases resources.
     */
    public void disconnect() {
        try {
            in.close();
        } catch (IOException e) {
            System.err.println("Error closing input stream: " + e.getMessage());
            LOGGER.error("Error closing input stream", e);
        }

        try {
            out.close();
        } catch (IOException e) {
            System.err.println("Error closing output stream: " + e.getMessage());
            LOGGER.error("Error closing output stream", e);
        }

        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
            LOGGER.error("Error closing socket", e);
        }
    }
}
