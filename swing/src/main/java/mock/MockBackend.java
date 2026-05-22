package mock;

import auth.dto.login.v1.LoginInput;
import auth.dto.login.v1.LoginOutput;
import auth.v1.Auth;
import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;
import issue.dto.addIssueComment.v1.AddIssueCommentInput;
import issue.dto.addIssueComment.v1.AddIssueCommentOutput;
import issue.dto.assignIssue.v1.AssignIssueInput;
import issue.dto.assignIssue.v1.AssignIssueOutput;
import issue.dto.changeIssueStatus.v1.ChangeIssueStatusInput;
import issue.dto.changeIssueStatus.v1.ChangeIssueStatusOutput;
import issue.dto.deleteIssue.v1.DeleteIssueInput;
import issue.dto.deleteIssue.v1.DeleteIssueOutput;
import issue.dto.getIssueDetail.v1.CommentOutput;
import issue.dto.getIssueDetail.v1.GetIssueDetailInput;
import issue.dto.getIssueDetail.v1.GetIssueDetailOutput;
import issue.dto.getIssueList.v1.GetIssueListInput;
import issue.dto.getIssueList.v1.GetIssueListOutput;
import issue.dto.getIssueList.v1.IssueSummaryOutput;
import issue.dto.recommendAssignee.v1.RecommendAssigneeInput;
import issue.dto.recommendAssignee.v1.RecommendAssigneeOutput;
import issue.dto.recommendAssignee.v1.RecommendedAssigneeOutput;
import issue.dto.registerIssue.v1.RegisterIssueInput;
import issue.dto.registerIssue.v1.RegisterIssueOutput;
import issue.v1.Issue;
import project.dto.createProject.v1.CreateProjectInput;
import project.dto.createProject.v1.CreateProjectOutput;
import project.dto.deleteProject.v1.DeleteProjectInput;
import project.dto.deleteProject.v1.DeleteProjectOutput;
import project.dto.getProjectList.v1.GetProjectListInput;
import project.dto.getProjectList.v1.GetProjectListOutput;
import project.dto.getProjectList.v1.ProjectInfoOutput;
import project.dto.updateProjectInfo.v1.UpdateProjectInfoInput;
import project.dto.updateProjectInfo.v1.UpdateProjectInfoOutput;
import project.v1.Project;
import user.v1.RoleResolver;
import user.dto.createUser.v1.CreateUserInput;
import user.dto.createUser.v1.CreateUserOutput;
import user.dto.deleteUser.v1.DeleteUserInput;
import user.dto.deleteUser.v1.DeleteUserOutput;
import user.dto.getProjectUserList.v1.GetProjectUserListInput;
import user.dto.getProjectUserList.v1.GetProjectUserListOutput;
import user.dto.getProjectUserList.v1.UserInfoOutput;
import user.dto.getUserInfo.v1.GetUserInfoInput;
import user.dto.getUserInfo.v1.GetUserInfoOutput;
import user.v1.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MockBackend implements Auth, Project, User, Issue, RoleResolver {

    private int nextProjectId = 2;
    private int nextUserId = 10;
    private int nextIssueId = 100;
    private int nextCommentId = 1000;

    private final Map<Integer, MockUser> users = new LinkedHashMap<>();
    private final Map<Integer, MockProject> projects = new LinkedHashMap<>();
    private final Map<Integer, MockIssue> issues = new LinkedHashMap<>();

    public MockBackend() {
        projects.put(1, new MockProject(1, "project1"));

        users.put(1, new MockUser(1, "admin", "1234", UserRole.ADMIN, 1));
        users.put(2, new MockUser(2, "pl1", "1234", UserRole.PL, 1));
        users.put(3, new MockUser(3, "dev1", "1234", UserRole.DEV, 1));
        users.put(4, new MockUser(4, "tester1", "1234", UserRole.TESTER, 1));
        users.put(5, new MockUser(5, "dev2", "1234", UserRole.DEV, 1));
        users.put(6, new MockUser(6, "tester2", "1234", UserRole.TESTER, 1));

        MockIssue issue = new MockIssue(
                1,
                1,
                "로그인 버튼 클릭 시 오류",
                "로그인 버튼 클릭 시 NullPointerException 발생",
                4,
                LocalDateTime.now().minusDays(1),
                null,
                null,
                IssuePriority.MAJOR,
                IssueStatus.NEW
        );

        issues.put(issue.issueId, issue);
    }

    @Override
    public LoginOutput login(LoginInput input) {
        if (input.loginId() == null || input.loginId().isBlank()) {
            return new LoginOutput(false, null, "아이디를 입력하세요.");
        }

        if (input.password() == null || input.password().isBlank()) {
            return new LoginOutput(false, null, "비밀번호를 입력하세요.");
        }

        for (MockUser user : users.values()) {
            if (user.loginId.equals(input.loginId())
                    && user.password.equals(input.password())) {
                return new LoginOutput(true, user.userId, "로그인 성공");
            }
        }

        return new LoginOutput(false, null, "아이디 또는 비밀번호가 올바르지 않습니다.");
    }

    @Override
    public void logout() {
    }

    @Override
    public UserRole resolveRole(Integer userId) {
        MockUser user = users.get(userId);
        return user == null ? null : user.role;
    }

    @Override
    public String resolveLoginId(Integer userId) {
        MockUser user = users.get(userId);
        return user == null ? null : user.loginId;
    }

    @Override
    public CreateProjectOutput createProject(CreateProjectInput input) {
        if (input.title() == null || input.title().isBlank()) {
            return new CreateProjectOutput(false, null, "프로젝트 제목은 비어 있을 수 없습니다.");
        }

        int id = nextProjectId++;
        projects.put(id, new MockProject(id, input.title()));

        return new CreateProjectOutput(true, id, "프로젝트 생성 성공");
    }

    @Override
    public GetProjectListOutput getProjectList(GetProjectListInput input) {
        List<ProjectInfoOutput> result = projects.values()
                .stream()
                .map(p -> new ProjectInfoOutput(p.projectId, p.title))
                .toList();

        return new GetProjectListOutput(true, "프로젝트 조회 성공", result);
    }

    @Override
    public UpdateProjectInfoOutput updateProjectInfo(UpdateProjectInfoInput input) {
        if (!isAdmin(input.requesterUserId())) {
            return new UpdateProjectInfoOutput(false, "Admin만 프로젝트를 수정할 수 있습니다.");
        }

        MockProject project = projects.get(input.projectId());

        if (project == null) {
            return new UpdateProjectInfoOutput(false, "프로젝트가 존재하지 않습니다.");
        }

        project.title = input.title();

        return new UpdateProjectInfoOutput(true, "프로젝트 수정 성공");
    }

    @Override
    public DeleteProjectOutput deleteProject(DeleteProjectInput input) {
        if (!isAdmin(input.requesterUserId())) {
            return new DeleteProjectOutput(false, "Admin만 프로젝트를 삭제할 수 있습니다.");
        }

        projects.remove(input.projectId());

        return new DeleteProjectOutput(true, "프로젝트 삭제 성공");
    }

    @Override
    public CreateUserOutput createUser(CreateUserInput input) {
        if (!isAdmin(input.requesterUserId())) {
            return new CreateUserOutput(false, null, "Admin만 유저를 생성할 수 있습니다.");
        }

        if (input.loginId() == null || input.loginId().isBlank()) {
            return new CreateUserOutput(false, null, "로그인 ID는 비어 있을 수 없습니다.");
        }

        int id = nextUserId++;
        users.put(
                id,
                new MockUser(
                        id,
                        input.loginId(),
                        input.password(),
                        input.role(),
                        input.projectId()
                )
        );

        return new CreateUserOutput(true, id, "유저 생성 성공");
    }

    @Override
    public GetProjectUserListOutput getProjectUserList(GetProjectUserListInput input) {
        List<UserInfoOutput> result = users.values()
                .stream()
                .filter(u -> Objects.equals(u.projectId, input.projectId()))
                .map(u -> new UserInfoOutput(u.userId, u.loginId, u.role, u.projectId))
                .toList();

        return new GetProjectUserListOutput(true, "유저 목록 조회 성공", result);
    }

    @Override
    public DeleteUserOutput deleteUser(DeleteUserInput input) {
        if (!isAdmin(input.requesterUserId())) {
            return new DeleteUserOutput(false, "Admin만 유저를 삭제할 수 있습니다.");
        }

        users.remove(input.targetUserId());

        return new DeleteUserOutput(true, "유저 삭제 성공");
    }

    @Override
    public GetUserInfoOutput getUserInfo(GetUserInfoInput input) {
        MockUser user = users.get(input.userId());
        if (user == null) {
            return new GetUserInfoOutput(
                    false,
                    null,
                    null,
                    null,
                    input.projectId(),
                    "유저를 찾을 수 없습니다."
            );
        }
        return new GetUserInfoOutput(
                true,
                user.userId,
                user.loginId,
                user.role,
                user.projectId,
                "유저 정보 조회 성공"
        );
    }

    @Override
    public RegisterIssueOutput registerIssue(RegisterIssueInput input) {
        MockUser reporter = users.get(input.reporterUserId());

        if (reporter == null || reporter.role != UserRole.TESTER) {
            return new RegisterIssueOutput(false, null, "Tester만 이슈를 등록할 수 있습니다.");
        }

        if (input.issueTitle() == null || input.issueTitle().isBlank()) {
            return new RegisterIssueOutput(false, null, "이슈 제목은 비어 있을 수 없습니다.");
        }

        if (input.issueDescription() == null || input.issueDescription().isBlank()) {
            return new RegisterIssueOutput(false, null, "이슈 설명은 비어 있을 수 없습니다.");
        }

        int id = nextIssueId++;

        MockIssue issue = new MockIssue(
                id,
                input.projectId(),
                input.issueTitle(),
                input.issueDescription(),
                input.reporterUserId(),
                LocalDateTime.now(),
                null,
                null,
                input.priority() == null ? IssuePriority.MAJOR : input.priority(),
                IssueStatus.NEW
        );

        issues.put(id, issue);

        return new RegisterIssueOutput(true, id, "이슈 등록 성공");
    }

    @Override
    public AssignIssueOutput assignIssue(AssignIssueInput input) {
        if (!hasRole(input.requesterUserId(), UserRole.PL)) {
            return new AssignIssueOutput(false, input.issueId(), "PL만 이슈를 배정할 수 있습니다.");
        }

        MockUser assignee = users.get(input.assigneeUserId());

        if (assignee == null || assignee.role != UserRole.DEV) {
            return new AssignIssueOutput(false, input.issueId(), "Assignee는 DEV여야 합니다.");
        }

        MockIssue issue = issues.get(input.issueId());

        if (issue == null) {
            return new AssignIssueOutput(false, input.issueId(), "이슈가 존재하지 않습니다.");
        }

        issue.assigneeUserId = input.assigneeUserId();
        issue.status = IssueStatus.ASSIGNED;

        addCommentInternal(issue, input.requesterUserId(), input.comment());

        return new AssignIssueOutput(true, input.issueId(), "이슈 배정 성공");
    }

    @Override
    public ChangeIssueStatusOutput changeIssueStatus(ChangeIssueStatusInput input) {
        MockIssue issue = issues.get(input.issueId());

        if (issue == null) {
            return new ChangeIssueStatusOutput(false, input.issueId(), null, "이슈가 존재하지 않습니다.");
        }

        IssueStatus targetStatus = input.targetStatus();

        if (targetStatus == IssueStatus.FIXED) {
            if (!hasRole(input.requesterUserId(), UserRole.DEV)) {
                return new ChangeIssueStatusOutput(false, input.issueId(), issue.status, "DEV만 fixed 처리할 수 있습니다.");
            }

            issue.fixerUserId = input.requesterUserId();
        }

        if (targetStatus == IssueStatus.RESOLVED) {
            if (!hasRole(input.requesterUserId(), UserRole.TESTER)) {
                return new ChangeIssueStatusOutput(false, input.issueId(), issue.status, "TESTER만 resolved 처리할 수 있습니다.");
            }
        }

        if (targetStatus == IssueStatus.CLOSED) {
            if (!hasRole(input.requesterUserId(), UserRole.PL)) {
                return new ChangeIssueStatusOutput(false, input.issueId(), issue.status, "PL만 closed 처리할 수 있습니다.");
            }
        }

        issue.status = targetStatus;

        return new ChangeIssueStatusOutput(true, input.issueId(), issue.status, "상태 변경 성공");
    }

    @Override
    public AddIssueCommentOutput addIssueComment(AddIssueCommentInput input) {
        MockIssue issue = issues.get(input.issueId());

        if (issue == null) {
            return new AddIssueCommentOutput(false, input.issueId(), null, "이슈가 존재하지 않습니다.");
        }

        int commentId = addCommentInternal(issue, input.authorUserId(), input.comment());

        return new AddIssueCommentOutput(true, input.issueId(), commentId, "코멘트 추가 성공");
    }

    @Override
    public GetIssueDetailOutput getIssueDetail(GetIssueDetailInput input) {
        MockIssue issue = issues.get(input.issueId());

        if (issue == null) {
            return new GetIssueDetailOutput(
                    false,
                    "이슈가 존재하지 않습니다.",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    List.of()
            );
        }

        return new GetIssueDetailOutput(
                true,
                "이슈 상세 조회 성공",
                issue.issueId,
                issue.projectId,
                issue.issueTitle,
                issue.issueDescription,
                issue.reporterUserId == null ? null : users.get(issue.reporterUserId).loginId,
                issue.reportedDate,
                issue.fixerUserId == null ? null : users.get(issue.fixerUserId).loginId,
                issue.assigneeUserId,
                issue.priority,
                issue.status,
                issue.comments
        );
    }

    @Override
    public GetIssueListOutput getIssueList(GetIssueListInput input) {
        List<IssueSummaryOutput> result = issues.values()
                .stream()
                .filter(i -> Objects.equals(i.projectId, input.projectId()))
                .filter(i -> input.assigneeUserId() == null || Objects.equals(i.assigneeUserId, input.assigneeUserId()))
                .filter(i -> input.reporterUserId() == null || Objects.equals(i.reporterUserId, input.reporterUserId()))
                .filter(i -> input.fixerUserId() == null || Objects.equals(i.fixerUserId, input.fixerUserId()))
                .filter(i -> input.status() == null || i.status == input.status())
                .filter(i -> input.priority() == null || i.priority == input.priority())
                .filter(i -> {
                    if (input.keyword() == null || input.keyword().isBlank()) {
                        return true;
                    }

                    String keyword = input.keyword().toLowerCase();

                    return i.issueTitle.toLowerCase().contains(keyword)
                            || i.issueDescription.toLowerCase().contains(keyword);
                })
                .map(this::toIssueSummary)
                .collect(Collectors.toList());

        return new GetIssueListOutput(true, "이슈 조회 성공", result);
    }

    @Override
    public RecommendAssigneeOutput recommendAssignees(RecommendAssigneeInput input) {
        List<MockUser> devs = users.values().stream()
                .filter(u -> u.role == UserRole.DEV)
                .limit(3)
                .toList();

        List<RecommendedAssigneeOutput> candidates =
                java.util.stream.IntStream.range(0, devs.size())
                        .mapToObj(i -> new RecommendedAssigneeOutput(
                                devs.get(i).loginId, // String userId 자리에 loginId
                                i + 1                // rank (1부터)
                        ))
                        .toList();

        return new RecommendAssigneeOutput(true, "담당자 추천 성공", candidates);
    }

    @Override
    public DeleteIssueOutput deleteIssue(DeleteIssueInput input) {
        if (!isAdmin(input.requesterUserId()) && !hasRole(input.requesterUserId(), UserRole.PL)) {
            return new DeleteIssueOutput(false, "Admin 또는 PL만 이슈를 삭제할 수 있습니다.");
        }

        issues.remove(input.issueId());

        return new DeleteIssueOutput(true, "이슈 삭제 성공");
    }

    private boolean isAdmin(Integer userId) {
        return hasRole(userId, UserRole.ADMIN);
    }

    private boolean hasRole(Integer userId, UserRole role) {
        MockUser user = users.get(userId);
        return user != null && user.role == role;
    }

    private int addCommentInternal(MockIssue issue, Integer authorUserId, String content) {
        if (content == null || content.isBlank()) {
            content = "(내용 없음)";
        }

        int commentId = nextCommentId++;

        MockUser author = users.get(authorUserId);
        String authorLoginId = (author == null) ? null : author.loginId;

        issue.comments.add(
                new CommentOutput(
                        commentId,
                        authorLoginId,
                        LocalDateTime.now(),
                        content
                )
        );

        return commentId;
    }

    private IssueSummaryOutput toIssueSummary(MockIssue issue) {
        return new IssueSummaryOutput(
                issue.issueId,
                issue.projectId,
                issue.issueTitle,
                issue.reporterUserId == null ? null : users.get(issue.reporterUserId).loginId,
                issue.assigneeUserId == null ? null : users.get(issue.assigneeUserId).loginId,
                issue.fixerUserId == null ? null : users.get(issue.fixerUserId).loginId,
                issue.priority,
                issue.status,
                issue.reportedDate
        );
    }

    private static class MockUser {
        private final Integer userId;
        private final String loginId;
        private final String password;
        private final UserRole role;
        private final Integer projectId;

        private MockUser(Integer userId, String loginId, String password, UserRole role, Integer projectId) {
            this.userId = userId;
            this.loginId = loginId;
            this.password = password;
            this.role = role;
            this.projectId = projectId;
        }
    }

    private static class MockProject {
        private final Integer projectId;
        private String title;

        private MockProject(Integer projectId, String title) {
            this.projectId = projectId;
            this.title = title;
        }
    }

    private static class MockIssue {
        private final Integer issueId;
        private final Integer projectId;
        private final String issueTitle;
        private final String issueDescription;
        private final Integer reporterUserId;
        private final LocalDateTime reportedDate;
        private Integer fixerUserId;
        private Integer assigneeUserId;
        private final IssuePriority priority;
        private IssueStatus status;
        private final List<CommentOutput> comments = new ArrayList<>();

        private MockIssue(
                Integer issueId,
                Integer projectId,
                String issueTitle,
                String issueDescription,
                Integer reporterUserId,
                LocalDateTime reportedDate,
                Integer fixerUserId,
                Integer assigneeUserId,
                IssuePriority priority,
                IssueStatus status
        ) {
            this.issueId = issueId;
            this.projectId = projectId;
            this.issueTitle = issueTitle;
            this.issueDescription = issueDescription;
            this.reporterUserId = reporterUserId;
            this.reportedDate = reportedDate;
            this.fixerUserId = fixerUserId;
            this.assigneeUserId = assigneeUserId;
            this.priority = priority;
            this.status = status;
        }
    }
}
