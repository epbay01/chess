package service;

import requestresult.*;
import server.Server;

public class GameService {
    public Result listGames(AuthenticatedRequest req) {
        return null;
    }

    public Result joinGame(JoinGameRequest req) {
        return null;
    }

    public Result createGame(CreateGameRequest req) {
        return null;
    }

    public Result clear() {
        try {
            Server.authDao.clear();
            Server.gameDao.clear();
            Server.userDao.clear();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ErrorResult(e.getMessage());
        }
        System.out.println("Server memory DAOs cleared");
        return new EmptyResult();
    }
}
