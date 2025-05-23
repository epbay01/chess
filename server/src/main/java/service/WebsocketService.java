package service;

import chess.ChessGame;
import chess.InvalidMoveException;
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
    private final static WebsocketSessions SESSIONS = WebsocketHandler.sessions;

    public static ServerMessage[] connect(UserGameCommand command, Session session) {
        ServerMessage msg1; // board
        ServerMessage msg2; // notification
        String notifMessage;
        if (command.getTeamColor() != null) {
            notifMessage = command.getUsername() + " has joined the game as "
                    + command.getTeamColor().toString().toLowerCase();
        } else {
            notifMessage = command.getUsername() + " has joined the game";
        }

        try {
            GameData gameData = Server.gameDao.getGame(command.getGameID());
            if (gameData.chessGame() == null) {
                throw new ServerException("Game does not exist");
            }
            ChessGame game = gameData.chessGame();
            msg1 = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            SESSIONS.addSession(command.getGameID(), session);

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
            command = new UserGameCommand(command.getCommandType(), command.getAuthToken(),
                    command.getGameID(), command.getUsername(), nullCheckColor(command), command.getMove());
            GameData data = Server.gameDao.getGame(command.getGameID());
            if (data == null) {
                throw new Exception("Game does not exist");
            }
            ChessGame game = data.chessGame();

            if (!validateUser(command, session)) { throw new ServerException("User not in game"); }

            if (command.getMove() != null && command.getTeamColor() == game.getTeamTurn()) {
                notifMessage = command.getUsername() + " has made a move: " + command.getMove().prettyPrint();
                
                game.makeMove(command.getMove());

                var result = checkLogic(data);
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

            command = new UserGameCommand(command.getCommandType(), command.getAuthToken(),
                    command.getGameID(), command.getUsername(), nullCheckColor(command), command.getMove());

            // if user isn't in the game itself, they are an observer and don't need to be removed
            if (validateUser(command, null)) {
                ChessGame.TeamColor color = command.getTeamColor();

                if (color == ChessGame.TeamColor.WHITE) {
                    newGameData = new GameData(
                            gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(),
                            gameData.chessGame()
                    );
                    Server.gameDao.updateGame(newGameData);
                } else if (color == ChessGame.TeamColor.BLACK) {
                    newGameData = new GameData(
                            gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(),
                            gameData.chessGame()
                    );
                    Server.gameDao.updateGame(newGameData);
                }
            }

            SESSIONS.removeSession(command.getGameID(), session);

            msg1 = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    command.getUsername() + " has left the game.");
        } catch (Exception e) {
            msg1 = new ServerMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
        }

        return new ServerMessage[]{msg1};
    }

    public ServerMessage[] resign(UserGameCommand command, Session session) {
        ServerMessage msg1;

        try {
            ChessGame.TeamColor color = nullCheckColor(command);
            command = new UserGameCommand(command.getCommandType(), command.getAuthToken(), command.getGameID(),
                    command.getUsername(), nullCheckColor(command), command.getMove());

            if (!validateUser(command, session)) {
                throw new ServerException("User not in game");
            }

            GameData data = Server.gameDao.getGame(command.getGameID());
            ChessGame game = data.chessGame();

            System.out.println(color + " is resigning on game " + game + " with id " + command.getGameID());

            game.resign(color);

            updateGame(game, command);

            var winner = (color == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK
                    : ChessGame.TeamColor.WHITE;
            var winData = new String[]{
                    (winner == ChessGame.TeamColor.WHITE) ? data.whiteUsername() : data.blackUsername(),
                    data.gameName(),
                    (winner == ChessGame.TeamColor.WHITE) ? data.blackUsername() : data.whiteUsername(),
                    winner.toString()
            };
            msg1 = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    color + " resigned, " + winner + " WON!", winData);

        } catch (Exception e) {
            msg1 = new ServerMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            return new ServerMessage[]{msg1};
        }

        return new ServerMessage[]{msg1};
    }

    private static Object[] checkLogic(GameData data) throws InvalidMoveException {
        ChessGame game = data.chessGame();
        ServerMessage msg = null;

        if (game.isInCheck(game.getTeamTurn())) {
            String teamString = (game.getTeamTurn() == ChessGame.TeamColor.WHITE) ? "White" : "Black";
            msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    teamString + " is now in check");
        }

        if (game.isInStalemate(game.getTeamTurn())) {
            var winData = new String[]{"", data.gameName(), "", ""};
            msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    "Game is in stalemate", winData);
            game.resign(null);
        }

        if (game.isInCheckmate(game.getTeamTurn())) {
            var winner = (game.getTeamTurn() == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK
                    : ChessGame.TeamColor.WHITE;
            var winData = new String[]{
                    (winner == ChessGame.TeamColor.WHITE) ? data.whiteUsername() : data.blackUsername(),
                    data.gameName(),
                    (winner == ChessGame.TeamColor.WHITE) ? data.blackUsername() : data.whiteUsername(),
                    winner.toString()
            };
            msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    game.getTeamTurn() + " is in checkmate, " + winner + " WON!", winData);
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

            if (!SESSIONS.validateSession(command.getGameID(), session) && session != null) {
                System.out.println("failed to validate session");
                return false;
            }

            if (command.getTeamColor() != null) {
                if (command.getTeamColor() == ChessGame.TeamColor.WHITE && gameData.whiteUsername() != null) {
                    return gameData.whiteUsername().equals(command.getUsername());
                } else if (command.getTeamColor() == ChessGame.TeamColor.BLACK && gameData.blackUsername() != null) {
                    return gameData.blackUsername().equals(command.getUsername());
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (DataAccessException e) {
            return false;
        }
    }

    private static ChessGame.TeamColor nullCheckColor(UserGameCommand command) throws DataAccessException {
        ChessGame.TeamColor color = command.getTeamColor();
        GameData gameData = Server.gameDao.getGame(command.getGameID());

        if (command.getTeamColor() == null) {
            System.out.println("team color not passed in, attempting auto-fill");
            boolean white = false;
            boolean black = false;
            if (gameData.whiteUsername() != null && gameData.blackUsername() != null) {
                white = gameData.whiteUsername().equals(command.getUsername());
                black = gameData.blackUsername().equals(command.getUsername());
            } else {
                if (gameData.whiteUsername() != null) { white = true; }
                if (gameData.blackUsername() != null) { black = true; }
            }

            if (white && black) {
                color = gameData.chessGame().getTeamTurn();
            } else if (white) {
                color = ChessGame.TeamColor.WHITE;
            } else if (black) {
                color = ChessGame.TeamColor.BLACK;
            }
        }

        System.out.println("command color: " + color);
        return color;
    }
}
