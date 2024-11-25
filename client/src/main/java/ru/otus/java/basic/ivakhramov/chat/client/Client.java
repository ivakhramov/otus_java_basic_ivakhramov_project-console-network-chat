package ru.otus.java.basic.ivakhramov.chat.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Scanner;

/**
 * The Client class represents the client part of the chat application.
 * It is responsible for sending and receiving messages from the server.
 */
public class Client {

    private MessageHandler messageHandler;
    private ConnectionManager connectionManager;
    private boolean isConnected;
    private static final Logger LOGGER = LogManager.getLogger(Client.class);

    /**
     * Constructor of the Client class.
     *
     * @param host server address
     * @param port server port
     * @throws IOException if connection to the server could not be established
     */
    public Client(String host, int port) throws IOException {
        Scanner scanner = new Scanner(System.in);
        messageHandler = new MessageHandler();
        connectionManager = new ConnectionManager(host, port);
        isConnected = true;


        new Thread(() -> messageListener()).start();

        while (true) {
            String message = scanner.nextLine();
            connectionManager.sendMessage(message);
            if (message.startsWith("/exit")) {
                connectionManager.disconnect();
                isConnected = false;
                break;
            }
        }
    }

    /**
     * Method for listening for incoming messages from the server.
     * Messages are processed using MessageHandler.
     */
    private void messageListener() {
        try {
            while (isConnected) {
                String message = connectionManager.receiveMessage();
                if (message != null) {
                    messageHandler.handler(message);
                } else {
                    System.err.println("Server disconnected.");
                    LOGGER.info("Server disconnected.");
                    isConnected = false;
                }
            }
        } finally {
            connectionManager.disconnect();
        }
    }
}
