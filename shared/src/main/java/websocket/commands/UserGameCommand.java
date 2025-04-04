package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 *
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    private final CommandType commandType;
    private final String authToken;
    private final Integer gameID;
    private final ChessMove move;
    private final String username;
    private final ChessGame.TeamColor teamColor;

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.username = null;
        this.teamColor = null;
        this.move = null;
    }

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.move = move;
        this.username = null;
        this.teamColor = null;
    }

    public UserGameCommand(CommandType commandType,
                           String authToken,
                           Integer gameID,
                           String username,
                           ChessGame.TeamColor teamColor) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.username = username;
        this.teamColor = teamColor;
        this.move = null;
    }

    public UserGameCommand(CommandType commandType,
                           String authToken,
                           Integer gameID,
                           String username,
                           ChessGame.TeamColor teamColor,
                           ChessMove move) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.username = username;
        this.teamColor = teamColor;
        this.move = move;
    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getGameID() {
        return gameID;
    }

    public ChessMove getMove() { return move; }

    public String getUsername() { return username; }

    public ChessGame.TeamColor getTeamColor() { return teamColor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand)) {
            return false;
        }
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID());
    }

    @Override
    public String toString() {
        return "UserGameCommand{" +
                "commandType=" + commandType +
                ", authToken='" + authToken + '\'' +
                ", gameID=" + gameID +
                ", move=" + move +
                ", username='" + username + '\'' +
                ", teamColor=" + teamColor +
                '}';
    }
}
