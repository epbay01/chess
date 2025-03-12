package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestresult.*;
import server.Server;

public class UserServiceTest {
    @BeforeEach
    void init() {
        try {
            Server.authDao.clear();
            Server.userDao.clear();
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void registerTest() {
        RegisterRequest request = new RegisterRequest("user", "pass", "email");
        Result result = UserService.register(request);

        // have no way of replicating UUID generation, so comparing class to verify not an error
        Assertions.assertEquals(LoginResult.class, result.getClass());
    }

    @Test
    void registerFailTest() {
        RegisterRequest request = new RegisterRequest("user", "pass", "email");
        UserService.register(request);
        Result result = UserService.register(request);

        var expected = new ErrorResult("Error: Username already exists");

        Assertions.assertEquals(expected, result);
    }

    @Test
    void loginTest() {
        RegisterRequest registerReq = new RegisterRequest("user", "pass", "email");
        UserService.register(registerReq);
        LoginRequest request = new LoginRequest("user", "pass");
        Result result = UserService.login(request); // returns a second auth token

        Assertions.assertEquals(LoginResult.class, result.getClass()); // same as register case
    }

    @Test
    void loginFailTest() {
        RegisterRequest registerReq = new RegisterRequest("user", "pass", "email");
        UserService.register(registerReq);
        LoginRequest request = new LoginRequest("user", "wrongpass");
        Result result = UserService.login(request);

        var expected = new ErrorResult("Error: Not authenticated");

        Assertions.assertEquals(expected, result);
    }

    @Test
    void logoutTest() {
        RegisterRequest registerReq = new RegisterRequest("user", "pass", "email");
        Result r = UserService.register(registerReq);
        if (r.getClass() != LoginResult.class) {
            Assertions.fail();
        } else {
            AuthenticatedRequest request = new AuthenticatedRequest(((LoginResult) r).getAuthToken());
            Result result = UserService.logout(request);

            EmptyResult expected = new EmptyResult();

            Assertions.assertEquals(expected, result);
        }
    }

    @Test
    void logoutFailTest() {
        RegisterRequest registerReq = new RegisterRequest("user", "pass", "email");
        Result r = UserService.register(registerReq);

        AuthenticatedRequest request = new AuthenticatedRequest("bad token");
        Result result = UserService.logout(request);

        ErrorResult expected = new ErrorResult("Error: Not authenticated");

        Assertions.assertEquals(expected, result);
    }
}
