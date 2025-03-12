package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DbUserDao implements UserDao {
    @Override
    public void createUser(UserData userData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String pass = BCrypt.hashpw(userData.password(), BCrypt.gensalt());

            String str = "INSERT INTO `user` VALUES ('" + userData.username() + "', '" +
                    pass + "', '" +
                    userData.email() + "')";
            conn.prepareStatement(str).executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData user = null;

        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(
                    "SELECT * FROM chess.`user` WHERE username='" + username + "'");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = new UserData(resultSet.getString("username"),
                    resultSet.getString("password"),
                    resultSet.getString("email"));
            }
            statement.close();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }

        if (user == null) {
            throw new DataAccessException("User does not exist");
        } else {
            return user;
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("DELETE FROM user").executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    // for testing
    public String queryAll() {
        StringBuilder query = new StringBuilder();
        try (Connection conn = DatabaseManager.getConnection()) {
            ResultSet results = conn.prepareStatement("SELECT * FROM `user`").executeQuery();
            while (results.next()) {
                query.append(results.getString("username")).append(",");
                query.append(results.getString("password")).append(",");
                query.append(results.getString("email")).append("\n");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return query.toString();
    }
}
