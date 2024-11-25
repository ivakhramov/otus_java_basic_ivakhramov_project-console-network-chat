package ru.otus.java.basic.ivakhramov.chat.server.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The Database class is responsible for managing the connection to a database.
 * It implements the AutoCloseable.
 */
public class Database implements AutoCloseable {

    private String url;
    private String user;
    private String password;
    private Connection connection;
    private static final Logger LOGGER = LogManager.getLogger(Database.class);

    /**
     * Constructs a Database instance with the specified connection parameters.
     *
     * @param url      the database connection URL
     * @param user     the username for the database connection
     * @param password the password for the database connection
     */
    public Database(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * Establishes a connection to the database using the provided parameters.
     *
     * @return the established Connection object, or null if the connection fails
     */
    public Connection getConnection() {
        try {
            this.connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection to DB " + this.connection.getCatalog() + " established");
            LOGGER.info("Connection to DB " + this.connection.getCatalog() + " established");
            return connection;
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            LOGGER.error("Error", e);
            return null;
        }
    }

    /**
     * Closes the database connection if it is not null.
     */
    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection to the database closed");
                LOGGER.info("Connection to the database closed");
            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
                LOGGER.error("Error", e);
            }
        }
    }
}
