package service;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import requestresult.*;

import java.util.UUID;

public class UserService {
    private UserDao userDao;
    private AuthDao authDao;

    public UserService() {
        userDao = new MemoryUserDao();
        authDao = new MemoryAuthDao();
    }

    public Result login(LoginRequest req) {
        return null;
    }

    public Result register(RegisterRequest req) {

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

    public Result logout(AuthenticatedRequest req) {
        if (!authenticate(req.authToken())) {
            return new ErrorResult("Not authenticated");
        }
        return null;
    }

    private boolean authenticate(String authToken) {
        AuthData dbData;

        try {
            dbData = authDao.getAuthByToken(authToken);
        } catch (Exception e) {
            return false;
        }

        return dbData.authToken().equals(authToken);
    }
}
