package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import requestresult.*;
import server.Server;
import service.UserService;

import java.util.Arrays;
import java.util.Objects;

public class GameService {
    private static GameDao gameDao = Server.gameDao;
    private static UserDao userDao = Server.userDao;
    private static AuthDao authDao = Server.authDao;

    public static Result listGames(AuthenticatedRequest req) {
        if (!UserService.authenticate(req.authToken())) {
            return new ErrorResult("Error: Not authenticated");
        }

        try { // TODO: fix this
            return new ListGamesResult(Arrays.asList(gameDao.listGames()));
        } catch (Exception ignored) { return null; }
    }

    public static Result joinGame(JoinGameRequest req) {
        if (!UserService.authenticate(req.authToken())) {
            return new ErrorResult("Error: Not authenticated");
        }

        try {
            String username = authDao.getAuthByToken(req.authToken()).username();
            GameData game = gameDao.getGame(Integer.parseInt(req.gameID()));
            GameData newData;
            if (Objects.equals(req.playerColor(), "WHITE")) {
                if (!(game.whiteUsername() == null || game.whiteUsername().isEmpty())) {
                    return new ErrorResult("Error: Color taken");
                }
                newData = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.chessGame());
            } else if (Objects.equals(req.playerColor(), "BLACK")) {
                if (!(game.blackUsername() == null || game.blackUsername().isEmpty())) {
                    return new ErrorResult("Error: Color taken");
                }
                newData = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.chessGame());
            } else {
                return new ErrorResult("Error: Bad color");
            }
            gameDao.updateGame(newData);
            return new EmptyResult();
        } catch (Exception e) {
            return new ErrorResult("Error: " + e.getMessage());
        }
    }

    public static Result createGame(CreateGameRequest req) {
        if (!UserService.authenticate(req.authToken())) {
            return new ErrorResult("Error: Not authenticated");
        }
        MemoryGameDao.increment();
        int gameID = MemoryGameDao.id;

        GameData data = new GameData(gameID, null, null, req.gameName(), new ChessGame());
        gameDao.createGame(data);

        return new CreateGameResult(gameID);
    }

    public static Result clear() {
        try {
            authDao.clear();
            gameDao.clear();
            userDao.clear();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ErrorResult("Error: " + e.getMessage());
        }
        System.out.println("Server memory DAOs cleared");
        return new EmptyResult();
    }
}
