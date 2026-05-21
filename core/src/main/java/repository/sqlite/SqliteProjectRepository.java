package repository.sqlite;

import domain.Project;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import repository.ProjectRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SqliteProjectRepository implements ProjectRepository {
    @NonNull
    private Connection connection;

    @Override
    public Integer save(Project project) throws Exception {
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO projects(name) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS
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

    @Override
    public List<Project> list() throws Exception {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT id, name FROM projects"
        );
        ResultSet resultSet = statement.executeQuery();

        List<Project> projects = new ArrayList<>();
        while (resultSet.next()) {
            Project project = new Project(resultSet.getString("name"));
            project.setId(resultSet.getInt("id"));
            projects.add(project);
        }
        return projects;
    }

    @Override
    public void delete(Integer id) throws Exception {
        PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM projects WHERE id = ?"
        );
        statement.setInt(1, id);
        statement.executeUpdate();
    }

    @Override
    public void update(Project project) throws Exception {
        PreparedStatement statement = connection.prepareStatement(
                "UPDATE projects SET name = ? WHERE id = ?"
        );
        statement.setString(1, project.getName());
        statement.setInt(2, project.getId());

        statement.executeUpdate();
    }
}
