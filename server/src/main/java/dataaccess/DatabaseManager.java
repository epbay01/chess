package dataaccess;

import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    public static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
            try (var preparedStatement = conn.prepareStatement("USE " + DATABASE_NAME)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Defines schema and creates the tables
     * @throws DataAccessException
     */
    public static void createTables() throws DataAccessException {
        try {
            String[] statements = {"""
                CREATE TABLE IF NOT EXISTS user (
                    `username` VARCHAR(255) NOT NULL,
                    `password` VARCHAR(255) NOT NULL,
                    `email` VARCHAR(255),
                    PRIMARY KEY(username)
                );
                """, """
                CREATE TABLE IF NOT EXISTS auth (
                    `token` VARCHAR(255) NOT NULL,
                    `username` VARCHAR(255) NOT NULL,
                    PRIMARY KEY(token),
                    FOREIGN KEY(username) REFERENCES user(username)
                        ON DELETE CASCADE
                        ON UPDATE NO ACTION
                );
                """, """
                CREATE TABLE IF NOT EXISTS game (
                    `gameID` INT auto_increment NOT NULL,
                    `whiteUsername` VARCHAR(255) NULL,
                    `blackUsername` VARCHAR(255) NULL,
                    `gameName` VARCHAR(255),
                    `chessGame` VARCHAR(2200),
                    PRIMARY KEY(gameID),
                    FOREIGN KEY(whiteUsername) REFERENCES user(username)
                        ON DELETE SET NULL
                        ON UPDATE NO ACTION,
                    FOREIGN KEY(blackUsername) REFERENCES user(username)
                        ON DELETE SET NULL
                        ON UPDATE NO ACTION
                );
                """};

            for (var s : statements) {
                try (var conn = DatabaseManager.getConnection()) {
                    var preparedStatement = conn.prepareStatement(s);
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    public static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
