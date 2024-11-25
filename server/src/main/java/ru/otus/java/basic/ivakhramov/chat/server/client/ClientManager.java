package ru.otus.java.basic.ivakhramov.chat.server.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * The ClientManager class is responsible for managing connected clients in a chat server.
 * It provides methods to subscribe, unsubscribe, and manage client connections.
 */
public class ClientManager {

    private List<ClientHandler> clients;
    private static final Logger LOGGER = LogManager.getLogger(ClientManager.class);

    /**
     * Returns the list of connected clients.
     *
     * @return List of ClientHandler objects representing connected clients.
     */
    public List<ClientHandler> getClients() {
        return clients;
    }

    /**
     * Constructs a new ClientManager instance and initializes the clients list.
     */
    public ClientManager() {
        clients = new ArrayList<>();
    }

    /**
     * Subscribes a new client to the manager.
     *
     * @param clientHandler The ClientHandler instance representing the client to be added.
     */
    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    /**
     * Unsubscribes a client from the manager.
     *
     * @param clientHandler The ClientHandler instance representing the client to be removed.
     */
    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    /**
     * Disconnects a user by name from the server.
     * Sends a disconnection message to the user and removes them from the client list.
     *
     * @param name The name of the user to be disconnected.
     */
    public synchronized void kickUser(String name) {
        for (ClientHandler client : clients) {
            if (client.getUser().getName().equals(name)) {
                client.sendMessage("You have been disconnected from the server by the administrator.");
                client.disconnect();
                break;
            } else {
                System.out.println("User not found for disconnection");
                LOGGER.info("User not found for disconnection");
            }
        }
    }

    /**
     * Retrieves a ClientHandler by the user's name.
     *
     * @param name The name of the user whose ClientHandler is to be retrieved.
     * @return The ClientHandler instance if found, otherwise null.
     */
    public synchronized ClientHandler getClientByName(String name) {
        for (ClientHandler client : clients) {
            if (client.getUser().getName().equals(name)) {
                return client;
            }
        }
        return null;
    }

    /**
     * Retrieves the user ID of a client by their name.
     *
     * @param name The name of the user whose ID is to be retrieved.
     * @return The user ID if found, otherwise -1.
     */
    public synchronized int getUserIdByName(String name) {
        ClientHandler client = getClientByName(name);
        return (client != null) ? client.getUser().getId() : -1;
    }

    /**
     * Checks if a given name is already in use by a connected client.
     *
     * @param name The name to check for availability.
     * @return true if the name is busy, otherwise false.
     */
    public synchronized boolean isNameBusy(String name) {
        return getClientByName(name) != null;
    }

    /**
     * Retrieves the names of all active clients currently connected to the chat server.
     *
     * @return a List of Strings containing the names of active clients
     */
    public List<String> getActiveClientNames() {
        List<String> activeClientNames = new ArrayList<>();
        for (ClientHandler client : clients) {
            activeClientNames.add(client.getUser().getName());
        }
        return activeClientNames;
    }
}
