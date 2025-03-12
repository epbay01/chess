package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import requestresult.*;
import server.Server;

import java.util.Arrays;
import java.util.Objects;

public class GameService {
    private static final GameDao GAME_DAO = Server.gameDao;
    private static final UserDao USER_DAO = Server.userDao;
    private static final AuthDao AUTH_DAO = Server.authDao;

    public static Result listGames(AuthenticatedRequest req) {
        if (!UserService.authenticate(req.authToken())) {
            return new ErrorResult("Error: Not authenticated");
        }

        try {
            return new ListGamesResult(Arrays.asList(GAME_DAO.listGames()));
        } catch (DataAccessException e) {
            return new ErrorResult("Error: " + e.getMessage());
        }
    }

    public static Result joinGame(JoinGameRequest req) {
        if (!UserService.authenticate(req.authToken())) {
            return new ErrorResult("Error: Not authenticated");
        }

        try {
            String username = AUTH_DAO.getAuthByToken(req.authToken()).username();
            GameData game = GAME_DAO.getGame(Integer.parseInt(req.gameID()));
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
            GAME_DAO.updateGame(newData);
            return new EmptyResult();
        } catch (Exception e) {
            return new ErrorResult("Error: " + e.getMessage());
        }
    }

    public static Result createGame(CreateGameRequest req) {
        if (!UserService.authenticate(req.authToken())) {
            return new ErrorResult("Error: Not authenticated");
        }

        int gameID = 0; // will be thrown out in sql implementation

        if (Server.useMemory) {
            MemoryGameDao.increment();
            gameID = MemoryGameDao.id;
        }

        GameData data = new GameData(gameID, null, null, req.gameName(), new ChessGame());

        try {
            GAME_DAO.createGame(data);
        } catch (DataAccessException e) {
            return new ErrorResult("Error: " + e.getMessage());
        }

        if (!Server.useMemory) {
            try {
                gameID = ((DbGameDao) GAME_DAO).getGameID(data);
            } catch (DataAccessException e) {
                return new ErrorResult("Error: " + e.getMessage());
            }
        }

        return new CreateGameResult(gameID);
    }

    public static Result clear() {
        try {
            AUTH_DAO.clear();
            GAME_DAO.clear();
            USER_DAO.clear();
        } catch (Exception e) {
            return new ErrorResult("Error: " + e.getMessage());
        }
        return new EmptyResult();
    }
}
