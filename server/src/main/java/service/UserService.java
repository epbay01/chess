package service;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import requestresult.*;
import server.Server;

import java.util.UUID;

public class UserService {
    private static UserDao userDao;
    private static AuthDao authDao;

    public UserService() {
        userDao = Server.userDao;
        authDao = Server.authDao;
    }

    public static Result login(LoginRequest req) {
        AuthData newData;

        // verify not already logged in
        try {
            authDao.getAuth(req.username());
            return new ErrorResult("Already logged in");
        } catch (DataAccessException ignored) {}

        try {
            UserData user = userDao.getUser(req.username());
            if (user.password().equals(req.password())) {
                newData = new AuthData(UUID.randomUUID().toString(), req.username());
                authDao.addAuth(newData);
            } else {
                return new ErrorResult("Not authenticated");
            }
        } catch (DataAccessException e) {
            return new ErrorResult(e.getMessage());
        }
        return new LoginResult(newData.authToken(), req.username());
    }

    public static Result register(RegisterRequest req) {

        try {
            userDao.getUser(req.username());
            return new ErrorResult("Username already exists");
        } catch (DataAccessException ignored) {}

        UserData data = new UserData(req.username(), req.password(), req.email());
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, req.username());

        userDao.createUser(data);
        authDao.addAuth(authData);

        return new LoginResult(authToken, req.username());
    }

    public static Result logout(AuthenticatedRequest req) {
        if (!authenticate(req.authToken())) {
            return new ErrorResult("Not authenticated");
        }
        return null;
    }

    public static boolean authenticate(String authToken) {
        AuthData dbData;

        try {
            dbData = authDao.getAuthByToken(authToken);
        } catch (Exception e) {
            return false;
        }

        return dbData.authToken().equals(authToken);
    }
}
