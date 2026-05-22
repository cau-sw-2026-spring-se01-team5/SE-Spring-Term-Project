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

import static org.junit.jupiter.api.Assertions.*;

class SwingInterfaceJunitTest {

    // 테스트용 임시 폴더
    @TempDir
    Path tempDir;

    // 로그인 + 권한 조회 테스트
    @Test
    void loginAndRoleResolverTest() throws Exception {
        // 임시 db 파일 기반으로 서비스 객체 생성
        AppServices services = concatServices(tempDir.resolve("it-1.db"));

        Auth auth = services.auth();
        RoleResolver roleResolver = services.roleResolver();

        // 기본 admin 계정으로 로그인 시도
        LoginOutput output = auth.login(new LoginInput("admin", "1234"));

        assertTrue(output.success()); // 로그인 성공 여부
        assertNotNull(output.userId()); // userId 존재 여부

        // userName과 role을 제대로 가져오는지
        assertEquals(UserRole.ADMIN, roleResolver.resolveRole(output.userId()));
        assertEquals("admin", roleResolver.resolveLoginId(output.userId()));
    }

    // 프로젝트 인터페이스와 user 인터페이스 동작 여부
    @Test
    void projectAndUserTest() throws Exception {
        // 테스트 db 생성
        AppServices services = concatServices(tempDir.resolve("it-2.db"));

        Auth auth = services.auth();
        Project project = services.project();
        User user = services.user();

        // admin 로그인
        LoginOutput login = auth.login(new LoginInput("admin", "1234"));
        assertTrue(login.success());

        var projectList = project.getProjectList(new GetProjectListInput(login.userId())); // 현재 로그인한 admin기준 프로젝트 조회
        assertTrue(projectList.success()); // 프로젝트 리스트 조회 판단
        assertNotNull(projectList.projectList()); // 목록 존재 여부 판단
        assertTrue(projectList.projectList().stream().anyMatch(p -> p.projectId() == 1)); // 기본 프로젝트 포함 여부

        var userInfo = user.getUserInfo(new GetUserInfoInput(login.userId(), 1)); // admin 유저 정보 조회
        assertTrue(userInfo.success()); // 유저 정보 가져와지는지
        assertEquals("admin", userInfo.loginId()); // 정확한 이름 가져오는지
        assertEquals(UserRole.ADMIN, userInfo.role()); // 정확한 역할 가져오는지
    }

    @Test
    void issueTest() throws Exception {
        // 테스트 db 생성
        AppServices services = concatServices(tempDir.resolve("it-3.db"));

        Auth auth = services.auth();
        User user = services.user();
        Issue issue = services.issue();

        LoginOutput adminLogin = auth.login(new LoginInput("admin", "1234"));
        assertTrue(adminLogin.success());

        // admin 권한으로 tester 계정 생성
        var testerCreate = user.createUser(new CreateUserInput(
                adminLogin.userId(),
                "tester-it",
                "1234",
                UserRole.TESTER,
                1
        ));

        assertTrue(testerCreate.success()); // 생성 성공 여부
        assertNotNull(testerCreate.createdUserId()); // userId 확인

        // tester 계정으로 이슈 등록
        var register = issue.registerIssue(new RegisterIssueInput(
                1,
                "이슈 제목 테스트용",
                "core 연동 확인용 이슈",
                IssuePriority.MAJOR,
                testerCreate.createdUserId()
        ));

        assertTrue(register.success()); // 이슈 등록 성공 여부
        assertNotNull(register.issueId()); // issueId 잘 가져와지는지

        // 등록한 이슈 상세 조회
        var detail = issue.getIssueDetail(new GetIssueDetailInput(register.issueId()));
        assertTrue(detail.success()); // 조회 성공
        assertEquals("이슈 제목 테스트용", detail.issueTitle()); // 제목 일치 여부 검증

        // 테스트 프로젝트1의 전체 이슈 목록 조회
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
        assertTrue(list.success()); // 조회 성공 여부
        assertTrue(list.issues().stream().anyMatch(i -> i.issueId().equals(register.issueId())));
    }

    // 테스트에 필요한 서비스 합치는 메서드
    private static AppServices concatServices(Path dbPath) throws Exception {
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

        return new AppServices(auth, project, user, roleResolver, issue);
    }

    // 테스트 db에 테이블 만드는 메서드
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

    // 디비 스키마 찾기
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

    // 테스트용 기본 데이터 넣기
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
}
