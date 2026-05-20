package repository.sqlite;

import domain.Project;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import repository.ProjectRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@RequiredArgsConstructor
public class SqliteProjectRepository implements ProjectRepository {
    @NonNull
    private Connection connection;

    @Override
    public Integer save(Project project) throws Exception {
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO projects(name) VALUES (?)"
        );

        statement.setString(1, project.getName());
        statement.executeUpdate();

        ResultSet keys = statement.getGeneratedKeys();
        if (keys.next()) {
            return keys.getInt(1);
        }

        return null;
    }

    @Override
    public Project load(Integer id) throws Exception {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT name FROM projects WHERE id = ?"
        );

        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            Project project = new Project(resultSet.getString("name"));
            project.setId(id);

            return project;
        }

        return null;
    }
}
