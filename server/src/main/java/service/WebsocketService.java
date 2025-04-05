package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import server.WebsocketHandler;
import server.WebsocketSessions;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.rmi.ServerException;

public class WebsocketService {
    private final static WebsocketSessions sessions = WebsocketHandler.sessions;

    public static ServerMessage[] connect(UserGameCommand command, Session session) {
        ServerMessage msg1; // board
        ServerMessage msg2; // notification
        String notifMessage = command.getUsername() + " has joined game " + command.getGameID();

        try {
            GameData gameData = Server.gameDao.getGame(command.getGameID());
            if (gameData.chessGame() == null) {
                throw new ServerException("Game does not exist");
            }
            ChessGame game = gameData.chessGame();
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
        ServerMessage msg3 = null; // check
        String notifMessage;

        try {
            ChessGame game = Server.gameDao.getGame(command.getGameID()).chessGame();

            if (!validateUser(command, session)) { throw new ServerException("User not in game"); }

            if (command.getMove() != null) {
                notifMessage = command.getUsername() + " has made a move: " + command.getMove().prettyPrint();

                game.makeMove(command.getMove());

                var result = checkLogic(game);
                msg3 = (ServerMessage)result[0];
                game = (ChessGame)result[1];

                updateGame(game, command);
            } else {
                throw new ServerException("Invalid move sent");
            }

            msg1 = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            msg2 = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notifMessage);
        } catch (Exception e) {
            msg1 = new ServerMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            return new ServerMessage[]{msg1};
        }

        if (msg3 == null) {
            return new ServerMessage[]{msg1, msg2};
        } else {
            return new ServerMessage[]{msg1, msg2, msg3};
        }
    }

    public static ServerMessage[] leave(UserGameCommand command, Session session) {
        ServerMessage msg1;

        try {
            GameData gameData = Server.gameDao.getGame(command.getGameID());
            GameData newGameData;

            // if user isn't in the game itself, they are an observer and don't need to be removed
            if (validateUser(command, null)) {
                ChessGame.TeamColor color = nullCheckColor(command);

                newGameData = (color == ChessGame.TeamColor.WHITE) ? new GameData(
                        gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(),
                        gameData.chessGame()
                ) : new GameData(
                        gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(),
                        gameData.chessGame()
                );

                Server.gameDao.updateGame(newGameData);
            }

            sessions.removeSession(command.getGameID(), session);

            msg1 = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    command.getUsername() + " has left the game.");
        } catch (Exception e) {
            msg1 = new ServerMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
        }

        return new ServerMessage[]{msg1};
    }

    public static ServerMessage[] resign(UserGameCommand command, Session session) {
        ServerMessage msg1;

        try {
            if (!validateUser(command, session)) {
                throw new ServerException("User not in game");
            }

            ChessGame game = Server.gameDao.getGame(command.getGameID()).chessGame();
            ChessGame.TeamColor color = nullCheckColor(command);

            game.resign(color);
            updateGame(game, command);

            var winner = (color == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK
                    : ChessGame.TeamColor.WHITE;
            msg1 = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    color + " resigned, " + winner  + " WON!");

        } catch (Exception e) {
            msg1 = new ServerMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            return new ServerMessage[]{msg1};
        }

        return new ServerMessage[]{msg1};
    }

    private static Object[] checkLogic(ChessGame game) {
        ServerMessage msg = null;

        if (game.isInCheck(game.getTeamTurn())) {
            String teamString = (game.getTeamTurn() == ChessGame.TeamColor.WHITE) ? "Black" : "White";
            msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    teamString + " is now in check");
        }

        if (game.isInStalemate(game.getTeamTurn())) {
            msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    "Game is in stalemate");
            game.resign(null);
        }

        if (game.isInCheckmate(game.getTeamTurn())) {
            var winner = (game.getTeamTurn() == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK
                    : ChessGame.TeamColor.WHITE;
            msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    game.getTeamTurn() + " is in checkmate, " + winner + " WON!");
            game.resign(game.getTeamTurn());
        }

        return new Object[]{msg, game};
    }

    private static void updateGame(ChessGame game, UserGameCommand command) throws DataAccessException {
        GameData gameData = Server.gameDao.getGame(command.getGameID());
        GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(),
                gameData.gameName(), game);
        Server.gameDao.updateGame(newGameData);
    }

    private static boolean validateUser(UserGameCommand command, Session session) {
        try {
            GameData gameData = Server.gameDao.getGame(command.getGameID());

            if (!sessions.validateSession(command.getGameID(), session) && session != null) {
                return false;
            }

            return ( gameData.whiteUsername().equals(command.getUsername())
                    || gameData.blackUsername().equals(command.getUsername()) );
        } catch (DataAccessException e) {
            return false;
        }
    }

    private static ChessGame.TeamColor nullCheckColor(UserGameCommand command) throws DataAccessException {
        ChessGame.TeamColor color = command.getTeamColor();
        GameData gameData = Server.gameDao.getGame(command.getGameID());

        if (command.getTeamColor() == null) {
            boolean white = false;
            boolean black = false;
            if (gameData.whiteUsername() != null && gameData.blackUsername() != null) {
                white = gameData.whiteUsername().equals(command.getUsername());
                black = gameData.blackUsername().equals(command.getUsername());
            }

            if (white && black) {
                color = gameData.chessGame().getTeamTurn();
            } else if (white) {
                color = ChessGame.TeamColor.WHITE;
            } else if (black) {
                color = ChessGame.TeamColor.BLACK;
            }
        }

        return color;
    }
}
