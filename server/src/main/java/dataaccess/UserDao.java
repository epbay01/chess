package dataaccess;

import model.UserData;

public interface UserDao {
    void createUser(UserData userData);
    UserData getUser(String username);
    void clear();
}
