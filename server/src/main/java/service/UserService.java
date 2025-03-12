package service;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import requestresult.*;
import server.Server;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    private static final UserDao userDao = Server.userDao;
    private static final AuthDao authDao = Server.authDao;

    private static boolean comparePasswords(UserData userData, String password) {
        if (Server.useMemory) {
            return Objects.equals(userData.password(), password);
        } else {
            return BCrypt.checkpw(password, userData.password());
        }
    }

    public static Result login(LoginRequest req) {
        AuthData newData;

        try {
            UserData user = userDao.getUser(req.username());
            if (comparePasswords(user, req.password())) {
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

        try {
            userDao.createUser(data);
            authDao.addAuth(authData);
        } catch (DataAccessException e) {
            return new ErrorResult("Error: " + e.getMessage());
        }

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
