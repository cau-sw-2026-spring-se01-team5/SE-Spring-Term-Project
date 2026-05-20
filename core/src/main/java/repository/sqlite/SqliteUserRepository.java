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
    public Integer save(User user, Integer projectId) throws Exception {
        try{
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO users(login_id, password, user_role) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, user.getLoginId());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole().name());
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (!keys.next()) {
                return null;
            }

            Integer userId = keys.getInt(1);

            PreparedStatement membershipStatement = connection.prepareStatement(
                    "INSERT INTO project_memberships(user_id, project_id) VALUES (?, ?)"
            );
            membershipStatement.setInt(1, userId);
            membershipStatement.setInt(2, projectId);
            membershipStatement.executeUpdate();

            connection.commit();

            return userId;
        } finally {
            connection.setAutoCommit(true);
        }
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
