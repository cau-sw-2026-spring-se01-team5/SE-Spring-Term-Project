package backend;

import backend.JavaFxBackend.CommentItem;
import backend.JavaFxBackend.IssueItem;
import backend.JavaFxBackend.LoginUser;
import backend.JavaFxBackend.ProjectItem;
import backend.JavaFxBackend.UserItem;
import auth.dto.login.v1.LoginInput;
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
import mock.MockAuth;
import mock.MockDatabase;
import mock.MockIssue;
import mock.MockProject;
import mock.MockUser;
import mock.model.MockUserData;
import project.dto.createProject.v1.CreateProjectInput;
import project.dto.deleteProject.v1.DeleteProjectInput;
import user.dto.createUser.v1.CreateUserInput;
import user.dto.deleteUser.v1.DeleteUserInput;
import user.dto.getProjectUserList.v1.GetProjectUserListInput;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 * JavaFX 화면 인터페이스와 Swing에서 가져온 mock controller 구현체 사이를 연결하는 어댑터이다.
 *
 * 설계 의도:
 * - JavaFX 화면은 JavaFxBackend라는 화면용 인터페이스만 사용한다.
 * - 실제 데이터 처리는 MockAuth, MockProject, MockUser, MockIssue에 위임한다.
 * - 따라서 화면 코드는 mock의 내부 자료구조를 직접 알 필요가 없고,
 *   나중에 실제 backend 구현체로 교체할 때도 이 어댑터 위치만 바꾸면 된다.
 */
public class MockJavaFxBackend implements JavaFxBackend {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final MockDatabase database = new MockDatabase();
    private final MockAuth auth = new MockAuth(database);
    private final MockProject project = new MockProject(database);
    private final MockUser user = new MockUser(database);
    private final MockIssue issue = new MockIssue(database);

    @Override
    public Optional<LoginUser> login(String loginId, String password) {
        /*
         * JavaFX LoginController는 화면용 LoginUser만 필요로 한다.
         * 실제 로그인 검증은 Swing mock과 같은 MockAuth에 맡기고,
         * 성공하면 userId로 MockDatabase에서 역할과 로그인 ID를 다시 조회한다.
         */
        var output = auth.login(new LoginInput(loginId, password));
        if (!output.success()) {
            return Optional.empty();
        }

        MockUserData data = database.users().get(output.userId());
        if (data == null) {
            return Optional.empty();
        }

        return Optional.of(new LoginUser(data.loginId(), data.role()));
    }

    @Override
    public int countByStatus(String status) {
        IssueStatus target = IssueStatus.valueOf(status);
        return (int) database.issues().values().stream()
                .filter(issueData -> issueData.status() == target)
                .count();
    }

    @Override
    public List<ProjectItem> projects() {
        return database.projects().values().stream()
                .map(data -> new ProjectItem(data.projectId(), data.title(), "mock 프로젝트"))
                .toList();
    }

    @Override
    public List<ProjectItem> projectsForUser(String loginId, UserRole role) {
        /*
         * admin은 전체 프로젝트에 접근할 수 있고,
         * PL/DEV/TESTER는 자신이 소속된 프로젝트만 접근할 수 있도록 화면용 목록을 제한한다.
         */
        if (role == UserRole.ADMIN) {
            return projects();
        }

        return findUser(loginId)
                .flatMap(userData -> projects().stream()
                        .filter(projectItem -> Objects.equals(projectItem.id(), userData.projectId()))
                        .findFirst())
                .map(List::of)
                .orElseGet(List::of);
    }

    @Override
    public List<UserItem> usersForProject(int projectId) {
        return database.users().values().stream()
                .filter(data -> Objects.equals(data.projectId(), projectId))
                .map(this::toUserItem)
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
        return database.users().values().stream()
                .anyMatch(data -> data.loginId().equals(loginId));
    }

    @Override
    public void addUser(String loginId, String password, UserRole role, int projectId) {
        user.createUser(new CreateUserInput(adminUserId(), loginId, password, role, projectId));
    }

    @Override
    public void deleteUser(String loginId) {
        findUser(loginId).ifPresent(data ->
                user.deleteUser(new DeleteUserInput(adminUserId(), data.userId(), data.projectId())));
    }

    @Override
    public List<String> developerLoginIds() {
        return loginIdsByRole(UserRole.DEV);
    }

    @Override
    public List<String> developerLoginIdsForProject(int projectId) {
        return database.users().values().stream()
                .filter(data -> data.role() == UserRole.DEV)
                .filter(data -> Objects.equals(data.projectId(), projectId))
                .map(MockUserData::loginId)
                .toList();
    }

    @Override
    public List<String> testerLoginIds() {
        return loginIdsByRole(UserRole.TESTER);
    }

    @Override
    public List<IssueItem> issuesForRole(String loginId, UserRole role) {
        /*
         * 역할별 기본 조회 범위를 정한다.
         * DEV는 자신에게 배정된 이슈, TESTER는 자신이 등록한 이슈 중심으로 보여주고,
         * PL과 admin은 프로젝트 범위의 이슈를 확인할 수 있다.
         */
        List<Integer> projectIds = projectsForUser(loginId, role).stream()
                .map(ProjectItem::id)
                .toList();

        return projectIds.stream()
                .flatMap(projectId -> issue.getIssueList(new GetIssueListInput(
                        projectId,
                        userIdOf(loginId),
                        role == UserRole.DEV ? userIdOf(loginId) : null,
                        role == UserRole.TESTER ? userIdOf(loginId) : null,
                        null,
                        null,
                        null,
                        null
                )).issues().stream())
                .map(this::toIssueItem)
                .toList();
    }

    @Override
    public void registerIssue(int projectId, String title, String description, String reporter, String priority) {
        issue.registerIssue(new issue.dto.registerIssue.v1.RegisterIssueInput(
                projectId,
                title,
                description,
                IssuePriority.valueOf(priority),
                userIdOf(reporter)
        ));
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
    public void reopenIssue(int issueId, String writer, String comment) {
        issue.changeIssueStatus(new ChangeIssueStatusInput(issueId, adminUserId(), IssueStatus.REOPENED));
        addComment(issueId, writer, comment);
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
        return issue.recommendAssignees(new RecommendAssigneeInput(issueItem.id(), issueItem.projectId()))
                .candidates()
                .stream()
                .sorted(Comparator.comparing(candidate -> candidate.rank()))
                .map(candidate -> candidate.userId())
                .toList();
    }

    @Override
    public Map<String, Long> dailyIssueCounts() {
        return database.issues().values().stream()
                .collect(Collectors.groupingBy(
                        data -> data.reportedDate().toLocalDate().toString(),
                        Collectors.counting()
                ));
    }

    private IssueItem toIssueItem(IssueSummaryOutput summary) {
        /*
         * controller 모듈의 DTO를 JavaFX 화면용 record로 변환한다.
         * 화면에서는 문자열 날짜, 로그인 ID, 코멘트 목록처럼 바로 표시하기 쉬운 형태가 필요하기 때문이다.
         */
        GetIssueDetailOutput detail = issue.getIssueDetail(new GetIssueDetailInput(summary.issueId()));
        List<CommentItem> comments = detail.comments().stream()
                .map(comment -> new CommentItem(
                        comment.authorUserId(),
                        comment.comment(),
                        comment.createdAt().format(DATE_FORMAT)
                ))
                .toList();

        return new IssueItem(
                summary.issueId(),
                summary.projectId(),
                summary.issueTitle(),
                detail.issueDescription(),
                summary.reporterUserId(),
                summary.reportedDate().format(DATE_FORMAT),
                summary.priority().name(),
                summary.status().name(),
                emptyIfNull(summary.assigneeUserId()),
                emptyIfNull(summary.fixerUserId()),
                comments
        );
    }

    private UserItem toUserItem(MockUserData data) {
        return new UserItem(data.userId(), data.loginId(), data.password(), data.role(), data.projectId());
    }

    private List<String> loginIdsByRole(UserRole role) {
        return database.users().values().stream()
                .filter(data -> data.role() == role)
                .map(MockUserData::loginId)
                .toList();
    }

    private Optional<MockUserData> findUser(String loginId) {
        return database.users().values().stream()
                .filter(data -> data.loginId().equals(loginId))
                .findFirst();
    }

    private Integer userIdOf(String loginId) {
        return findUser(loginId).map(MockUserData::userId).orElse(null);
    }

    private Integer adminUserId() {
        return database.users().values().stream()
                .filter(data -> data.role() == UserRole.ADMIN)
                .map(MockUserData::userId)
                .findFirst()
                .orElse(null);
    }

    private String emptyIfNull(String value) {
        return value == null ? "" : value;
    }
}
