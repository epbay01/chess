package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class GameDaoTest {
    private GameDao dao;
    private GameData gameData;

    @BeforeEach
    void setUp() {
        dao = new MemoryGameDao();
        gameData = new GameData(1234, "user1", "user2", "game1", new ChessGame());
    }

    @Test
    void testCreateGame() {
        dao.createGame(gameData);

        ArrayList<GameData> list = new ArrayList<>();
        list.add(gameData);
        var expected = new MemoryGameDao(list);

        Assertions.assertEquals(dao, expected);
    }

    @Test
    void testGetGame() {
        dao.createGame(gameData);

        ArrayList<GameData> list = new ArrayList<>();
        list.add(gameData);
        var expected = new MemoryGameDao(list);

        try {
            Assertions.assertEquals(dao.getGame(1234), expected.getGame(1234));
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void testDeleteGame() {
        dao.createGame(gameData);
        try {
            dao.deleteGame(1234);
        } catch (Exception e) {
            Assertions.fail();
        }

        var expected = new MemoryGameDao();

        Assertions.assertEquals(dao, expected);
    }

    @Test
    void testClearGame() {
        dao.createGame(gameData);
        dao.createGame(new GameData(5678, "user3", "user4", "game2", new ChessGame()));
        dao.clear();

        var expected = new MemoryGameDao();

        Assertions.assertEquals(dao, expected);
    }

    @Test
    void testUpdateGame() {
        dao.createGame(gameData);
        GameData updatedData = new GameData(1234, "user3", "user4", "game2", new ChessGame());
        try {
            dao.updateGame(updatedData);
        } catch (Exception e) {
            Assertions.fail();
        }

        var list = new ArrayList<GameData>();
        list.add(updatedData);
        var expected = new MemoryGameDao(list);

        Assertions.assertEquals(dao, expected);
    }

    @Test
    void testGetGameFail() {
        Assertions.assertThrows(DataAccessException.class, () -> dao.getGame(1234));
    }

    @Test
    void testUpdateGameFail() {
        Assertions.assertThrows(DataAccessException.class, () -> dao.updateGame(gameData));
    }

    @Test
    void testDeleteGameFail() {
        Assertions.assertThrows(DataAccessException.class, () -> dao.deleteGame(1234));
    }
}
