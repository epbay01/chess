package service;
import dataaccess.AuthDao;
import dataaccess.MemoryAuthDao;
import dataaccess.MemoryUserDao;
import dataaccess.UserDao;
import model.AuthData;
import model.UserData;
import requestresult.*;

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
        return null;
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
