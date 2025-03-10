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
        Gson gson = new Gson();
        int count = 0;
        try (Connection conn = DatabaseManager.getConnection()) {
            ResultSet resultSet = conn.prepareStatement("SELECT * FROM game").executeQuery();
            while (resultSet.next()) {
                ChessGame chessGame = gson.fromJson(resultSet.getString("chessGame"), ChessGame.class);
                games.add(new GameData(
                        resultSet.getInt("gameID"),
                        resultSet.getString("whiteUsername"),
                        resultSet.getString("blackUsername"),
                        resultSet.getString("gameName"),
                        chessGame
                ));
                count++;
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return games.toArray(new GameData[count]);
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
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("DELETE FROM game").executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
