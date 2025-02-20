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

    public void addAuth(AuthData authData) {
        db.add(authData);
    }

    public AuthData getAuth(String username) throws DataAccessException {
        for (AuthData authData : db) {
            if (authData.username().equals(username)) {
                return authData;
            }
        }
        throw new DataAccessException("Username not found");
    }

    public void deleteAuth(String username) throws DataAccessException {
        if(!db.removeIf(authData -> authData.username().equals(username))) {
            throw new DataAccessException("Username not found");
        }
    }

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
