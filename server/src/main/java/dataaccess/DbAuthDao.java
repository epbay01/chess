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
    public void addAuth(AuthData authData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String str = "INSERT INTO auth VALUES ('" + authData.authToken() + "', '"
                    + authData.username() + "')";
            conn.prepareStatement(str).executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            ResultSet resultSet = conn.prepareStatement("SELECT * FROM auth WHERE username='" + username + "'")
                    .executeQuery();
            if (resultSet.next()) {
                return new AuthData(resultSet.getString("token"),
                        resultSet.getString("username"));
            } else {
                throw new DataAccessException("Token not found");
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public AuthData getAuthByToken(String token) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            ResultSet resultSet = conn.prepareStatement("SELECT * FROM auth WHERE token='" + token + "'")
                    .executeQuery();
            if (resultSet.next()) {
                return new AuthData(resultSet.getString("token"),
                        resultSet.getString("username"));
            } else {
                throw new DataAccessException("Token not found");
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("DELETE FROM auth WHERE token='" + token + "'").executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("DELETE FROM auth").executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
