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
    private DbUserDao userDao;
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
            userDao = new DbUserDao();
            userDao.clear();
            userDao.createUser(new UserData("user", "pass", "email"));
            userDao.createUser(new UserData("user2", "pass2", "email2"));

            dao = new DbGameDao();
            dao.clear();
        } catch (Exception e) {
            System.err.println("In setup: " + e.getMessage());
        }
    }

    @Test
    void clearTest() {
        // clear run in setup
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("INSERT INTO games ('whiteUsername', 'blackUsername', 'gameName', 'chessGame') VALUES ('user', 'user2', 'game', "
                    + (new ChessGame().toString()) + ")").executeUpdate();
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
            System.out.print(gson.toJson(new ChessGame()));
            conn.prepareStatement("INSERT INTO game (whiteUsername, blackUsername, gameName, chessGame) " +
                    "VALUES ('user', 'user2', 'game', '" + (gson.toJson(new ChessGame())) + "')").executeUpdate();
            results = dao.listGames();
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        GameData[] expected = {gameData};

        // currently the id auto-increments so this may fail
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

    @AfterEach
    void shutdown() {
        server.stop();
    }
}
