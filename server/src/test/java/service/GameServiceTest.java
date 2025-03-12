package service;

import chess.ChessGame;
import dataaccess.*;
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
        try {
            Server.authDao.clear();
            Server.userDao.clear();
            Server.gameDao.clear();
        } catch (Exception e) { Assertions.fail(); }
    }

    @Test
    public void listGamesTest() {
        try {
            Server.userDao.createUser(new UserData("user", "pass", "email"));
            Server.authDao.addAuth(new AuthData("1234", "user"));
        } catch (Exception e) {
            Assertions.fail("createUser or addAuth failed");
        }
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
        try {
            Server.userDao.createUser(new UserData("user", "pass", "email"));
            Server.authDao.addAuth(new AuthData("1234", "user"));
            Server.gameDao.createGame(new GameData(1, null, null, "game", new ChessGame()));
        } catch (Exception e) {
            Assertions.fail("createUser, addAuth or createGame failed with error: " + e.getMessage());
        }
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
        try {
            Server.userDao.createUser(new UserData("user", "pass", "email"));
            Server.authDao.addAuth(new AuthData("1234", "user"));
        } catch (Exception e) {
            Assertions.fail("createUser or addAuth failed with error: " + e.getMessage());
        }
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
        try {
            Server.userDao.createUser(new UserData("user", "pass", "email"));
            Server.authDao.addAuth(new AuthData("1234", "user"));
            Server.gameDao.createGame(new GameData(1, "user", null, "game", new ChessGame()));
        } catch (Exception e) {
            Assertions.fail("createUser, addAuth or createGame failed");
        }

        GameService.clear();

        if (Server.useMemory) {
            Assertions.assertEquals(new MemoryAuthDao(), Server.authDao);
            Assertions.assertEquals(new MemoryGameDao(), Server.gameDao);
            Assertions.assertEquals(new MemoryUserDao(), Server.userDao);
        } else {
            try {
                Assertions.assertArrayEquals(new GameData[]{}, Server.gameDao.listGames());
                Assertions.assertEquals("", ((DbUserDao) Server.userDao).queryAll());
                Assertions.assertEquals("", ((DbAuthDao) Server.authDao).queryAll());
            } catch (Exception e) {
                Assertions.fail();
            }
        }
    }
}
