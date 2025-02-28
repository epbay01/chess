package dataaccess;

import model.*;

public interface GameDao {
    void createGame(GameData gameData);
    GameData[] listGames();
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(GameData gameData) throws DataAccessException;
    void deleteGame(int gameID) throws DataAccessException;
    void clear();
}
