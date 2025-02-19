package dataaccess;

import model.*;

public interface GameDao {
    void createGame(GameData gameData);
    GameData[] listGames();
    GameData getGame(int id);
    void updateGame(GameData gameData);
    void clear();
}
