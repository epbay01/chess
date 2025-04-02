package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.UserService;
import service.WebsocketService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

@WebSocket
public class WebsocketHandler {
    public static WebsocketSessions sessions = new WebsocketSessions();

    @OnWebSocketMessage
    private void onMessage(Session session, String message) {
        var command = new Gson().fromJson(message, UserGameCommand.class);
        ServerMessage[] outputMessage = new ServerMessage[1];

        if (!authenticate(command)) {
            outputMessage = new ServerMessage[]{
                    new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Not authenticated")
            };
        } else {
            outputMessage = switch(command.getCommandType()) {
                case CONNECT -> WebsocketService.connect(command, session);
                case MAKE_MOVE -> WebsocketService.makeMove(command, session);
                case LEAVE -> WebsocketService.leave(command, session);
                case RESIGN -> WebsocketService.resign(command, session);
            };
        }

        sendMessage(outputMessage, session, command);
    }

    private boolean authenticate(UserGameCommand command) {
        return UserService.authenticate(command.getAuthToken());
    }

    private void sendMessage(ServerMessage[] message, Session session, UserGameCommand command) {
        String json = new Gson().toJson(message);

        try {
            if (message[0].getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
                session.getRemote().sendString(json);
                return;
            }

            switch (command.getCommandType()) {
                case CONNECT:
                    session.getRemote().sendString(json); // board
                    sessions.sendToGame(command.getGameID(), message[1], session); // notification
                case MAKE_MOVE:
                    sessions.sendToGame(command.getGameID(), message[0]); // updated board
                    sessions.sendToGame(command.getGameID(), message[1], session); // notification
                    if (message.length > 2) {
                        sessions.sendToGame(command.getGameID(), message[2]); // check
                    }
                    break;
                case LEAVE:
                    sessions.sendToGame(command.getGameID(), message[0], session); // notification
                    break;
                case RESIGN:
                    sessions.sendToGame(command.getGameID(), message[0]); // notification
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
