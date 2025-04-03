package client;

import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import javax.websocket.*;

import java.net.URI;

public class WebsocketFacade extends Endpoint {
    private final Session session;

    public WebsocketFacade(String url) throws Exception {
        var uri = new URI(url.replace("http", "ws") + "/ws");

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler((MessageHandler.Whole<String>) WebsocketFacade::handler);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    // PRIVATE ON MESSAGE METHODS

    private static void handler(String message) {
        var msg = new Gson().fromJson(message, ServerMessage.class);

        switch (msg.getServerMessageType()) {
            case LOAD_GAME -> loadGame(msg);
            case NOTIFICATION -> notify(msg);
            case ERROR -> error(msg);
        }
    }

    private static void loadGame(ServerMessage message) {}

    private static void notify(ServerMessage message) {}

    private static void error(ServerMessage message) {}

    // PUBLIC SEND METHODS

    public static void connect() {}

    public static void make_move(ChessMove move) {}

    public static void leave() {}

    public static void resign() {}

    private void send(UserGameCommand command) {}
}
