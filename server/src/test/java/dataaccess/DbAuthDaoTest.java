package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import server.Server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;

public class DbAuthDaoTest {
    private Server server;
    private DbAuthDao dao;
    private AuthData authData;

    @BeforeEach
    void setup() {
        server = new Server();
        server.run(9000);

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("INSERT INTO user VALUES ('user', 'pass', 'email');").executeUpdate();
            conn.prepareStatement("INSERT INTO user VALUES ('user2', 'pass2', 'email2');").executeUpdate();
        } catch (Exception e) {
            System.err.println("During setup: " + e.getMessage());
        }

        dao = new DbAuthDao();
        authData = new AuthData("1234", "user");
        dao.clear();
    }

    @Test
    void clearTest() {
        dao.clear();

        try (Connection conn = DatabaseManager.getConnection()) {
            ResultSet resultSet = conn.prepareStatement("SELECT * FROM auth").executeQuery();
            Assertions.assertFalse(resultSet.next());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void queryAllTest() {
        // adding directly to avoid test circular dependencies
        try(Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("INSERT INTO auth VALUES ('1234', 'user');")
                    .executeUpdate();
            conn.prepareStatement("INSERT INTO auth VALUES ('4321', 'user2');")
                    .executeUpdate();
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        String result = dao.queryAll();

        String expected = "1234,user\n4321,user2\n";

        Assertions.assertEquals(expected, result);
    }

    @AfterEach
    void shutdown() {
        server.stop();
    }
}
