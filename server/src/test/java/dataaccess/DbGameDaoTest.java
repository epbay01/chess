package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import server.Server;
import model.GameData;
import model.UserData;

import java.sql.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class DbGameDaoTest {
    private Server server;
    private DbGameDao dao;
    private GameData gameData;

    @BeforeEach
    void setup() {
        server = new Server();
        server.run(9000);
        gameData = new GameData(
                1, "user", "user2", "game", new ChessGame()
        );

        try {
            DbUserDao userDao = new DbUserDao();
            userDao.clear();
            userDao.createUser(new UserData("user", "pass", "email"));
            userDao.createUser(new UserData("user2", "pass2", "email2"));

            dao = new DbGameDao();
            dao.hardClear();
        } catch (Exception e) {
            System.err.println("In setup: " + e.getMessage());
        }
    }

    @Test
    void clearTest() {
        // clear run in setup
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("INSERT INTO game (whiteUsername, blackUsername, gameName, chessGame)"
                    + " VALUES ('user', 'user2', 'game', '')").executeUpdate();
            dao.clear();
            ResultSet resultSet = conn.prepareStatement("SELECT * FROM game").executeQuery();
            Assertions.assertFalse(resultSet.next());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void listGamesTest() {
        GameData[] results = new GameData[0];
        Gson gson = new Gson();
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("INSERT INTO game (whiteUsername, blackUsername, gameName, chessGame) " +
                    "VALUES ('user', 'user2', 'game', '" + (gson.toJson(new ChessGame())) + "')").executeUpdate();
            results = dao.listGames();
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        GameData[] expected = {gameData};

        Assertions.assertArrayEquals(expected, results);
    }

    @Test
    void createGameTest() {
        try (Connection conn = DatabaseManager.getConnection()) {
            dao.createGame(gameData);
            ResultSet resultSet = conn.prepareStatement("SELECT * FROM game").executeQuery();
            Assertions.assertTrue(resultSet.next());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void getGameTest() {
        GameData result = null;
        try {
            dao.createGame(gameData);
            result = dao.getGame(1);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        Assertions.assertEquals(gameData, result);
    }

    @Test
    void updateGameTest() {
        GameData result = null;
        try {
            dao.createGame(new GameData(
                    1,"user","user","", new ChessGame())
            );
            dao.updateGame(gameData);
            result = dao.getGame(1);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        Assertions.assertEquals(gameData, result);
    }

    @Test
    void deleteGameTest() {
        GameData[] result = null;
        try {
            dao.createGame(gameData);
            dao.deleteGame(1);
            result = dao.listGames();
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        Assertions.assertArrayEquals(new GameData[]{}, result);
    }

    @AfterEach
    void shutdown() {
        server.stop();
    }
}
