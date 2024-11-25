package ru.otus.java.basic.ivakhramov.chat.server.command;

import ru.otus.java.basic.ivakhramov.chat.server.client.ClientHandler;
import ru.otus.java.basic.ivakhramov.chat.server.client.ClientManager;
import ru.otus.java.basic.ivakhramov.chat.server.model.EnumRole;

/**
 * The CommandProcessor class is responsible for processing commands received from clients in a chat application.
 * It interprets commands that start with a '/' and executes the corresponding actions, such as changing names,
 * roles, sending private messages, and more.
 */
public class CommandProcessor {

    private ClientManager clientManager;
    private ClientHandler clientHandler;

    /**
     * Constructs a CommandProcessor with the specified ClientManager and ClientHandler.
     *
     * @param clientManager the ClientManager instance to manage clients
     * @param clientHandler the ClientHandler instance to handle client communication
     */
    public CommandProcessor(ClientManager clientManager, ClientHandler clientHandler) {
        this.clientManager = clientManager;
        this.clientHandler = clientHandler;
    }

    /**
     * Processes a command received from a client.
     *
     * @param message the command message to process
     */
    public void processCommand(String message) {
        clientHandler.updateLastActiveTime();
        if (message.startsWith("/")) {

            String[] substrings = message.split(" ");

            switch (substrings[0]) {
                case "/changeName":
                    changeName(substrings);
                    break;
                case "/getName":
                    getName();
                    break;
                case "/changeRole":
                    changeRole(substrings);
                    break;
                case "/getRole":
                    getRole();
                    break;
                case "/getActiveClients" :
                    getActiveClients();
                    break;
                case "/w" :
                    privateMessage(substrings);
                    break;
                case "/kick" :
                    kick(substrings);
                    break;
                case "/help" :
                    help();
                    break;
                case "/exit" :
                    exit();
                    break;
            }
        } else {
            clientHandler.getMessageService().broadcastMessage(clientHandler.getUser().getName() + " : " + message);
        }
    }

    /**
     * Changes the nickname of the user.
     *
     * @param substrings the command arguments, where substrings[1] is the new name
     */
    private void changeName(String[] substrings) {
        if (substrings.length != 2) {
            clientHandler.sendMessage("Invalid command format /changeName ");
            return;
        }

        clientHandler.getUser().setName(substrings[1]);
        clientHandler.getDatabaseManager().updateName(clientHandler.getUser().getId(), substrings[1]);
        clientHandler.sendMessage("Your new nickname: " + clientHandler.getUser().getName());
    }

    /**
     * Sends the current nickname of the user.
     */
    private void getName() {
        clientHandler.sendMessage("Your nickname: " + clientHandler.getUser().getName());
    }

    /**
     * Changes the role of a user if the command issuer is an administrator.
     *
     * @param substrings the command arguments, where substrings[1] is the target user's name and substrings[2] is the new role
     */
    private void changeRole(String[] substrings) {
        if (substrings.length != 3) {
            clientHandler.sendMessage("Invalid command format /changeRole ");
            return;
        }

        if (!clientHandler.isRoleAdmin()) {
            clientHandler.sendMessage("You are not an administrator and cannot change user roles.");
            return;
        }

        if (!clientHandler.isNameExist(substrings[1])) {
            clientHandler.sendMessage("User with nickname " + substrings[1] + " not registered in chat");
            return;
        }

        if (substrings[2].equals("ADMIN")) {
            clientManager.getClientByName(substrings[1]).getUser().addRoleToRoles(EnumRole.ADMIN);
            clientHandler.getDatabaseManager().addRole(clientManager.getUserIdByName(substrings[1]), EnumRole.ADMIN);
            clientHandler.sendMessage("Now at " + substrings[1] + " there is a role/roles " + clientManager.getClientByName(substrings[1]).getUser().getRoles());
        } else if (substrings[2].equals("USER")) {
            clientManager.getClientByName(substrings[1]).getUser().removeRoleFromRoles(EnumRole.ADMIN);
            clientHandler.getDatabaseManager().deleteRole(clientManager.getUserIdByName(substrings[1]), EnumRole.ADMIN);
            clientHandler.sendMessage("Now at " + substrings[1] + " there is a role/roles " + clientManager.getClientByName(substrings[1]).getUser().getRoles());
        } else {
            clientHandler.sendMessage("The role you specified \"" + substrings[2] + "\" does not exist");
        }
    }

    /**
     * Sends the current role(s) of the user.
     */
    private void getRole() {
        clientHandler.sendMessage("Your role/roles: " + clientHandler.getUser().getRoles());
    }

    /**
     * Retrieves and broadcasts the list of active clients to all connected clients.
     */
    private void getActiveClients() {
        clientHandler.getMessageService().broadcastActiveClients();
    }

    /**
     * Sends a private message to another user.
     *
     * @param substrings the command arguments, where substrings[1] is the recipient's name and the rest are the message body
     */
    private void privateMessage(String[] substrings) {
        if (substrings.length < 3) {
            clientHandler.sendMessage("Invalid command format /w ");
            return;
        }

        String privateName = substrings[1];

        String bodyMessage = "";
        for (int i = 2; i < substrings.length; i++) {
            bodyMessage += (substrings[i] + " ");
        }

        clientHandler.getMessageService().privateMessage(clientHandler.getUser().getName() + " : " + bodyMessage, privateName, clientHandler.getUser().getName());
    }

    /**
     * Kicks a user from the chat if the command issuer is an administrator.
     *
     * @param substrings the command arguments, where substrings[1] is the name of the user to be kicked
     */
    private void kick(String[] substrings) {
        if (substrings.length != 2) {
            clientHandler.sendMessage("НInvalid command format /kick ");
            return;
        }

        if (!clientHandler.isRoleAdmin()) {
            clientHandler.sendMessage("You are not an administrator and cannot remove users from the chat.");
            return;
        }

        if (!clientHandler.isNameExist(substrings[1])) {
            clientHandler.sendMessage("User with nickname " + substrings[1] + " not registered in chat");
            return;
        }

        clientManager.kickUser(substrings[1]);
        clientHandler.sendExitok(substrings[1]);
        clientHandler.sendMessage("Client with nickname " + substrings[1] + " disconnected from chat");
    }

    /**
     * Sends a list of available commands to the user.
     */
    private void help() {
        clientHandler.sendMessage("You can use the following commands:\n" +
                "/auth \"login\" \"password\" - authenticate\n" +
                "/reg \"login\" \"password\" \"name\" - register\n" +
                "/changeName \"name\" - change name\n" +
                "/getName - find out name\n" +
                "/changeRole \"name\" \"ADMIN/USER\"- change role (if you are an administrator)\n" +
                "/getRole - find out role/roles\n" +
                "/getActiveClients - get a list of active clients\n" +
                "/w \"name\" \"сообщение\" - send message to user with nickname \"name\"\n" +
                "\"message\" - send message to all users\n" +
                "/kick \"name\" - remove user from chat (if you are an administrator)\n" +
                "/exit - exit program\n" +
                "/help - list of commands");
    }

    /**
     * Exits the chat for the current user.
     */
    private void exit() {
        clientHandler.sendExitok(clientHandler.getUser().getName());
    }
}
