package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class DbGameDao implements GameDao {
    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        String newWhiteUsername = (gameData.whiteUsername() != null) ?
                gameData.whiteUsername().replaceAll("'", "''") : null;
        String newBlackUsername = (gameData.blackUsername() != null) ?
                gameData.blackUsername().replaceAll("'", "''") : null;
        if (gameData.gameName() == null) { throw new DataAccessException("Cannot parse null string"); }
        String newGameName = gameData.gameName().replaceAll("'", "''");
        Gson gson = new Gson();
        try (Connection conn = DatabaseManager.getConnection()) {
            String str = "INSERT INTO game (whiteUsername, blackUsername, gameName, chessGame) VALUES (" +
                    ((newWhiteUsername != null) ? "'" + newWhiteUsername + "'" : "NULL") + ", " +
                    ((newBlackUsername != null) ? "'" + newBlackUsername + "'" : "NULL") + ", '" +
                    newGameName + "', '" +
                    gson.toJson(gameData.chessGame())
                    + "');";
            conn.prepareStatement(str).executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public int getGameID(GameData gameData) throws DataAccessException {
        String newWhiteUsername = (gameData.whiteUsername() != null) ?
                gameData.whiteUsername().replaceAll("'", "''") : null;
        String newBlackUsername = (gameData.blackUsername() != null) ?
                gameData.blackUsername().replaceAll("'", "''") : null;
        String newGameName = gameData.gameName().replaceAll("'", "''");
        try (Connection conn = DatabaseManager.getConnection()) {
            String str = "SELECT * FROM game WHERE whiteUsername" +
                    ((newWhiteUsername != null) ? "='" + newWhiteUsername + "'" : " IS NULL") +
                    " AND blackUsername" +
                    ((newBlackUsername != null) ? "='" + newBlackUsername + "'" : " IS NULL") +
                    " AND gameName='" + newGameName + "'";
            ResultSet resultSet = conn.prepareStatement(str).executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("gameID");
            } else {
                throw new DataAccessException("Game not found");
            }
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
            try (var statement = conn.prepareStatement("SELECT * FROM game WHERE gameID=" + gameID)) {
                ResultSet resultSet = statement.executeQuery();
                game = getNextGame(resultSet);
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return game;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        String newWhiteUsername = (gameData.whiteUsername() != null) ?
                gameData.whiteUsername().replaceAll("'", "''") : null;
        String newBlackUsername = (gameData.blackUsername() != null) ?
                gameData.blackUsername().replaceAll("'", "''") : null;
        String newGameName = gameData.gameName().replaceAll("'", "''");
        Gson gson = new GsonBuilder().serializeNulls().create();
        try (Connection conn = DatabaseManager.getConnection()) {
            String str = "UPDATE game SET whiteUsername=" +
                    ((newWhiteUsername != null) ? "'" + newWhiteUsername + "'" : "NULL") +
                    ", blackUsername=" +
                    ((newBlackUsername != null) ? "'" + newBlackUsername + "'" : "NULL") +
                    ", gameName='" + newGameName
                    + "', chessGame='" + gson.toJson(gameData.chessGame())
                    + "' WHERE gameID=" + gameData.gameID();
            try (var statement = conn.prepareStatement(str)) {
                statement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        System.out.println("game updated in db");
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
