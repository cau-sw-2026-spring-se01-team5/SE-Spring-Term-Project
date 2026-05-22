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
import session.UserSession;
import user.v1.User;
import user.v1.RoleResolver;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

/* App 진입점. */
/* 필요한 객체들 생성해서 의존성 조립 역할 */
public class Main {

    public static void main(String[] args) {
        // Swing UI 관련 작업을 Event Dispatch Thread에서 실행하도록 함
        // Swing 로직은 전용 스레드에서 처리하는 것이 안전.
        // 따라서 SwingUtilities.invokeLater안에서 UI를 생성함
        SwingUtilities.invokeLater(() -> {
            try {
                Connection connection = DriverManager.getConnection("jdbc:sqlite:test.db");
                initSchema(connection);
                seedDefaultAdmin(connection);

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

                // mock 연결 테스트
                // MockDatabase database = new MockDatabase();
                // Auth auth = new MockAuth(database);
                // Project project = new MockProject(database);
                // User user = new MockUser(database);
                // RoleResolver roleResolver = new MockRoleResolver(database);
                // Issue issue = new MockIssue(database);

                // 현재 로그인한 사용자 상태를 저장하는 세션 객체
                // 로그인 이후 화면과 controller에게 현재 사용자 정보를 공유하기 위해 필요
                UserSession session = new UserSession();

                // 전체 Swing 창 생성
                AppFrame frame = new AppFrame();

                // App 전체 화면 전환 조율
                // 로그인 화면 -> 성공 시 프로젝트 선택 화면 -> 선택 후 메인 화면 -> 로그아웃 시 로그인 화면 등등
                // Main이 직접 화면 전환 처리하지 않고 AppController에게 역할 위임
                AppController controller = new AppController(
                        frame,
                        session,
                        roleResolver,
                        auth,
                        project,
                        user,
                        issue
                );

                controller.start();

                // 창 띄우기
                frame.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "앱 시작 실패: " + e.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
            }
        });
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

    private static void seedDefaultAdmin(Connection connection) throws Exception {
        try (PreparedStatement project = connection.prepareStatement(
                "INSERT OR IGNORE INTO projects(id, name) VALUES (1, ?)")
        ) {
            project.setString(1, "default-project");
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
