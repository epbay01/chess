package server;

import com.google.gson.Gson;
import java.lang.String;

import requestresult.*;
import spark.*;
import service.*;

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
        AuthenticatedRequest logoutReq = new AuthenticatedRequest(req.headers("authorization"));
        return handle(UserService::logout, logoutReq, res);
    }
    public static Object handleListGames(Request req, Response res) {
        AuthenticatedRequest listReq = new AuthenticatedRequest(req.headers("authorization"));
        return handle(GameService::listGames, listReq, res);
    }
    public static Object handleCreateGame(Request req, Response res) {
        CreateGameRequest createReq = new CreateGameRequest(
                req.headers("Authorization"),
                new Gson().fromJson(req.body(), String.class)
        );
        return handle(GameService::createGame, createReq, res);
    }
    public static Object handleJoinGame(Request req, Response res) {
        JoinGameRequest temp = new Gson().fromJson(req.body(), JoinGameRequest.class);
        JoinGameRequest joinReq = new JoinGameRequest(
                req.headers("Authorization"),
                temp.gameId(),
                temp.playerColor()
        );
        return handle(GameService::joinGame, joinReq, res);
    }

    // generic static handle doesn't work because clear() doesn't fit the functional interface (it has no parameters)
    public static Object handleClear(Request req, Response res) {
        res.type("application/json");

        Result result = GameService.clear();

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
            String str = ((ErrorResult) result).getMessage().substring(7);

            switch (str) {
                case "Username or password is invalid":
                    response.status(400);
                    break;
                case "Not authenticated", "User does not exist", "Token not found":
                    response.status(401);
                    break;
                case "Username not found", "Game not found":
                    response.status(404);
                    break;
                case "Not implemented":
                    response.status(501);
                    break;
                case "Username already exists":
                    response.status(403);
                    break;
                default:
                    response.status(500);
            }
        } else {
            response.status(200);
        }
    }
}
