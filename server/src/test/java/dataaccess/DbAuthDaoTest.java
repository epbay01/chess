package dataaccess;

import model.AuthData;
import model.UserData;
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
    private DbUserDao userDao;
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

        try {
            dao = new DbAuthDao();
            authData = new AuthData("1234", "user");
            dao.clear();

            userDao = new DbUserDao();
            userDao.clear();
            userDao.createUser(new UserData("user", "pass", "email"));
            userDao.createUser(new UserData("user2", "pass2", "email2"));
            // so foreign key constraints pass
        } catch (Exception e) {
            System.err.println("During setup: " + e.getMessage());
        }
    }

    @Test
    void clearTest() {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("INSERT INTO auth VALUES ('1234', 'user');").executeUpdate();

            dao.clear();

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

    @Test
    void addAuthTest() {
        String result = "";
        try {
            dao.addAuth(authData);
            result = dao.queryAll();
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        String expected = "1234,user\n";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void addAuthFailTest() {
        Assertions.assertThrows(DataAccessException.class,
                () -> dao.addAuth(new AuthData(null, null)));
    }

    @Test
    void deleteAuthTest() {
        String result = "";
        try {
            dao.addAuth(authData);
            dao.deleteAuth(authData.authToken());
            result = dao.queryAll();
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        String expected = "";

        Assertions.assertEquals(expected, result);
    }

    // deleteAuth never fails

    @Test
    void getAuthTest() {
        AuthData result = null;
        try {
            dao.addAuth(authData);
            result = dao.getAuth("user");
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        Assertions.assertEquals(authData, result);
    }

    @Test
    void getAuthFailTest() {
        Assertions.assertThrows(DataAccessException.class, () -> dao.getAuth("user"));
    }

    @Test
    void getAuthByTokenTest() {
        AuthData result = null;
        try {
            dao.addAuth(authData);
            result = dao.getAuthByToken("1234");
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        Assertions.assertEquals(authData, result);
    }

    @Test
    void getAuthByTokenFailTest() {
        Assertions.assertThrows(DataAccessException.class, () -> dao.getAuthByToken("1234"));
    }

    @AfterEach
    void shutdown() {
        server.stop();
    }
}
