package ru.otus.java.basic.ivakhramov.chat.server.command;

import ru.otus.java.basic.ivakhramov.chat.server.client.ClientHandler;
import ru.otus.java.basic.ivakhramov.chat.server.client.ClientManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * The MessageService class is responsible for handling the sending of messages
 * in a chat application. It provides functionality for broadcasting messages
 * to all connected clients and sending private messages between specific clients.
 */
public class MessageService {

    private ClientManager clientManager;

    /**
     * Constructs a MessageService with the specified ClientManager.
     *
     * @param clientManager the ClientManager that manages connected clients
     */
    public MessageService(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    /**
     * Broadcasts a message to all connected clients.
     *
     * @param message the message to be sent to all clients
     */
    public synchronized void broadcastMessage(String message) {
        String timestamp = getCurrentTimestamp();
        String messageWithTimestamp = timestamp + " " + message;

        for (ClientHandler client : clientManager.getClients()) {
            client.sendMessage(messageWithTimestamp);
        }
    }

    /**
     * Sends a private message from one client to another.
     *
     * @param message  the message to be sent
     * @param toName   the nickname of the recipient client
     * @param fromName the nickname of the sender client
     */
    public synchronized void privateMessage(String message, String toName, String fromName) {
        String timestamp = getCurrentTimestamp();
        String messageWithTimestamp = timestamp + " " + message;
        ClientHandler toClient = clientManager.getClientByName(toName);
        ClientHandler fromClient = clientManager.getClientByName(fromName);

        if (toClient != null) {
            toClient.sendMessage(messageWithTimestamp);
            fromClient.sendMessage(messageWithTimestamp);
        } else if (fromClient != null) {
            fromClient.sendMessage("User with nickname " + toName + " does not exist");
        }
    }

    /**
     * Broadcasts the list of active clients to all connected clients.
     */
    public synchronized void broadcastActiveClients() {
        List<String> activeClients = clientManager.getActiveClientNames();
        String message = "Active clients: " + activeClients.toString();
        broadcastMessage(message);
    }

    /**
     * Gets the current timestamp in the format "yyyy-MM-dd HH:mm:ss".
     *
     * @return the current timestamp as a String
     */
    private String getCurrentTimestamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }
}
