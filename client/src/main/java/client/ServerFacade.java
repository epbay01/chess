package client;

import chess.ChessGame;
import com.google.gson.Gson;
import exceptions.BadStatusCodeException;
import exceptions.ServerException;
import model.*;
import requestresult.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class ServerFacade {
    private final int port;

    public ServerFacade(int port) {
        this.port = port;
    }

    private String getUrl(String endpoint) {
        return "http://localhost:" + port + endpoint;
    }


    public void clear() throws BadStatusCodeException, ServerException {
        try {
            Result r = makeRequest("/db", "DELETE", null, EmptyResult.class);
            if (r instanceof ErrorResult) {
                throw new ServerException(((ErrorResult) r).getMessage());
            }
        } catch (IOException e) {
            throw new ServerException(e.getMessage());
        }
    }

    public AuthData register(UserData userData) throws BadStatusCodeException, ServerException {
        var registerRequest = new RegisterRequest(
                userData.username(), userData.password(), userData.email()
        );
        try {
            Result r = makeRequest("/user", "POST", registerRequest, LoginResult.class);
            return new AuthData(((LoginResult) r).getAuthToken(), ((LoginResult) r).getUsername());
        } catch (IOException e) {
            throw new ServerException(e.getMessage());
        }
    }

    public AuthData login(UserData userData) throws BadStatusCodeException, ServerException {
        var loginRequest = new LoginRequest(userData.username(), userData.password());
        try {
            Result r = makeRequest("/session", "POST", loginRequest, LoginResult.class);
            return new AuthData(((LoginResult) r).getAuthToken(), ((LoginResult) r).getUsername());
        } catch (IOException e) {
            throw new ServerException(e.getMessage());
        }
    }

    public void logout(AuthData data) throws BadStatusCodeException, ServerException {
        var logoutRequest = new AuthenticatedRequest(data.authToken());
        try {
            Result r = makeRequest("/session", "DELETE", logoutRequest, EmptyResult.class);
            if (r instanceof ErrorResult) {
                throw new ServerException(((ErrorResult) r).getMessage());
            }
        } catch (IOException e) {
            throw new ServerException(e.getMessage());
        }
    }

    public int createGame(AuthData authData, String name) throws BadStatusCodeException, ServerException {
        CreateGameRequest request = new CreateGameRequest(authData.authToken(), name);
        try {
            Result r = makeRequest("/game", "POST", request, CreateGameResult.class);
            if (r instanceof ErrorResult) {
                throw new ServerException(((ErrorResult) r).getMessage());
            } else {
                return ((CreateGameResult) r).getGameID();
            }
        } catch (IOException e) {
            throw new ServerException(e.getMessage());
        }
    }

    public List<GameData> listGames(AuthData authData) throws BadStatusCodeException, ServerException {
        var request = new AuthenticatedRequest(authData.authToken());
        try {
            Result r = makeRequest("/game", "GET", request, ListGamesResult.class);

            if (r instanceof ErrorResult) {
                throw new ServerException(((ErrorResult) r).getMessage());
            }

            return ((ListGamesResult) r).getGames();
        } catch (IOException e) {
            throw new ServerException(e.getMessage());
        }
    }

    public void joinGame(AuthData authData, int gameID, ChessGame.TeamColor color) throws BadStatusCodeException, ServerException {
        var request = new JoinGameRequest(authData.authToken(), Integer.toString(gameID), color.toString());

        try {
            Result r = makeRequest("/game", "PUT", request, EmptyResult.class);

            if (r instanceof ErrorResult) {
                throw new ServerException(((ErrorResult) r).getMessage());
            }
        } catch (IOException e) {
            throw new ServerException(e.getMessage());
        }
    }


    private <T, V extends Result> Result makeRequest (
            String endpoint, String method, T request, Class<V> responseClass
    ) throws BadStatusCodeException, IOException {
        HttpURLConnection http;
        try {
            URL url = new URI(getUrl(endpoint)).toURL();
            http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (request instanceof AuthenticatedRequest) {
                http.setRequestProperty("authorization", ((AuthenticatedRequest) request).authToken());
            } else if (request instanceof CreateGameRequest) {
                http.setRequestProperty("authorization", ((CreateGameRequest) request).authToken());
            } else if (request instanceof JoinGameRequest) {
                http.setRequestProperty("authorization", ((JoinGameRequest) request).authToken());
            }

            if (method.equals("POST") || method.equals("PUT")) {
                try (OutputStream body = http.getOutputStream()) {
                    String bodyData = new Gson().toJson(request);
                    body.write(bodyData.getBytes());
                }
            }

            http.connect();
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }

        int statusCode = http.getResponseCode();
        if (statusCode / 100 != 2) {
            throw new BadStatusCodeException(statusCode, http.getResponseMessage());
        }

        try (InputStream body = http.getInputStream()) {
            Reader reader = new InputStreamReader(body);
            return new Gson().fromJson(reader, responseClass);
        } catch (Exception e) {
            try (InputStream body = http.getInputStream()) {
                Reader reader = new InputStreamReader(body);
                return new Gson().fromJson(reader, ErrorResult.class);
            } catch (Exception f) {
                throw new ServerException(f.getMessage());
            }
        }
    }

//    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass)
//    throws ResponseException {
//        String serverUrl = getUrl(path);
//        try {
//            URL url = (new URI(serverUrl)).toURL();
//            HttpURLConnection http = (HttpURLConnection) url.openConnection();
//            http.setRequestMethod(method);
//            http.setDoOutput(true);
//
//            writeBody(request, http);
//            http.connect();
//            throwIfNotSuccessful(http);
//            return readBody(http, responseClass);
//        } catch (ResponseException ex) {
//            throw ex;
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
}
