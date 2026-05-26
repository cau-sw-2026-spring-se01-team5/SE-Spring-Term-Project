package app;

import auth.dto.login.v1.LoginInput;
import auth.dto.login.v1.LoginOutput;
import auth.v1.Auth;
import controller.auth.v1.AuthImpl;
import controller.issue.v1.IssueImpl;
import controller.project.v1.ProjectImpl;
import controller.user.v1.RoleResolverImpl;
import controller.user.v1.UserImpl;
import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;
import issue.dto.getIssueDetail.v1.GetIssueDetailInput;
import issue.dto.getIssueList.v1.GetIssueListInput;
import issue.dto.registerIssue.v1.RegisterIssueInput;
import issue.v1.Issue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import project.dto.getProjectList.v1.GetProjectListInput;
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
import user.dto.createUser.v1.CreateUserInput;
import user.dto.getUserInfo.v1.GetUserInfoInput;
import user.v1.RoleResolver;
import user.v1.User;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SwingInterfaceJunitTest {

    @TempDir
    Path tempDir;

    @Test
    void loginAndRoleResolverTest() throws Exception {
        try (TestServices testServices = concatServices(tempDir.resolve("it-1.db"))) {
            AppServices services = testServices.services();

            Auth auth = services.auth();
            RoleResolver roleResolver = services.roleResolver();

            LoginOutput output = auth.login(new LoginInput("admin", "1234"));

            assertTrue(output.success());
            assertNotNull(output.userId());
            assertEquals(UserRole.ADMIN, roleResolver.resolveRole(output.userId()));
            assertEquals("admin", roleResolver.resolveLoginId(output.userId()));
        }
    }

    @Test
    void projectAndUserTest() throws Exception {
        try (TestServices testServices = concatServices(tempDir.resolve("it-2.db"))) {
            AppServices services = testServices.services();

            Auth auth = services.auth();
            Project project = services.project();
            User user = services.user();

            LoginOutput login = auth.login(new LoginInput("admin", "1234"));
            assertTrue(login.success());

            var projectList = project.getProjectList(new GetProjectListInput(login.userId()));
            assertTrue(projectList.success());
            assertNotNull(projectList.projectList());
            assertTrue(projectList.projectList().stream().anyMatch(p -> p.projectId() == 1));

            var userInfo = user.getUserInfo(new GetUserInfoInput(login.userId(), 1));
            assertTrue(userInfo.success());
            assertEquals("admin", userInfo.loginId());
            assertEquals(UserRole.ADMIN, userInfo.role());
        }
    }

    @Test
    void issueTest() throws Exception {
        try (TestServices testServices = concatServices(tempDir.resolve("it-3.db"))) {
            AppServices services = testServices.services();

            Auth auth = services.auth();
            User user = services.user();
            Issue issue = services.issue();

            LoginOutput adminLogin = auth.login(new LoginInput("admin", "1234"));
            assertTrue(adminLogin.success());

            var testerCreate = user.createUser(new CreateUserInput(
                    adminLogin.userId(),
                    "tester-it",
                    "1234",
                    UserRole.TESTER,
                    1
            ));

            assertTrue(testerCreate.success());
            assertNotNull(testerCreate.createdUserId());

            var register = issue.registerIssue(new RegisterIssueInput(
                    1,
                    "이슈 제목 테스트용",
                    "core 연동 확인용 이슈",
                    IssuePriority.MAJOR,
                    testerCreate.createdUserId()
            ));

            assertTrue(register.success());
            assertNotNull(register.issueId());

            var detail = issue.getIssueDetail(new GetIssueDetailInput(register.issueId()));
            assertTrue(detail.success());
            assertEquals("이슈 제목 테스트용", detail.issueTitle());

            var list = issue.getIssueList(new GetIssueListInput(
                    1,
                    adminLogin.userId(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            ));
            assertTrue(list.success());
            assertTrue(list.issues().stream().anyMatch(i -> i.issueId().equals(register.issueId())));
        }
    }

    @Test
    void statisticsTest() throws Exception {
        try (TestServices testServices = concatServices(tempDir.resolve("it-4.db"))) {
            AppServices services = testServices.services();

            Auth auth = services.auth();
            User user = services.user();
            Issue issue = services.issue();

            LoginOutput adminLogin = auth.login(new LoginInput("admin", "1234"));
            assertTrue(adminLogin.success());

            var testerCreate = user.createUser(new CreateUserInput(
                    adminLogin.userId(),
                    "tester-stat",
                    "1234",
                    UserRole.TESTER,
                    1
            ));
            assertTrue(testerCreate.success());

            var register = issue.registerIssue(new RegisterIssueInput(
                    1,
                    "통계 테스트 이슈",
                    "상태별 일일 통계 검증용",
                    IssuePriority.MAJOR,
                    testerCreate.createdUserId()
            ));
            assertTrue(register.success());

            var list = issue.getIssueList(new GetIssueListInput(
                    1,
                    adminLogin.userId(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            ));
            assertTrue(list.success());
            assertNotNull(list.issues());
            assertFalse(list.issues().isEmpty());

            long newCount = list.issues().stream()
                    .filter(summary -> summary.status() == IssueStatus.NEW)
                    .count();
            assertTrue(newCount >= 1);

            Map<String, Long> daily = list.issues().stream()
                    .collect(Collectors.groupingBy(
                            summary -> summary.reportedDate().toLocalDate().toString(),
                            Collectors.counting()
                    ));
            assertFalse(daily.isEmpty());
        }
    }

    private static TestServices concatServices(Path dbPath) throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath.toAbsolutePath());
        initSchema(connection);
        addDefaultData(connection);

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

        return new TestServices(new AppServices(auth, project, user, roleResolver, issue), connection);
    }

    private static void initSchema(Connection connection) throws Exception {
        Path schemaPath = resolveSchemaPath();
        String schema = Files.readString(schemaPath);

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

    private static void addDefaultData(Connection connection) throws Exception {
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

        try (PreparedStatement membership = connection.prepareStatement(
                "INSERT OR IGNORE INTO project_memberships(user_id, project_id) " +
                        "VALUES ((SELECT id FROM users WHERE login_id = ?), ?)")
        ) {
            membership.setString(1, "admin");
            membership.setInt(2, 1);
            membership.executeUpdate();
        }
    }

    private record TestServices(AppServices services, Connection connection) implements AutoCloseable {
        @Override
        public void close() throws Exception {
            connection.close();
        }
    }
}
