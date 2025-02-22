package server;

import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDao;
import dataaccess.MemoryUserDao;
import spark.*;

public class Server {
    public static MemoryAuthDao authDao;
    public static MemoryUserDao userDao;
    public static MemoryGameDao gameDao;

    public Server() {
        authDao = new MemoryAuthDao();
        userDao = new MemoryUserDao();
        gameDao = new MemoryGameDao();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/user", Handler::handleRegister);

        Spark.delete("/db", Handler::handleClear);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
