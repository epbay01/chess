package server;

import com.google.gson.Gson;
import requestresult.*;
import spark.*;
import service.*;

public class Handler {
    public static Object handleRegister(Request req, Response res) { return res.body(); }
    public static Object handleLogin(Request req, Response res) { return res.body(); }
    public static Object handleLogout(Request req, Response res) { return res.body(); }
    public static Object handleListGames(Request req, Response res) { return res.body(); }
    public static Object handleCreateGame(Request req, Response res) { return res.body(); }
    public static Object handleJoinGame(Request req, Response res) { return res.body(); }

    public static Object handleClear(Request req, Response res) {
        GameService gameService = new GameService();
        Gson gson = new Gson();
        res.type("application/json");

        Result result = gameService.clear();
        System.out.println(gson.toJson(result));

        if (result instanceof ErrorResult) {
            switch (((ErrorResult) result).getMessage()) {
                case "Not authenticated":
                    res.status(401);
                default:
                    res.status(400);
            }
        } else {
            res.status(200);
        }

        System.out.println(gson.toJson(result));

        res.body(gson.toJson(result));
        return res.body();
    }
}
