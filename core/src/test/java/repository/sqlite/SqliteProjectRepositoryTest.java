package repository.sqlite;

import domain.Project;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import repository.ProjectRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;


@Disabled
class SqliteProjectRepositoryTest {
    @Test
    void save() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:test.db");

        ProjectRepository repository = new SqliteProjectRepository(connection);

        Project project = new Project("project2");
        Integer id = repository.save(project);
        assertNotNull(id);
    }

    @Test
    void load() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:test.db");

        ProjectRepository repository = new SqliteProjectRepository(connection);

        Project project = repository.load(1);
        assertNotNull(project);
    }
    @Test
    void delete() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:test.db");

        ProjectRepository repository = new SqliteProjectRepository(connection);

        repository.delete(2);
        Project project = repository.load(2);
        assertNull(project);
    }
}