package websocket.messages;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    ChessGame game;
    String errorMessage;
    String message;
    String[] winData = null;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.message = null;
        this.serverMessageType = type;
    }

    public ServerMessage(ServerMessageType type, ChessGame chessGame) {
        this.serverMessageType = type;
        if (type == ServerMessageType.LOAD_GAME) {
            this.game = chessGame;
        }
    }

    public ServerMessage(ServerMessageType type, String message) {
        this.serverMessageType = type;
        if (type == ServerMessageType.NOTIFICATION) {
            this.message = message;
        } else if (type == ServerMessageType.ERROR) {
            this.errorMessage = message;
        } else {
            this.game = (new Gson()).fromJson(message, ChessGame.class);
        }
    }

    public ServerMessage(ServerMessageType type, String message, String[] winData) {
        this.serverMessageType = type;
        if (type == ServerMessageType.NOTIFICATION) {
            this.message = message;
            this.winData = winData;
        }  else if (type == ServerMessageType.ERROR) {
            this.errorMessage = message;
        }
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public String getMessage() {
        if (this.serverMessageType == ServerMessageType.NOTIFICATION) {
            return this.message;
        } else if (this.serverMessageType == ServerMessageType.ERROR) {
            return this.errorMessage;
        } else {
            return (new Gson()).toJson(this.game);
        }
    }

    public ChessGame getGame() {
        return this.game;
    }

    public String[] getWinData() { return this.winData; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
