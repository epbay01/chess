package service;

import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public class WebsocketService {
    public static ServerMessage connect(UserGameCommand command) {}
    public static ServerMessage makeMove(UserGameCommand command) {}
    public static ServerMessage leave(UserGameCommand command) {}
    public static ServerMessage resign(UserGameCommand command) {}
}
