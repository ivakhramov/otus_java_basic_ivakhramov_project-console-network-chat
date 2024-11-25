package ru.otus.java.basic.ivakhramov.chat.server;

import ru.otus.java.basic.ivakhramov.chat.server.database.Database;

/**
 * The main class of the chat server application.
 * This class is responsible for initializing and starting the server.
 */
public class ServerApplication {

    /**
     * Entry point to the application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        int port = 8189;
        String url = "jdbc:postgresql://localhost:5432/otus_java_basic_ivakhramov_project_console_network_chat";
        String user = "postgres";
        String password = "qwerty";

        new Server(port, new Database(url, user, password).getConnection()).start();
    }
}
