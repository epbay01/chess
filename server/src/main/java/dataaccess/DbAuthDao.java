package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.ResultSet;

public class DbAuthDao implements AuthDao{
    public String queryAll() {
        StringBuilder query = new StringBuilder();
        try (Connection conn = DatabaseManager.getConnection()) {
            ResultSet results = conn.prepareStatement("SELECT * FROM auth").executeQuery();
            while (results.next()) {
                query.append(results.getString("token")).append(",");
                query.append(results.getString("username")).append("\n");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return query.toString();
    }

    @Override
    public void addAuth(AuthData authData) {

    }

    @Override
    public AuthData getAuth(String username) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuthByToken(String token) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {

    }

    @Override
    public void clear() {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("drop table auth").executeUpdate();
            DatabaseManager.createTables();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
