package server;

import com.google.gson.Gson;
import org.eclipse.jetty.server.Authentication;
import requestresult.*;
import spark.*;
import service.*;

public class Handler {
    public static Object handleRegister(Request req, Response res) {
        UserService userService = new UserService();
        res.type("application/json");
        Gson gson = new Gson();

        RegisterRequest regReq = gson.fromJson(req.body(), RegisterRequest.class);
        Result result = userService.register(regReq);

        errorCheck(result, res);

        res.body(Result.toJson(result));
        return res.body();
    }

    public static Object handleLogin(Request req, Response res) { return res.body(); }
    public static Object handleLogout(Request req, Response res) { return res.body(); }
    public static Object handleListGames(Request req, Response res) { return res.body(); }
    public static Object handleCreateGame(Request req, Response res) { return res.body(); }
    public static Object handleJoinGame(Request req, Response res) { return res.body(); }

    public static Object handleClear(Request req, Response res) {
        GameService gameService = new GameService();
        res.type("application/json");

        Result result = gameService.clear();
        System.out.println(Result.toJson(result));

        errorCheck(result, res);

        System.out.println(Result.toJson(result));

        res.body(Result.toJson(result));
        return res.body();
    }

    private static void errorCheck(Result result, Response response) {
        if (result instanceof ErrorResult) {
            switch (((ErrorResult) result).getMessage()) {
                case "Not authenticated":
                    response.status(401);
                    break;
                default:
                    /* 400 error messages:
                        "Username already exists"
                     */
                    response.status(400);
            }
        } else {
            response.status(200);
        }
    }
}
