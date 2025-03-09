package dataaccess;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mindrot.jbcrypt.BCrypt;

import server.Server;
import model.UserData;

import java.sql.Connection;
import java.sql.ResultSet;

public class DbUserDaoTest {
    Server server;
    DbUserDao dao;
    UserData userData;

    @BeforeEach
    void setup() {
        server = new Server();
        server.run(9000);

        dao = new DbUserDao();
        userData = new UserData("user", "pass", "email");

        try {
            dao.clear();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    void manualInsert(UserData data) throws Exception {
        try (Connection conn = DatabaseManager.getConnection()) {
            String strB = "INSERT INTO `user` VALUES ('" + data.username() + "', '" +
                    data.password() + "', '" +
                    data.email() + "')";
            conn.prepareStatement(strB).executeUpdate();
        }
    }

    @Test
    void clearTest() {
        try (Connection conn = DatabaseManager.getConnection()) {
            manualInsert(userData);

            dao.clear();

            ResultSet resultSet = conn.prepareStatement("SELECT * FROM `user`").executeQuery();
            Assertions.assertFalse(resultSet.next());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void queryAllTest() {
        String result = "";

        try {
            manualInsert(userData);
            result = dao.queryAll();
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        String expected = "user,pass,email\n";

        Assertions.assertEquals(expected, result);
    }

    @Test
    void addUserTest() {
        String result = "";
        try {
            dao.createUser(userData);
            result = dao.queryAll();
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        String[] allResults = result.split(",");
        String[] expected = {"user", "pass", "email\n"};

        Assertions.assertEquals(expected[0], allResults[0]);
        Assertions.assertTrue(BCrypt.checkpw(expected[1], allResults[1]));
        Assertions.assertEquals(expected[2], allResults[2]);
    }

    @AfterEach
    void shutdown() {
        server.stop();
    }

    @Test
    void getUserTest() {
        UserData result = null;
        try {
            manualInsert(userData);

            result = dao.getUser(userData.username());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        Assertions.assertEquals(userData, result);
    }
}
