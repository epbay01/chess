package client;

import model.UserData;
import org.junit.jupiter.api.*;
import requestresult.RegisterRequest;
import server.Server;
import client.ServerFacade;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        facade = new ServerFacade(1234);
        var port = server.run(1234);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void clearTest() {

    }

    @Test
    public void registerTest() {
        UserData userData = new UserData("user", "pass", "email");
        try {
            var result = facade.register(
                    new RegisterRequest(userData.username(), userData.password(), userData.email())
            );
            Assertions.assertNotNull(result);
            Assertions.assertEquals(userData.username(), result.username());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

}
