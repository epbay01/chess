package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class WebsocketSessions {
    HashMap<Integer, Set<Session>> sessions;

    public WebsocketSessions() {
        this.sessions = new HashMap<>();
    }

    public void addSession(int gameId, Session session) {
        if (!sessions.containsKey(gameId)) {
            sessions.put(gameId, new HashSet<>());
        }
        sessions.get(gameId).add(session);
    }

    public void removeSession(int gameId, Session session) {
        sessions.get(gameId).remove(session);
        if (sessions.get(gameId).isEmpty()) {
            sessions.remove(gameId);
        }
    }

    public void sendToGame(int gameId, ServerMessage message) throws IOException {
        this.sendToGame(gameId, message, null);
    }
    public void sendToGame(int gameId, ServerMessage message, Session exclude) throws IOException {
        String msg = new Gson().toJson(message);
        for (Session s : sessions.get(gameId)) {
            // compares ip addresses to determine if session is the same
            var ip = (exclude != null) ? exclude.getRemoteAddress() : null;
            if (!s.getRemoteAddress().equals(ip) && s.isOpen()) {
                s.getRemote().sendString(msg);
            } else if (!s.isOpen()) {
                s.close();
                removeSession(gameId, s);
            }
        }
    }

    public boolean validateSession(int gameId, Session session) {
        return sessions.get(gameId).contains(session);
    }
}
