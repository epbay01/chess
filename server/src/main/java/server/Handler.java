package server;

import com.google.gson.Gson;
import java.lang.String;

import model.AuthData;
import requestresult.*;
import spark.*;
import service.*;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Handler {
    @FunctionalInterface
    public interface ServiceFunction<T> {
        Result process(T req);
    }

    public static Object handleRegister(Request req, Response res) {
        Gson gson = new Gson();
        RegisterRequest regReq = gson.fromJson(req.body(), RegisterRequest.class);
        return handle(UserService::register, regReq, res);
    }

    public static Object handleLogin(Request req, Response res) {
        Gson gson = new Gson();
        LoginRequest loginReq = gson.fromJson(req.body(), LoginRequest.class);
        return handle(UserService::login, loginReq, res);
    }

    public static Object handleLogout(Request req, Response res) {
        AuthenticatedRequest logoutReq = new Gson().fromJson(req.body(), AuthenticatedRequest.class);
        return handle(UserService::logout, logoutReq, res);
    }
    public static Object handleListGames(Request req, Response res) {
        AuthenticatedRequest listReq = new Gson().fromJson(req.body(), AuthenticatedRequest.class);
        return handle(GameService::listGames, listReq, res);
    }
    public static Object handleCreateGame(Request req, Response res) { return res.body(); }
    public static Object handleJoinGame(Request req, Response res) { return res.body(); }

    // generic static handle doesn't work because clear() doesn't fit the functional interface (it has no parameters)
    public static Object handleClear(Request req, Response res) {
        GameService gameService = new GameService();
        res.type("application/json");

        Result result = gameService.clear();

        errorCheck(result, res);

        res.body(Result.toJson(result));
        return res.body();
    }

    public static <V> Object handle(ServiceFunction<V> service, V request, Response res) {
        Result result = service.process(request);

        errorCheck(result, res);

        res.body(Result.toJson(result));
        res.type("application/json");
        return res.body();
    }

    private static void errorCheck(Result result, Response response) {
        if (result instanceof ErrorResult) {
            String str = ((ErrorResult) result).getMessage();

            switch (str) {
                case "Not authenticated":
                    response.status(401);
                    break;
                case "Token not found", "Username not found", "Game not found":
                    response.status(404);
                    break;
                default:
                    /* 400 error messages:
                        "Username already exists"
                        "Already logged in"
                     */
                    response.status(400);
            }
        } else {
            response.status(200);
        }
    }
}
