package server;

import dataaccess.*;
import service.GameService;
import spark.*;

public class Server {
    public AuthDao authDao;
    public UserDao userDao;
    public GameDao gameDao;
    public boolean useMemory = false;

    public Server() { this.useMemory = false; }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        try {
            DatabaseManager.createDatabase();
            DatabaseManager.createTables();
            useMemory = false;
        } catch (DataAccessException e) {
            System.out.println("Database or table creation failed with message:\n" + e.getMessage());
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
