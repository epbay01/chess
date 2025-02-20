package dataaccess;

import model.AuthData;

public interface AuthDao {
    void addAuth(AuthData authData);
    AuthData getAuth(String username) throws DataAccessException;
    AuthData getAuthByToken(String token) throws DataAccessException;
    void deleteAuth(String username) throws DataAccessException;
    void clear();
}
