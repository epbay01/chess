package dataaccess;

import model.AuthData;

public interface AuthDao {
    void addAuth(AuthData authData) throws DataAccessException;
    AuthData getAuth(String username) throws DataAccessException;
    AuthData getAuthByToken(String token) throws DataAccessException;
    void deleteAuth(String token) throws DataAccessException;
    void clear() throws DataAccessException;
}
