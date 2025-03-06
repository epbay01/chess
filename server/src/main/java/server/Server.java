package server;

import dataaccess.*;
import service.GameService;
import spark.*;

public class Server {
    public static MemoryAuthDao authDao = new MemoryAuthDao();
    public static MemoryUserDao userDao = new MemoryUserDao();
    public static MemoryGameDao gameDao = new MemoryGameDao();
    public boolean useMemory;

    public Server() { this.useMemory = false; }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        try {
            DatabaseManager.createDatabase();
            DatabaseManager.createTables();
        } catch (DataAccessException e) {
            System.out.println("Database or table creation failed with message:\n" + e.getMessage());
            useMemory = true;
        }

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
        GameService.clear();
        Spark.stop();
        Spark.awaitStop();
    }
}
