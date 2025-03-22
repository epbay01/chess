package client;

import com.google.gson.Gson;
import exceptions.BadStatusCodeException;
import exceptions.ServerException;
import model.*;
import requestresult.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final int port;

    public ServerFacade(int port) {
        this.port = port;
    }

    private String getUrl(String endpoint) {
        return "http://localhost:" + port + endpoint;
    }

    public void clear() {}

    public AuthData register(RegisterRequest registerRequest) throws BadStatusCodeException, ServerException {
        try {
            Result r = makeRequest("/user", "POST", registerRequest, false, LoginResult.class);
            return new AuthData(((LoginResult) r).getAuthToken(), ((LoginResult) r).getUsername());
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }

    public AuthData login(LoginRequest loginRequest) throws BadStatusCodeException, ServerException {
        try {
            Result r = makeRequest("/session", "POST", loginRequest, false, LoginResult.class);
            return new AuthData(((LoginResult) r).getAuthToken(), ((LoginResult) r).getUsername());
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }

    public void logout(AuthenticatedRequest logoutRequest) throws BadStatusCodeException, ServerException {
        try {
            Result r = makeRequest("/session", "DELETE", logoutRequest, true, EmptyResult.class);
            if (r instanceof ErrorResult) {
                throw new ServerException(((ErrorResult) r).getMessage());
            }
        } catch (IOException e) {
            throw new ServerException(e.getMessage());
        }
    }

    private <T, V extends Result> Result makeRequest (
            String endpoint, String method, T request, boolean hasAuthCookie, Class<V> responseClass
    ) throws BadStatusCodeException, IOException {
        try {
            URL url = new URI(getUrl(endpoint)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (hasAuthCookie) {
                http.setRequestProperty("Authentication", ((AuthenticatedRequest)request).authToken());
            }

            if (request != null) {
                try (OutputStream body = http.getOutputStream()) {
                    String bodyData = new Gson().toJson(request);
                    body.write(bodyData.getBytes());
                }
            }

            http.connect();
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

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ErrorResult(e.getMessage());
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
