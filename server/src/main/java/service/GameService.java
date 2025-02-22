package service;

import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import requestresult.*;
import server.Server;

public class GameService {
    private static GameDao gameDao;
    private static UserDao userDao;
    private static AuthDao authDao;

    public GameService() {
        gameDao = Server.gameDao;
        userDao = Server.userDao;
        authDao = Server.authDao;
    }
    public static Result listGames(AuthenticatedRequest req) {
        return new ErrorResult("Not implemented");
    }

    public static Result joinGame(JoinGameRequest req) {
        return new ErrorResult("Not implemented");
    }

    public static Result createGame(CreateGameRequest req) {
        return new ErrorResult("Not implemented");
    }

    public static Result clear() {
        try {
            authDao.clear();
            gameDao.clear();
            userDao.clear();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ErrorResult(e.getMessage());
        }
        System.out.println("Server memory DAOs cleared");
        return new EmptyResult();
    }
}
