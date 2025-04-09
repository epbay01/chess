package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.AuthData;
import ui.GameRepl;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import javax.websocket.*;

import java.io.IOException;
import java.net.URI;

public class WebsocketFacade extends Endpoint {
    private final Session session;
    private final GameRepl repl;

    public WebsocketFacade(ServerFacade facade, GameRepl repl) throws Exception {
        this.repl = repl;

        var url = facade.getUrl("/ws");
        var uri = new URI(url.replace("http", "ws"));

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) { handler(message); }
        });
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    // PRIVATE ON MESSAGE METHODS

    private void handler(String message) {
        var msg = new Gson().fromJson(message, ServerMessage.class);

        switch (msg.getServerMessageType()) {
            case LOAD_GAME -> loadGame(msg);
            case NOTIFICATION -> notify(msg);
            case ERROR -> error(msg);
        }
    }

    private void loadGame(ServerMessage message) {
        if (repl.game == null) {
            repl.game = message.getGame();
            repl.printBoard(message.getGame().getBoard());
            System.out.print("\n"); // sometimes get input prints before, sometimes after
            if (repl.observing) {
                repl.getInputObserverPrint();
            } else {
                repl.getInputPrint();
            }
        } else {
            repl.game = message.getGame();
        }
    }

    private void notify(ServerMessage message) {
        repl.notify(message.getMessage(), message.getWinData());
    }

    private void error(ServerMessage message) {
        repl.error(message.getMessage());
    }

    // PUBLIC SEND METHODS

    public void command(UserGameCommand.CommandType commandType,
                        AuthData authData,
                        int gameId,
                        ChessGame.TeamColor color) { command(commandType, authData, gameId, color, null); }
    public void command(UserGameCommand.CommandType commandType,
                        AuthData authData,
                        int gameId,
                        ChessGame.TeamColor color,
                        ChessMove move) {
        var command = new UserGameCommand(
                commandType, authData.authToken(), gameId, authData.username(), color, move
        );
        try {
            send(command);
        } catch (IOException e) {
            repl.error("Connection failed");
        }
    }

    private void send(UserGameCommand command) throws IOException {
        this.session.getBasicRemote().sendText(
                new Gson().toJson(command)
        );
    }
}
