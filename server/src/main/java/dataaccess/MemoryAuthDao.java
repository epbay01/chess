package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MemoryAuthDao implements AuthDao {
    private List<AuthData> db;

    public MemoryAuthDao() {
        db = new ArrayList<>();
    }

    public MemoryAuthDao(List<AuthData> db) {
        this.db = db;
    }

    @Override
    public void addAuth(AuthData authData) {
        db.add(authData);
    }

    @Override
    public AuthData getAuth(String username) throws DataAccessException {
        for (AuthData authData : db) {
            if (authData.username().equals(username)) {
                return authData;
            }
        }
        throw new DataAccessException("Username not found");
    }

    @Override
    public AuthData getAuthByToken(String token) throws DataAccessException {
        for (AuthData authData : db) {
            if (authData.authToken().equals(token)) {
                return authData;
            }
        }
        throw new DataAccessException("Token not found");
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {
        if(!db.removeIf(authData -> authData.authToken().equals(token))) {
            throw new DataAccessException("Username not found");
        }
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
        MemoryAuthDao that = (MemoryAuthDao) o;
        return Objects.equals(db, that.db);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(db);
    }
}
