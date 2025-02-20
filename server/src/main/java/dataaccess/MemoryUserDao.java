package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryUserDao implements UserDao {
    ArrayList<UserData> db;

    public MemoryUserDao() {
        db = new ArrayList<>();
    }

    public MemoryUserDao(ArrayList<UserData> db) {
        this.db = db;
    }

    @Override
    public void createUser(UserData userData) {
        db.add(userData);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : db) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        throw new DataAccessException("User " + username + " does not exist");
    }

    @Override
    public void clear() {
        db.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemoryUserDao that = (MemoryUserDao) o;
        return Objects.equals(db, that.db);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(db);
    }
}
