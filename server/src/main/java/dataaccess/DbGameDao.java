package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DbGameDao implements GameDao {
    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        Gson gson = new Gson();
        try (Connection conn = DatabaseManager.getConnection()) {
            String str = "INSERT INTO game (whiteUsername, blackUsername, gameName, chessGame) VALUES ('" +
                    gameData.whiteUsername() + "', '" +
                    gameData.blackUsername() + "', '" +
                    gameData.gameName() + "', '" +
                    gson.toJson(gameData.chessGame())
                    + "');";
            conn.prepareStatement(str).executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        ArrayList<GameData> games = new ArrayList<>();
        int count = 0;
        try (Connection conn = DatabaseManager.getConnection()) {
            ResultSet resultSet = conn.prepareStatement("SELECT * FROM game").executeQuery();
            GameData data;
            do {
                data = getNextGame(resultSet);
                if (data != null) {
                    games.add(data);
                    count++;
                }
            } while (data != null);
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return games.toArray(new GameData[count]);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game;
        try (Connection conn = DatabaseManager.getConnection()) {
            ResultSet resultSet = conn.prepareStatement("SELECT * FROM game WHERE gameID=" + gameID)
                    .executeQuery();
            game = getNextGame(resultSet);
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return game;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        Gson gson = new Gson();
        try (Connection conn = DatabaseManager.getConnection()) {
            String str = "UPDATE game SET whiteUsername='"
                    + gameData.whiteUsername() + "', blackUsername='"
                    + gameData.blackUsername() + "', gameName='"
                    + gameData.gameName() + "', chessGame='"
                    + gson.toJson(gameData.chessGame())
                    + "' WHERE gameID=" + gameData.gameID();
            conn.prepareStatement(str).executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String str = "DELETE FROM game WHERE gameID=" + gameID;
            conn.prepareStatement(str).executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("DELETE FROM game").executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void hardClear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("drop table game").executeUpdate();
            DatabaseManager.createTables();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private GameData getNextGame(ResultSet resultSet) throws DataAccessException {
        Gson gson = new Gson();
        GameData game = null;
        try {
            if (resultSet.next()) {
                game = new GameData(
                        resultSet.getInt("gameID"),
                        resultSet.getString("whiteUsername"),
                        resultSet.getString("blackUsername"),
                        resultSet.getString("gameName"),
                        gson.fromJson(resultSet.getString("chessGame"), ChessGame.class)
                );
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return game;
    }
}
