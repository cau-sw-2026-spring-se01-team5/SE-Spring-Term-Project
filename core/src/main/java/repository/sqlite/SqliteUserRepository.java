package repository.sqlite;

import domain.User;
import enums.user.v1.UserRole;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SqliteUserRepository implements UserRepository {
    @NonNull
    private Connection connection;

    @Override
    public Integer save(User user, Integer projectId) throws Exception {
        try {
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
                        UserRole.valueOf(
                                resultSet.getString("user_role")
                        )
                );
                user.setId(id);

                return user;
            }
        }

        return null;
    }

    @Override
    public User byLoginId(String loginId) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id, login_id, password, user_role FROM users WHERE login_id = ?"
        )) {
            statement.setString(1, loginId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User(
                        resultSet.getString("login_id"),
                        resultSet.getString("password"),
                        UserRole.valueOf(resultSet.getString("user_role"))
                );
                user.setId(resultSet.getInt("id"));
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> byProjectId(Integer projectId) throws Exception {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT id, login_id, password, user_role FROM users\n" +
                        "JOIN project_memberships pm on users.id = pm.user_id\n" +
                        "WHERE pm.project_id = ?"
        );

        statement.setInt(1, projectId);
        ResultSet resultSet = statement.executeQuery();

        List<User> users = new ArrayList<>();
        while (resultSet.next()) {
            User user = new User(
                    resultSet.getString("login_id"),
                    resultSet.getString("password"),
                    UserRole.valueOf(
                            resultSet.getString("user_role")
                    )
            );
            user.setId(resultSet.getInt("id"));

            users.add(user);
        }

        return users;
    }

    @Override
    public void delete(Integer id) throws Exception {
        PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM users WHERE id = ?"
        );
        statement.setInt(1, id);
        statement.executeUpdate();
    }
}
