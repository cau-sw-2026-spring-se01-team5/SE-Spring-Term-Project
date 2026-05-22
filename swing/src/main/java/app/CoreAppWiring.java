package app;

import auth.v1.Auth;
import controller.auth.v1.AuthImpl;
import controller.issue.v1.IssueImpl;
import controller.project.v1.ProjectImpl;
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
import user.v1.RoleResolver;
import user.v1.User;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class CoreAppWiring implements AppWiring {

    @Override
    public AppServices wire() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:test.db");
        initSchema(connection);
        makeDefaultAdmin(connection);

        UserRepository userRepository = new SqliteUserRepository(connection);
        ProjectRepository projectRepository = new SqliteProjectRepository(connection);
        IssueRepository issueRepository = new SqliteIssueRepository(connection);
        CommentRepository commentRepository = new SqliteCommentRepository(connection);
        RecommendationRepository recommendationRepository = new LuceneRecommendationRepository(issueRepository);

        Auth auth = new AuthImpl(userRepository);
        Project project = new ProjectImpl(userRepository, projectRepository);
        User user = new UserImpl(userRepository);
        RoleResolver roleResolver = new RoleResolverImpl(userRepository);
        Issue issue = new IssueImpl(
                userRepository,
                issueRepository,
                commentRepository,
                recommendationRepository
        );

        return new AppServices(auth, project, user, roleResolver, issue);
    }

    private static void initSchema(Connection connection) throws Exception {
        String schema = Files.readString(Path.of("core/src/main/java/repository/sqlite/schema.sql"));

        String[] statements = schema.split(";");
        for (String raw : statements) {
            String sql = raw.trim();
            if (sql.isEmpty()) {
                continue;
            }
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        }
    }

    // 처음 앱 실행시 초기 프로젝트와 admin 생성
    private static void makeDefaultAdmin(Connection connection) throws Exception {
        try (PreparedStatement project = connection.prepareStatement(
                "INSERT OR IGNORE INTO projects(id, name) VALUES (1, ?)")
        ) {
            project.setString(1, "project1");
            project.executeUpdate();
        }

        try (PreparedStatement admin = connection.prepareStatement(
                "INSERT OR IGNORE INTO users(login_id, password, user_role) VALUES (?, ?, ?)")
        ) {
            admin.setString(1, "admin");
            admin.setString(2, "1234");
            admin.setString(3, "ADMIN");
            admin.executeUpdate();
        }

        // 디폴트로 만든 admin을 projcet1에 할당
        try (PreparedStatement membership = connection.prepareStatement(
                "INSERT OR IGNORE INTO project_memberships(user_id, project_id) " +
                        "VALUES ((SELECT id FROM users WHERE login_id = ?), ?)")
        ) {
            membership.setString(1, "admin");
            membership.setInt(2, 1);
            membership.executeUpdate();
        }
    }
}
