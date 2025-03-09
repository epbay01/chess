package service;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import requestresult.*;
import server.Server;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    private static UserDao userDao = Server.userDao;
    private static AuthDao authDao = Server.authDao;

    public static Result login(LoginRequest req) {
        AuthData newData;

        try {
            // TODO: CHANGE THIS PASSWORD COMPARISON!!!
            UserData user = userDao.getUser(req.username());
            if (user.password().equals(req.password())) {
                newData = new AuthData(UUID.randomUUID().toString(), req.username());
                authDao.addAuth(newData);
            } else {
                return new ErrorResult("Error: Not authenticated");
            }
        } catch (DataAccessException e) {
            return new ErrorResult("Error: " + e.getMessage());
        }
        return new LoginResult(newData.authToken(), req.username());
    }

    public static Result register(RegisterRequest req) {

        if (Objects.equals(req.username(), "") ||
                Objects.equals(req.password(), "") ||
                req.username() == null ||
                req.password() == null) {
            return new ErrorResult("Error: Username or password is invalid");
        }

        try {
            userDao.getUser(req.username());
            return new ErrorResult("Error: Username already exists");
        } catch (DataAccessException ignored) {}

        UserData data = new UserData(req.username(), req.password(), req.email());
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, req.username());

        try { // TODO: change later
            userDao.createUser(data);
            authDao.addAuth(authData);
        } catch (DataAccessException ignored) {}

        return new LoginResult(authToken, req.username());
    }

    public static Result logout(AuthenticatedRequest req) {
        if (!authenticate(req.authToken())) {
            return new ErrorResult("Error: Not authenticated");
        }

        try {
            AuthData data = authDao.getAuthByToken(req.authToken());
            authDao.deleteAuth(data.authToken());
        } catch (DataAccessException e) {
            return new ErrorResult("Error: " + e.getMessage());
        }
        return new EmptyResult();
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
