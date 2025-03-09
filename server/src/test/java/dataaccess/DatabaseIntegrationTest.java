package dataaccess;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import server.Server;

/**
 * Tests for DataManager integration, and more basic functionality, NOT the db DAO.
 * initTest - tests everything works and useMemory is set properly
 * sqlDatabaseTest - tests db exists and can be connected to
 * sqlTableTest - tests tables all exist and have primary keys (they throw sql errors if not)
 */
public class DatabaseIntegrationTest {
    public Server server;
    @BeforeEach
    void setup() {
        server = new Server();
        server.run(8000);
    }

    @Test
    void initTest() {
        Assertions.assertFalse(server.useMemory);
    }

    @Test
    void sqlDatabaseTest() {
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
        try {
            try (var conn = DatabaseManager.getConnection()) {
                conn.prepareStatement("SELECT username FROM user").execute();
                conn.prepareStatement("SELECT token FROM auth").execute();
                conn.prepareStatement("SELECT gameID FROM game").execute();
            }
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @AfterEach
    void shutdown() {
        server.stop();
    }
}
