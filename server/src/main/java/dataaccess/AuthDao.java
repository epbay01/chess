package dataaccess;

import model.AuthData;

public interface AuthDao {
    void addAuth(AuthData authData);
    AuthData getAuth(String username);
    void deleteAuth(String username);
    void clear();
}
