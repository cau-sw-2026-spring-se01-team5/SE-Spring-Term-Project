package repository.sqlite;

import domain.Role;
import domain.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@RequiredArgsConstructor
public class SqliteUserRepository implements UserRepository {
    @NonNull
    private Connection connection;

    @Override
    public Integer save(User user) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO users(login_id, password, user_role) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, user.getLoginId());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole().name());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        return null;
    }

    @Override
    public User load(Integer id) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT login_id, password, user_role FROM users WHERE id = ?"
        )) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User(
                        resultSet.getString("login_id"),
                        resultSet.getString("password"),
                        Role.valueOf(
                                resultSet.getString("user_role")
                        )
                );
                user.setId(id);

                return user;
            }
        }

        return null;
    }
}
