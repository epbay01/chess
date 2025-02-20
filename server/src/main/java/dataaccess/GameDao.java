package dataaccess;

import model.*;

public interface GameDao {
    void createGame(GameData gameData);
    GameData[] listGames();
    GameData getGame(int gameId) throws DataAccessException;
    void updateGame(GameData gameData) throws DataAccessException;
    void deleteGame(int gameId) throws DataAccessException;
    void clear();
}
