package ru.otus.java.basic.ivakhramov.chat.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The MessageHandler class handles incoming chat messages.
 * Depending on the type of message, it performs the appropriate actions.
 */
public class MessageHandler {

    private static final Logger LOGGER = LogManager.getLogger(MessageHandler.class);

    /**
     * Processes an incoming message.
     *
     * @param message a string representing the incoming message.
     *                The message must begin with a command, which may be followed by additional information.
     *                For example:
     *                - "/exitok" for exit
     *                - "/authok <username>" for successful authentication
     *                - "/regok <username>" for successful registration
     *                - any other message will be output to console as is.
     */
    public void handler(String message) {
        switch (message.split(" ")[0]) {
            case "/exitok":
                System.out.println("Disconnected from server.");
                LOGGER.info("Disconnected from server.");
                break;
            case "/authok":
                System.out.println("Authentication successful with username: " + message.split(" ")[1]);
                LOGGER.info("Authentication successful with username: " + message.split(" ")[1]);
                break;
            case "/regok":
                System.out.println("Registration was successful with username: " + message.split(" ")[1]);
                LOGGER.info("Registration was successful with username: " + message.split(" ")[1]);
                break;
            default:
                System.out.println(message);
        }
    }
}
