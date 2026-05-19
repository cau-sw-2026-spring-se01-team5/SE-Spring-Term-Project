package repository.sqlite;

import domain.Role;
import domain.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import repository.UserRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;


@Disabled
class SqliteUserRepositoryTest {
    private static Connection connection;

    @BeforeAll
    static void setConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:test.db");

        PreparedStatement statement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS users (\n" +
                        "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "  login_id TEXT NOT NULL UNIQUE,\n" +
                        "  password TEXT NOT NULL,\n" +
                        "  user_role TEXT NOT NULL\n" +
                        ");"
        );

        statement.execute();
    }

    @Test
    void save() throws Exception {
        UserRepository repository = new SqliteUserRepository(connection);

        User user = new User("username", "password", Role.ADMIN);
        Integer userId = repository.save(user);
        assertNotNull(userId);
    }

    @Test
    void saveAndLoad() throws Exception {
        UserRepository repository = new SqliteUserRepository(connection);

        User user = new User("username", "password", Role.ADMIN);
        Integer userId = repository.save(user);

        User loadedUser = repository.load(userId);

        assertEquals(user.getLoginId(), loadedUser.getLoginId());
    }

    @AfterAll
    static void closeConnection() throws SQLException {
        connection.close();
    }
}