package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class UserDaoTest {
    UserDao dao;
    UserData userData;

    @BeforeEach
    void setUp() {
        dao = new MemoryUserDao();
        userData = new UserData("username", "password", "email@email.com");
    }

    @Test
    void testCreateUser() {
        dao.createUser(userData);

        var list = new ArrayList<UserData>();
        list.add(userData);
        var expected = new MemoryUserDao(list);

        Assertions.assertEquals(dao, expected);
    }

    @Test
    void testGetUser() {
        UserData result;
        dao.createUser(userData);
        try {
            result = dao.getUser("username");
        } catch (Exception e) {
            Assertions.fail(e);
            return;
        }

        Assertions.assertEquals(result, userData);
    }

    @Test
    void testClear() {
        dao.createUser(userData);
        dao.clear();

        var expected = new MemoryUserDao();

        Assertions.assertEquals(dao, expected);
    }

    @Test
    void testGetUserFail() {
        Assertions.assertThrows(DataAccessException.class, () -> dao.getUser("username"));
    }
}
