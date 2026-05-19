package app;

import enums.user.v1.UserRole;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
 * JavaFX 데모 화면을 위한 JavaFxBackend 구현체이다.
 *
 * 설계 의도:
 * - View가 DemoDataStore를 직접 호출하지 않도록 중간에 둔 어댑터 역할의 클래스이다.
 * - DemoDataStore의 내부 모델을 화면용 record(ProjectItem, IssueItem 등)로 변환한다.
 * - 이렇게 하면 View는 화면에 필요한 데이터 모양만 알면 되고, 저장소 내부 구조에는 의존하지 않는다.
 */
public class DemoJavaFxBackend implements JavaFxBackend {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public Optional<LoginUser> login(String loginId, String password) {
        return store.findUser(loginId, password)
                .map(user -> new LoginUser(user.loginId(), user.role()));
    }

    @Override
    public int countByStatus(String status) {
        return store.countByStatus(status);
    }

    @Override
    public List<ProjectItem> projects() {
        return store.projects().stream().map(this::projectItem).toList();
    }

    @Override
    public List<ProjectItem> projectsForUser(String loginId, UserRole role) {
        return store.projectsForUser(loginId, role).stream().map(this::projectItem).toList();
    }

    @Override
    public List<UserItem> usersForProject(int projectId) {
        return store.usersForProject(projectId).stream().map(this::userItem).toList();
    }

    @Override
    public ProjectItem addProject(String name, String description) {
        return projectItem(store.addProject(name, description));
    }

    @Override
    public void deleteProject(int projectId) {
        store.deleteProject(projectId);
    }

    @Override
    public boolean hasLoginId(String loginId) {
        return store.hasLoginId(loginId);
    }

    @Override
    public void addUser(String loginId, String password, UserRole role, int projectId) {
        store.addUser(loginId, password, role, projectId);
    }

    @Override
    public void deleteUser(String loginId) {
        store.deleteUser(loginId);
    }

    @Override
    public List<String> developerLoginIds() {
        return store.developerLoginIds();
    }

    @Override
    public List<String> developerLoginIdsForProject(int projectId) {
        return store.developerLoginIdsForProject(projectId);
    }

    @Override
    public List<String> testerLoginIds() {
        return store.testerLoginIds();
    }

    @Override
    public List<IssueItem> issuesForRole(String loginId, UserRole role) {
        /*
         * 역할별 조회 범위는 화면마다 달라져야 한다.
         * 예를 들어 개발자는 자기에게 배정된 이슈 중심으로 보고, PL은 프로젝트 이슈를 관리한다.
         * 이 필터링 결과를 화면용 IssueItem으로 변환해서 View에 전달한다.
         */
        return store.issuesForRole(loginId, role).stream().map(this::issueItem).toList();
    }

    @Override
    public void registerIssue(int projectId, String title, String description, String reporter, String priority) {
        store.registerIssue(projectId, title, description, reporter, priority);
    }

    @Override
    public void assignIssue(int issueId, String assignee, String writer, String comment) {
        store.assignIssue(issueId, assignee, writer, comment);
    }

    @Override
    public void markFixed(int issueId, String fixer, String comment) {
        store.markFixed(issueId, fixer, comment);
    }

    @Override
    public void resolveIssue(int issueId, String writer, String comment) {
        store.resolveIssue(issueId, writer, comment);
    }

    @Override
    public void reopenIssue(int issueId, String writer, String comment) {
        store.reopenIssue(issueId, writer, comment);
    }

    @Override
    public void closeIssue(int issueId, String writer, String comment) {
        store.closeIssue(issueId, writer, comment);
    }

    @Override
    public void addComment(int issueId, String writer, String comment) {
        store.addComment(issueId, writer, comment);
    }

    @Override
    public List<String> recommendAssignees(IssueItem issue) {
        return store.recommendAssignees(findIssue(issue.id()));
    }

    @Override
    public Map<String, Long> dailyIssueCounts() {
        return store.dailyIssueCounts();
    }

    private ProjectItem projectItem(DemoDataStore.ProjectRecord project) {
        /* 저장소의 ProjectRecord를 화면에서 쓰는 ProjectItem으로 변환한다. */
        return new ProjectItem(project.id(), project.name(), project.description());
    }

    private UserItem userItem(DemoDataStore.AppUser user) {
        /* 저장소의 AppUser를 화면에서 쓰는 UserItem으로 변환한다. */
        return new UserItem(user.id(), user.loginId(), user.password(), user.role(), user.projectId());
    }

    private IssueItem issueItem(DemoDataStore.IssueRecord issue) {
        /*
         * 이슈 상세 정보와 테이블 출력에 필요한 필드만 모아서 화면용 객체로 만든다.
         * View가 저장소 객체를 직접 수정하지 못하게 하는 보호막 역할도 한다.
         */
        return new IssueItem(
                issue.id(),
                issue.projectId(),
                issue.title(),
                issue.description(),
                issue.reporter(),
                issue.reportedDate(),
                issue.priority(),
                issue.status(),
                issue.assignee(),
                issue.fixer(),
                issue.comments().stream().map(this::commentItem).toList()
        );
    }

    private CommentItem commentItem(DemoDataStore.IssueComment comment) {
        return new CommentItem(comment.author(), comment.content(), comment.createdAt());
    }

    private DemoDataStore.IssueRecord findIssue(int issueId) {
        /*
         * 추천 기능은 기존 해결 이력을 비교해야 하므로 저장소의 원본 이슈가 필요하다.
         * 화면에는 IssueItem만 넘기지만, 이 구현체 내부에서 다시 원본 이슈를 찾아 처리한다.
         */
        return store.issuesForRole("admin", UserRole.ADMIN).stream()
                .filter(issue -> issue.id() == issueId)
                .findFirst()
                .orElseThrow();
    }
}
