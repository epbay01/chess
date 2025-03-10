package dataaccess;

import model.GameData;

public class DbGameDao implements GameDao {
    @Override
    public void createGame(GameData gameData) {

    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        return new GameData[0];
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
