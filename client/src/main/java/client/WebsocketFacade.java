package client;

import chess.ChessMove;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public class WebsocketFacade {
    public static void connect() {}

    public static void make_move(ChessMove move) {}

    public static void leave() {}

    public static void resign() {}

    private void send(UserGameCommand command) {}

    private void receive(ServerMessage message) {}
}
