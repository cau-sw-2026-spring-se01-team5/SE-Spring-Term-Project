package repository.sqlite;

import domain.User;
import enums.user.v1.UserRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import repository.UserRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@Disabled
class SqliteUserRepositoryTest {
    private static Connection connection;

    @BeforeAll
    static void setConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:test.db");
    }

    @Test
    void save() throws Exception {
        UserRepository repository = new SqliteUserRepository(connection);

        User user = new User("username", "password", UserRole.ADMIN);
        Integer userId = repository.save(user, 1);
        assertNotNull(userId);
    }

    @Test
    void load() throws Exception {
        UserRepository repository = new SqliteUserRepository(connection);

        User loadedUser = repository.load(2);

        assertEquals("username", loadedUser.getLoginId());
    }

    @Test
    void byProjectId() throws Exception {
        UserRepository repository = new SqliteUserRepository(connection);

        List<User> users = repository.byProjectId(1);
        assertFalse(users.isEmpty());
    }

    @AfterAll
    static void closeConnection() throws SQLException {
        connection.close();
    }
}