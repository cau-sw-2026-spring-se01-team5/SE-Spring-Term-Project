package backend;

import auth.dto.login.v1.LoginInput;
import auth.v1.Auth;
import controller.auth.v1.AuthImpl;
import controller.issue.v1.IssueImpl;
import controller.project.v1.ProjectImpl;
import controller.user.v1.UserImpl;
import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;
import issue.dto.addIssueComment.v1.AddIssueCommentInput;
import issue.dto.assignIssue.v1.AssignIssueInput;
import issue.dto.changeIssueStatus.v1.ChangeIssueStatusInput;
import issue.dto.getIssueDetail.v1.GetIssueDetailInput;
import issue.dto.getIssueDetail.v1.GetIssueDetailOutput;
import issue.dto.getIssueList.v1.GetIssueListInput;
import issue.dto.getIssueList.v1.IssueSummaryOutput;
import issue.dto.recommendAssignee.v1.RecommendAssigneeInput;
import issue.dto.registerIssue.v1.RegisterIssueInput;
import issue.v1.Issue;
import project.dto.createProject.v1.CreateProjectInput;
import project.dto.deleteProject.v1.DeleteProjectInput;
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
import statistics.dto.countByStatus.v1.CountByStatusInput;
import statistics.dto.getDailyIssueCounts.v1.GetDailyIssueCountsInput;
import statistics.v1.Statistics;
import user.dto.createUser.v1.CreateUserInput;
import user.dto.deleteUser.v1.DeleteUserInput;
import user.dto.getProjectUserList.v1.GetProjectUserListInput;
import user.dto.getUserInfo.v1.GetUserInfoInput;
import user.v1.User;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 * JavaFX 화면을 실제 core backend에 연결하는 어댑터이다.
 *
 * JavaFX View와 Controller는 JavaFxBackend만 알고 있다.
 * 이 클래스는 그 요청을 core 모듈의 AuthImpl, ProjectImpl, UserImpl, IssueImpl 호출로 변환한다.
 *
 * 설계 의도:
 * - JavaFX 화면 코드는 mock인지 실제 backend인지 몰라도 된다.
 * - mock 방식은 MockJavaFxBackend에 남기고, 실제 연동은 이 클래스에 분리한다.
 * - BackendFactory에서 구현체만 바꾸면 두 방식을 전환할 수 있다.
 */
public class RealJavaFxBackend implements JavaFxBackend {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String DATABASE_URL = "jdbc:sqlite:test.db";

    private final Auth auth;
    private final Project project;
    private final User user;
    private final Issue issue;
    private final Statistics statistics;
    private final UserRepository userRepository;

    private RealJavaFxBackend(
            Auth auth,
            Project project,
            User user,
            Issue issue,
            Statistics statistics,
            UserRepository userRepository
    ) {
        this.auth = auth;
        this.project = project;
        this.user = user;
        this.issue = issue;
        this.statistics = statistics;
        this.userRepository = userRepository;
    }

    /*
 * JavaFX 화면을 실제 core backend에 연결하는 어댑터이다.
 *
 * JavaFX View와 Controller는 JavaFxBackend만 알고 있다.
 * 이 클래스는 그 요청을 core 모듈의 AuthImpl, ProjectImpl, UserImpl, IssueImpl 호출로 변환한다.
 *
 * 설계 의도:
 * - JavaFX 화면 코드는 mock인지 실제 backend인지 몰라도 된다.
 * - mock 방식은 MockJavaFxBackend에 남기고, 실제 연동은 이 클래스에 분리한다.
 * - BackendFactory에서 구현체만 바꾸면 두 방식을 전환할 수 있다.
 */
    public static RealJavaFxBackend create() {
        try {
            Connection connection = DriverManager.getConnection(DATABASE_URL);
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
            Issue issue = new IssueImpl(userRepository, issueRepository, commentRepository, recommendationRepository);
            Statistics statistics = new controller.statistics.v1.StatisticsImpl(issueRepository);

            return new RealJavaFxBackend(auth, project, user, issue, statistics, userRepository);
        } catch (Exception e) {
            throw new IllegalStateException("실제 backend 초기화 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<LoginUser> login(String loginId, String password) {
        var output = auth.login(new LoginInput(loginId, password));
        if (!output.success()) {
            return Optional.empty();
        }

        var info = user.getUserInfo(new GetUserInfoInput(output.userId(), null));
        if (!info.success()) {
            return Optional.empty();
        }
        return Optional.of(new LoginUser(info.loginId(), info.role()));
    }

    @Override
    public int countByStatus(Integer projectId, String status) {
        return (int) statistics.countByStatus(new CountByStatusInput(projectId, IssueStatus.valueOf(status))).count();
    }

    @Override
    public List<ProjectItem> projects() {
        var output = project.getProjectList(new GetProjectListInput(adminUserId()));
        if (!output.success() || output.projectList() == null) {
            return List.of();
        }
        return output.projectList().stream()
                .map(item -> new ProjectItem(item.projectId(), item.title(), ""))
                .toList();
    }

    @Override
    public List<ProjectItem> projectsForUser(String loginId, UserRole role) {
        if (role == UserRole.ADMIN) {
            return projects();
        }

        Integer userId = userIdOf(loginId);
        if (userId == null) {
            return List.of();
        }

        return projects().stream()
                .filter(projectItem -> usersForProject(projectItem.id()).stream()
                        .anyMatch(userItem -> Objects.equals(userItem.id(), userId)))
                .toList();
    }

    @Override
    public List<UserItem> usersForProject(int projectId) {
        var output = user.getProjectUserList(new GetProjectUserListInput(projectId));
        if (!output.success() || output.userList() == null) {
            return List.of();
        }

        return output.userList().stream()
                .map(item -> new UserItem(
                        item.userId(),
                        item.loginId(),
                        "",
                        item.role(),
                        item.projectId()
                ))
                .toList();
    }

    @Override
    public ProjectItem addProject(String name, String description) {
        var output = project.createProject(new CreateProjectInput(name));
        return new ProjectItem(output.projectId(), name, description == null ? "" : description);
    }

    @Override
    public void deleteProject(int projectId) {
        project.deleteProject(new DeleteProjectInput(adminUserId(), projectId));
    }

    @Override
    public boolean hasLoginId(String loginId) {
        return userIdOf(loginId) != null;
    }

    @Override
    public void addUser(String loginId, String password, UserRole role, int projectId) {
        user.createUser(new CreateUserInput(adminUserId(), loginId, password, role, projectId));
    }

    @Override
    public void deleteUser(String loginId) {
        Integer targetUserId = userIdOf(loginId);
        if (targetUserId != null) {
            user.deleteUser(new DeleteUserInput(adminUserId(), targetUserId, null));
        }
    }

    @Override
    public List<String> developerLoginIds() {
        return loginIdsByRole(UserRole.DEV);
    }

    @Override
    public List<String> developerLoginIdsForProject(int projectId) {
        return usersForProject(projectId).stream()
                .filter(userItem -> userItem.role() == UserRole.DEV)
                .map(UserItem::loginId)
                .toList();
    }

    @Override
    public List<String> testerLoginIds() {
        return loginIdsByRole(UserRole.TESTER);
    }

    @Override
    public List<IssueItem> issuesForRole(String loginId, UserRole role) {
        Integer currentUserId = userIdOf(loginId);
        if (currentUserId == null) {
            return List.of();
        }

        return projectsForUser(loginId, role).stream()
                .flatMap(projectItem -> issue.getIssueList(new GetIssueListInput(
                        projectItem.id(),
                        currentUserId,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                )).issues().stream())
                .map(this::toIssueItem)
                .toList();
    }

    @Override
    public String registerIssue(int projectId, String title, String description, String reporter, String priority) {
        var output = issue.registerIssue(new RegisterIssueInput(
                projectId,
                title,
                description,
                IssuePriority.valueOf(priority),
                userIdOf(reporter)
        ));
        return output.success() ? null : output.message();
    }

    @Override
    public void assignIssue(int issueId, String assignee, String writer, String comment) {
        issue.assignIssue(new AssignIssueInput(issueId, userIdOf(writer), userIdOf(assignee), comment));
        addComment(issueId, writer, comment);
    }

    @Override
    public void markFixed(int issueId, String fixer, String comment) {
        issue.changeIssueStatus(new ChangeIssueStatusInput(issueId, userIdOf(fixer), IssueStatus.FIXED));
        addComment(issueId, fixer, comment);
    }

    @Override
    public void resolveIssue(int issueId, String writer, String comment) {
        issue.changeIssueStatus(new ChangeIssueStatusInput(issueId, userIdOf(writer), IssueStatus.RESOLVED));
        addComment(issueId, writer, comment);
    }

    @Override
    public String reopenIssue(int issueId, String writer, String comment) {
        var output = issue.changeIssueStatus(new ChangeIssueStatusInput(issueId, userIdOf(writer), IssueStatus.REOPENED));
        if (!output.success()) {
            return output.message();
        }
        addComment(issueId, writer, comment);
        return null;
    }

    @Override
    public void closeIssue(int issueId, String writer, String comment) {
        issue.changeIssueStatus(new ChangeIssueStatusInput(issueId, userIdOf(writer), IssueStatus.CLOSED));
        addComment(issueId, writer, comment);
    }

    @Override
    public void addComment(int issueId, String writer, String comment) {
        issue.addIssueComment(new AddIssueCommentInput(issueId, userIdOf(writer), comment));
    }

    @Override
    public List<String> recommendAssignees(IssueItem issueItem) {
        var output = issue.recommendAssignees(new RecommendAssigneeInput(issueItem.id(), issueItem.projectId()));
        if (!output.success() || output.candidates() == null) {
            return List.of();
        }
        return output.candidates().stream()
                .map(candidate -> candidate.userId())
                .toList();
    }

    @Override
    public Map<String, Long> dailyIssueCounts(Integer projectId) {
        return statistics.getDailyIssueCounts(new GetDailyIssueCountsInput(projectId)).counts().stream()
                .collect(Collectors.toMap(
                        count -> count.date(),
                        count -> count.count(),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }

    private IssueItem toIssueItem(IssueSummaryOutput summary) {
        GetIssueDetailOutput detail = issue.getIssueDetail(new GetIssueDetailInput(summary.issueId()));
        List<CommentItem> comments = detail.comments().stream()
                .map(comment -> new CommentItem(
                        comment.authorUserId(),
                        comment.comment(),
                        formatDate(comment.createdAt())
                ))
                .toList();

        return new IssueItem(
                summary.issueId(),
                summary.projectId(),
                summary.issueTitle(),
                emptyIfNull(detail.issueDescription()),
                emptyIfNull(summary.reporterUserId()),
                formatDate(summary.reportedDate()),
                summary.priority().name(),
                summary.status().name(),
                emptyIfNull(summary.assigneeUserId()),
                emptyIfNull(summary.fixerUserId()),
                comments
        );
    }

    private List<String> loginIdsByRole(UserRole role) {
        return projects().stream()
                .flatMap(projectItem -> usersForProject(projectItem.id()).stream())
                .filter(userItem -> userItem.role() == role)
                .map(UserItem::loginId)
                .distinct()
                .toList();
    }

    private Integer userIdOf(String loginId) {
        try {
            domain.User found = userRepository.byLoginId(loginId);
            return found == null ? null : found.getId();
        } catch (Exception e) {
            return null;
        }
    }

    private Integer adminUserId() {
        return userIdOf("admin");
    }

    private String emptyIfNull(String value) {
        return value == null ? "" : value;
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(DATE_FORMAT);
    }

    private static void initSchema(Connection connection) throws Exception {
        String schema = Files.readString(resolveSchemaPath());
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

    private static Path resolveSchemaPath() throws Exception {
        Path[] candidates = new Path[]{
                Paths.get("core", "src", "main", "java", "repository", "sqlite", "schema.sql"),
                Paths.get("..", "core", "src", "main", "java", "repository", "sqlite", "schema.sql"),
                Paths.get(System.getProperty("user.dir"), "core", "src", "main", "java", "repository", "sqlite", "schema.sql"),
                Paths.get(System.getProperty("user.dir"), "..", "core", "src", "main", "java", "repository", "sqlite", "schema.sql")
        };

        for (Path candidate : candidates) {
            Path normalized = candidate.normalize();
            if (Files.exists(normalized)) {
                return normalized;
            }
        }

        throw new java.nio.file.NoSuchFileException("core/src/main/java/repository/sqlite/schema.sql");
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
