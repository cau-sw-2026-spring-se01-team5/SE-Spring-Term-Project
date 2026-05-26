package app;

import auth.v1.Auth;
import controller.auth.v1.AuthImpl;
import controller.issue.v1.IssueImpl;
import controller.project.v1.ProjectImpl;
import controller.statistics.v1.StatisticsImpl;
import controller.user.v1.RoleResolverImpl;
import controller.user.v1.UserImpl;
import issue.v1.Issue;
import project.v1.Project;
import repository.CommentRepository;
import repository.IssueRepository;
import repository.ProjectRepository;
import repository.RecommendationRepository;
import repository.UserRepository;
import repository.lucene.LuceneRecommendationRepository;
import repository.sqlite.SqliteCommentRepository;
import repository.sqlite.SqliteIssueRepository;
import repository.sqlite.SqliteProjectRepository;
import repository.sqlite.SqliteUserRepository;
import statistics.v1.Statistics;
import user.v1.RoleResolver;
import user.v1.User;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public final class JavaFxServicesFactory {

    private JavaFxServicesFactory() {
    }

    public static JavaFxServices create() {
        try {
            String dbPath = System.getProperty("javafx.db.path", "test.db");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            initSchema(connection);

            UserRepository userRepository = new SqliteUserRepository(connection);
            ProjectRepository projectRepository = new SqliteProjectRepository(connection);
            IssueRepository issueRepository = new SqliteIssueRepository(connection);
            CommentRepository commentRepository = new SqliteCommentRepository(connection);
            RecommendationRepository recommendationRepository = new LuceneRecommendationRepository(issueRepository);

            Auth auth = new AuthImpl(userRepository);
            Project project = new ProjectImpl(userRepository, projectRepository);
            User user = new UserImpl(userRepository);
            RoleResolver roleResolver = new RoleResolverImpl(userRepository);
            Issue issue = new IssueImpl(userRepository, issueRepository, commentRepository, recommendationRepository);
            Statistics statistics = new StatisticsImpl(issueRepository);

            return new JavaFxServices(auth, project, user, roleResolver, issue, statistics);
        } catch (Exception exception) {
            throw new IllegalStateException("실제 service 초기화 실패: " + exception.getMessage(), exception);
        }
    }

    private static void initSchema(Connection connection) throws Exception {
        Path schemaPath = resolveSchemaPath();
        String schema = Files.readString(schemaPath);
        for (String raw : schema.split(";")) {
            String sql = raw.trim();
            if (sql.isEmpty()) {
                continue;
            }
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        }
    }

    private static Path resolveSchemaPath() {
        Path direct = Path.of("core/src/main/java/repository/sqlite/schema.sql");
        if (Files.exists(direct)) {
            return direct;
        }

        Path parent = Path.of("../core/src/main/java/repository/sqlite/schema.sql");
        if (Files.exists(parent)) {
            return parent;
        }

        throw new IllegalStateException("schema.sql 경로를 찾을 수 없습니다.");
    }
}
