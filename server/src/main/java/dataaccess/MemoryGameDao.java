package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryGameDao implements GameDao {
    ArrayList<GameData> db;

    public MemoryGameDao() {
        db = new ArrayList<>();
    }

    public MemoryGameDao(ArrayList<GameData> db) {
        this.db = db;
    }

    public void createGame(GameData gameData) {
        db.add(gameData);
    }

    public GameData[] listGames() {
        GameData[] result = new GameData[db.size()];
        return db.toArray(result);
    }

    public GameData getGame(int gameId) throws DataAccessException {
        for (GameData gameData : db) {
            if (gameData.gameID() == gameId) {
                return gameData;
            }
        }
        throw new DataAccessException("Game with id " + gameId + "does not exist.");
    }

    public void updateGame(GameData gameData) throws DataAccessException {
        GameData oldGame;
        try {
            oldGame = getGame(gameData.gameID());
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }

        int ind = db.indexOf(oldGame);
        db.remove(ind);
        db.add(ind, gameData);
    }

    public void deleteGame(int gameId) throws DataAccessException {
        GameData gameData;
        try {
            gameData = getGame(gameId);
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        db.remove(gameData);
    }

    public void clear() {
        db.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemoryGameDao that = (MemoryGameDao) o;
        return Objects.equals(db, that.db);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(db);
    }
}
