package dataaccess;

import model.UserData;

public class DbUserDao implements UserDao {
    @Override
    public void createUser(UserData userData) {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() {

    }
}
