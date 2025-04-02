package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import server.WebsocketHandler;
import server.WebsocketSessions;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.rmi.ServerException;

public class WebsocketService {
    private final static WebsocketSessions sessions = WebsocketHandler.sessions;

    public static ServerMessage[] connect(UserGameCommand command, Session session) {
        ServerMessage msg1; // board
        ServerMessage msg2; // notification
        String notifMessage = command.getUsername() + " has joined game " + command.getGameID();

        try {
            ChessGame game = Server.gameDao.getGame(command.getGameID()).chessGame();
            msg1 = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            sessions.addSession(command.getGameID(), session);

            msg2 = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notifMessage);
        } catch (Exception e) {
            msg1 = new ServerMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            return new ServerMessage[]{msg1};
        }

        return new ServerMessage[]{msg1, msg2};
    }

    public static ServerMessage[] makeMove(UserGameCommand command, Session session) {
        ServerMessage msg1; // board
        ServerMessage msg2; // notification
        String notifMessage = command.getUsername() + " has made a move: " + command.getMove().prettyPrint();

        try {
            GameData gameData = Server.gameDao.getGame(command.getGameID());
            ChessGame game = gameData.chessGame();

            if (command.getMove() != null) {
                game.makeMove(command.getMove());
                Server.gameDao.updateGame(
                        new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(),
                                gameData.gameName(), game)
                );
            } else {
                throw new ServerException("Invalid move sent");
            }

            msg1 = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            msg2 = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notifMessage);

            // check notification
            if (game.isInCheck(game.getTeamTurn())) {
                ServerMessage msg3 = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        game.getTeamTurn() + " is now in check");
                return new ServerMessage[]{msg1, msg2, msg3};
            }
        } catch (Exception e) {
            msg1 = new ServerMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            return new ServerMessage[]{msg1};
        }

        return new ServerMessage[]{msg1, msg2};
    }

    public static ServerMessage[] leave(UserGameCommand command, Session session) {
        return new ServerMessage[]{
                new ServerMessage(ServerMessage.ServerMessageType.ERROR, "not implemented")};
    }

    public static ServerMessage[] resign(UserGameCommand command, Session session) {
        return new ServerMessage[]{
                new ServerMessage(ServerMessage.ServerMessageType.ERROR, "not implemented")};
    }
}
