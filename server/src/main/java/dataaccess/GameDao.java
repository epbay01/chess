package dataaccess;

import model.*;

public interface GameDao {
    void createGame(GameData gameData);
    GameData[] listGames() throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(GameData gameData) throws DataAccessException;
    void deleteGame(int gameID) throws DataAccessException;
    void clear() throws DataAccessException;
}
