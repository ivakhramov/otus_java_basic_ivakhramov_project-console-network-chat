package ru.otus.java.basic.ivakhramov.chat.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * The ClientApplication class is the entry point to the chat client application.
 * It initializes the client by establishing a connection to the server on the specified host and port.
 */
public class ClientApplication {

    private static String host = "localhost";
    private static int port = 8189;
    private static final Logger LOGGER = LogManager.getLogger(ClientApplication.class);

    /**
     * The main method of the application that the client launches.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            new Client(host, port);
        } catch (IOException e) {
            System.err.println("Error initializing client: " + e.getMessage());
            LOGGER.error("Error initializing client", e);
        }
    }
}
