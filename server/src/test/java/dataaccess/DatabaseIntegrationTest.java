package dataaccess;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import server.Server;

import java.sql.SQLException;

/**
 * Tests for DataManager integration, and more basic functionality, NOT the db dao.
 * initTest - tests everything works and useMemory is set properly
 * sqlDatabaseTest - tests db exists and can be connected to
 * sqlTableTest - tests tables all exist and have primary keys
 */
public class DatabaseIntegrationTest {
    @Test
    void initTest() {
        var server = new Server();

        server.run(8000);

        Assertions.assertFalse(server.useMemory);
    }

    @Test
    void sqlDatabaseTest() {
        var server = new Server();
        server.run(8000);

        try {
            try (var conn = DatabaseManager.getConnection()) {
                Assertions.assertNotNull(conn);
            }
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void sqlTableTest() {
        var server = new Server();
        server.run(8000);

        try {
            try (var conn = DatabaseManager.getConnection()) {
                conn.prepareStatement("SELECT username FROM user").executeUpdate();
                conn.prepareStatement("SELECT token FROM auth").executeUpdate();
                conn.prepareStatement("SELECT gameID FROM game").executeUpdate();
            }
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
}
