package server;

import dataaccess.*;
import spark.*;

public class Server {
    public static AuthDao authDao;
    public static UserDao userDao;
    public static GameDao gameDao;
    public static boolean useMemory;
    private static final WebsocketHandler WEBSOCKET_HANDLER = new WebsocketHandler();

    static {
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.createTables();
            useMemory = false;
            System.out.println("Database server started successfully");
        } catch (DataAccessException e) {
            System.out.println("Database or table creation failed with message:\n" + e.getMessage());
            System.out.println("Using in-memory database");
            useMemory = true;
        }

        if (!useMemory) {
            authDao = new DbAuthDao();
            userDao = new DbUserDao();
            gameDao = new DbGameDao();
        } else {
            authDao = new MemoryAuthDao();
            userDao = new MemoryUserDao();
            gameDao = new MemoryGameDao();
        }
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", WEBSOCKET_HANDLER);

        Spark.post("/user", Handler::handleRegister);
        Spark.post("/session", Handler::handleLogin);
        Spark.delete("/session", Handler::handleLogout);

        Spark.get("/game", Handler::handleListGames);
        Spark.post("/game", Handler::handleCreateGame);
        Spark.put("/game", Handler::handleJoinGame);

        Spark.delete("/db", Handler::handleClear);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
