import exceptions.BadStatusCodeException;
import model.*;
import requestresult.*;

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

    public LoginResult register(RegisterRequest registerRequest) throws BadStatusCodeException {
        Result r = makeRequest("/user", "POST", registerRequest, false);
        return (LoginResult) r;
    }

    public LoginResult login(LoginRequest loginRequest) throws BadStatusCodeException {
        Result r = makeRequest("/session", "POST", loginRequest, false);
        return (LoginResult) r;
    }

    public void logout(AuthenticatedRequest logoutRequest) throws BadStatusCodeException {
        Result r = makeRequest("/session", "DELETE", logoutRequest, true);
    }

    private <T> Result makeRequest(
            String endpoint, String method, T request, boolean hasAuthCookie
    ) throws BadStatusCodeException {
        try {
            URL url = new URI(getUrl(endpoint)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (hasAuthCookie) {
                http.setRequestProperty("Authentication", ((AuthenticatedRequest)request).authToken());
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
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
