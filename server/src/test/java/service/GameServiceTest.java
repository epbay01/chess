package service;

import chess.ChessGame;
import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDao;
import dataaccess.MemoryUserDao;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestresult.*;
import server.Server;
import service.GameService;
import service.UserService;

import java.util.ArrayList;
import java.util.List;

public class GameServiceTest {
    @BeforeEach
    public void init() {
        Server.authDao.clear();
        Server.userDao.clear();
        Server.gameDao.clear();
    }

    @Test
    public void listGamesTest() {
        Server.authDao.addAuth(new AuthData("1234", "user"));
        AuthenticatedRequest request = new AuthenticatedRequest("1234");
        Result result = GameService.listGames(request);

        ListGamesResult expected = new ListGamesResult(new ArrayList<>());

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void listGamesFailTest() {
        AuthenticatedRequest request = new AuthenticatedRequest("1234");
        Result result = GameService.listGames(request);

        ErrorResult expected = new ErrorResult("Error: Not authenticated");

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void joinGameTest() {
        Server.authDao.addAuth(new AuthData("1234", "user"));
        Server.gameDao.createGame(new GameData(1, null, null, "game", new ChessGame()));
        JoinGameRequest request = new JoinGameRequest("1234", "1", "WHITE");
        Result result = GameService.joinGame(request);

        EmptyResult expected = new EmptyResult();

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void joinGameFailTest() {
        JoinGameRequest request = new JoinGameRequest("1234", "1", "WHITE");
        Result result = GameService.joinGame(request);

        ErrorResult expected = new ErrorResult("Error: Not authenticated");

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void createGameTest() {
        Server.authDao.addAuth(new AuthData("1234", "user"));
        CreateGameRequest request = new CreateGameRequest("1234", "game");
        Result result = GameService.createGame(request);

        var expected = new CreateGameResult(1);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void createGameFailTest() {
        CreateGameRequest request = new CreateGameRequest("1234", "game");
        Result result = GameService.createGame(request);

        var expected = new ErrorResult("Error: Not authenticated");

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void clearTest() {
        Server.authDao.addAuth(new AuthData("1234", "user"));
        Server.userDao.createUser(new UserData("user", "pass", "email"));
        Server.gameDao.createGame(new GameData(1,"user", null, "game", new ChessGame()));

        GameService.clear();

        Assertions.assertEquals(new MemoryAuthDao(), Server.authDao);
        Assertions.assertEquals(new MemoryGameDao(), Server.gameDao);
        Assertions.assertEquals(new MemoryUserDao(), Server.userDao);
    }
}
