package service;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.Server;
import requestresult.*;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    private static final UserDao USER_DAO = Server.userDao;
    private static final AuthDao AUTH_DAO = Server.authDao;

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
            UserData user = USER_DAO.getUser(req.username());
            if (!comparePasswords(user, req.password())) {
                return new ErrorResult("Error: Not authenticated");
            }

            do {
                newData = new AuthData(UUID.randomUUID().toString(), req.username());
                try {
                    AUTH_DAO.getAuthByToken(newData.authToken());
                } catch (DataAccessException e) {
                    AUTH_DAO.addAuth(newData);
                    return new LoginResult(newData.authToken(), req.username());
                }
            } while (true); // loops until successfully finds an unique token
        } catch (DataAccessException e) {
            return new ErrorResult("Error: " + e.getMessage());
        }
    }

    public static Result register(RegisterRequest req) {

        if (Objects.equals(req.username(), "") ||
                Objects.equals(req.password(), "") ||
                req.username() == null ||
                req.password() == null) {
            return new ErrorResult("Error: Username or password is invalid");
        }

        try {
            USER_DAO.getUser(req.username());
            return new ErrorResult("Error: Username already exists");
        } catch (DataAccessException ignored) {}

        UserData data = new UserData(req.username(), req.password(), req.email());
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, req.username());

        try {
            USER_DAO.createUser(data);
            AUTH_DAO.addAuth(authData);
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
            AuthData data = AUTH_DAO.getAuthByToken(req.authToken());
            AUTH_DAO.deleteAuth(data.authToken());
        } catch (DataAccessException e) {
            return new ErrorResult("Error: " + e.getMessage());
        }
        return new EmptyResult();
    }

    public static boolean authenticate(String authToken) {
        AuthData dbData;

        try {
            dbData = AUTH_DAO.getAuthByToken(authToken);
        } catch (Exception e) {
            return false;
        }

        return dbData.authToken().equals(authToken);
    }
}
