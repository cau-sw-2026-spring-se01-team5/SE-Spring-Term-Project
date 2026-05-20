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


class SqliteUserRepositoryTest {
    private static Connection connection;

    @BeforeAll
    static void setConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:test.db");
    }

    @Test
    void save() throws Exception {
        UserRepository repository = new SqliteUserRepository(connection);

        User user = new User("username", "password", Role.ADMIN);
        Integer userId = repository.save(user, 1);
        assertNotNull(userId);
    }

    @Test
    void load() throws Exception {
        UserRepository repository = new SqliteUserRepository(connection);

        User loadedUser = repository.load(2);

        assertEquals("username", loadedUser.getLoginId());
}

    @AfterAll
    static void closeConnection() throws SQLException {
        connection.close();
    }
}