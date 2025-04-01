package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.WebsocketService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

@WebSocket
public class WebsocketHandler {
    public static WebsocketSessions sessions = new WebsocketSessions();

    @OnWebSocketMessage
    private void onMessage(Session session, String message) {
        var command = new Gson().fromJson(message, UserGameCommand.class);

        var outputMessage = switch(command.getCommandType()) {
            case CONNECT -> WebsocketService.connect(command);
            case MAKE_MOVE -> WebsocketService.makeMove(command);
            case LEAVE -> WebsocketService.leave(command);
            case RESIGN -> WebsocketService.resign(command);
        };
    }

    private boolean authenticate(String authToken) {

    }

    private void sendMessage(ServerMessage message) {}
}
