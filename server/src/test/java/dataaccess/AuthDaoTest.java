package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class AuthDaoTest {
    private MemoryAuthDao dao;
    private AuthData authData;

    @BeforeEach
    void setUp() {
        dao = new MemoryAuthDao();
        authData = new AuthData("1234", "username");
    }

    @Test
    void testAddAuth() {
        dao.addAuth(authData);

        ArrayList<AuthData> list = new ArrayList<>();
        list.add(authData);
        var expected = new MemoryAuthDao(list);

        Assertions.assertEquals(dao, expected);
    }

    @Test
    void testGetAuth() {
        dao.addAuth(authData);

        ArrayList<AuthData> list = new ArrayList<>();
        list.add(authData);
        var expected = new MemoryAuthDao(list);

        try {
            Assertions.assertEquals(dao.getAuth("username"), expected.getAuth("username"));
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void testGetAuthByToken() {
        AuthData result;
        dao.addAuth(authData);
        try {
            result = dao.getAuthByToken("1234");
        } catch (Exception e) {
            Assertions.fail();
            return;
        }

        Assertions.assertEquals(result, authData);
    }

    @Test
    void testDeleteAuth() {
        dao.addAuth(authData);
        try {
            dao.deleteAuth(authData.authToken());
        } catch (Exception e) {
            Assertions.fail();
        }

        var expected = new MemoryAuthDao();

        Assertions.assertEquals(dao, expected);
    }

    @Test
    void testClearAuth() {
        dao.addAuth(authData);
        dao.addAuth(new AuthData("5678", "user"));
        dao.clear();

        var expected = new MemoryAuthDao();

        Assertions.assertEquals(dao, expected);
    }

    @Test
    void testGetAuthFail() {
        Assertions.assertThrows(DataAccessException.class, () -> dao.getAuth("username"));
    }

    @Test
    void testDeleteAuthFail() {
        Assertions.assertThrows(DataAccessException.class, () -> dao.deleteAuth("username"));
    }
}
