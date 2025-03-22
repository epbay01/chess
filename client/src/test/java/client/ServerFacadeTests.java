package client;

import chess.ChessGame;
import exceptions.BadStatusCodeException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.List;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static final int PORT = 8080;
    private static final UserData USER_DATA = new UserData("user", "pass", "email");

    @BeforeAll
    public static void init() {
        server = new Server();
        facade = new ServerFacade(PORT);
        var port = server.run(PORT);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Order(1)
    @Test
    public void clearTest() {
        try {
            facade.clear();
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Order(2)
    @Test
    public void registerTest() {
        try {
            facade.clear();
            var result = facade.register(USER_DATA);
            Assertions.assertNotNull(result);
            Assertions.assertEquals(USER_DATA.username(), result.username());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Order(2)
    @Test
    public void registerUserExists() {
        facade.clear();
        facade.register(USER_DATA);
        Assertions.assertThrows(BadStatusCodeException.class, () -> facade.register(USER_DATA));
    }

    @Order(3)
    @Test
    public void loginTest() {
        try {
            facade.clear();
            facade.register(USER_DATA);
            var result = facade.login(USER_DATA);
            Assertions.assertNotNull(result);
            Assertions.assertEquals(AuthData.class, result.getClass());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Order(3)
    @Test
    public void loginWrongPasswordTest() {
        try {
            facade.clear();
            var auth = facade.register(USER_DATA);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
        Assertions.assertThrows(BadStatusCodeException.class, () -> facade.login(
                new UserData("user", "badPass", "email")
        ));
    }

    @Order(3)
    @Test
    public void logoutTest() {
        try {
            facade.clear();
            var authData = facade.register(USER_DATA);
            facade.logout(authData);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Order(3)
    @Test
    public void logoutNotLoggedInTest() {
        facade.clear();
        Assertions.assertThrows(BadStatusCodeException.class, () -> facade.logout(
                new AuthData("faketoken", "user")
        ));
    }

    @Order(3)
    @Test
    public void createGameTest() {
        try {
            facade.clear();
            var auth = facade.register(USER_DATA);
            var id = facade.createGame(auth, "Game");
            Assertions.assertTrue(true, "Game id is: " + id);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Order(3)
    @Test
    public void createGameUnauthorizedTest() {
        Assertions.assertThrows(BadStatusCodeException.class, () -> facade.createGame(
                new AuthData("faketoken", "user"),
                "Game"
        ));
    }

    @Order(3)
    @Test
    public void listGamesTest() {
        List<GameData> result = null;
        int id1 = 1;
        int id2 = 2;

        try {
            facade.clear();
            var auth = facade.register(USER_DATA);
            id1 = facade.createGame(auth, "Game");
            id2 = facade.createGame(auth, "Game 2");
            result = facade.listGames(auth);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        GameData[] expected = {
                new GameData(id1, null, null, "Game", new ChessGame()),
                new GameData(id2, null, null, "Game 2", new ChessGame()),
        };

        Assertions.assertArrayEquals(expected, result.toArray());
    }

    @Order(3)
    @Test
    public void listGamesUnauthorizedTest() {
        Assertions.assertThrows(BadStatusCodeException.class, () -> facade.listGames(
                new AuthData("faketoken", "user")
        ));
    }

    @Order(4)
    @Test
    public void joinGameTest() {
        GameData[] before = null;
        GameData[] after = null;
        int id = 1;

        try {
            facade.clear();
            var auth = facade.register(USER_DATA);
            id = facade.createGame(auth, "Game");
            before = facade.listGames(auth).toArray(new GameData[1]);
            facade.joinGame(auth, id, ChessGame.TeamColor.WHITE);
            after = facade.listGames(auth).toArray(new GameData[1]);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        Assertions.assertNull(before[0].whiteUsername());
        Assertions.assertEquals(after[0].whiteUsername(), USER_DATA.username());
    }

    @Order(4)
    @Test
    public void joinNonexistentGameTest() {
        try {
            facade.clear();
            var auth = facade.register(USER_DATA);
            Assertions.assertThrows(BadStatusCodeException.class, () -> facade.joinGame(auth, 1, ChessGame.TeamColor.WHITE));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

    }
}
