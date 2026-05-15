package mock;

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
import mock.model.MockIssueData;
import mock.model.MockUserData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MockIssue implements Issue {

    private final MockDatabase database;

    public MockIssue(MockDatabase database) {
        this.database = database;
    }

    @Override
    public RegisterIssueOutput registerIssue(RegisterIssueInput input) {
        MockUserData reporter = database.users().get(input.reporterUserId());

        if (reporter == null || reporter.role() != UserRole.TESTER) {
            return new RegisterIssueOutput(false, null, "Tester만 이슈를 등록할 수 있습니다.");
        }

        if (input.issueTitle() == null || input.issueTitle().isBlank()) {
            return new RegisterIssueOutput(false, null, "이슈 제목은 비어 있을 수 없습니다.");
        }

        if (input.issueDescription() == null || input.issueDescription().isBlank()) {
            return new RegisterIssueOutput(false, null, "이슈 설명은 비어 있을 수 없습니다.");
        }

        int issueId = database.nextIssueId();

        MockIssueData issue = new MockIssueData(
                issueId,
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

        database.issues().put(issueId, issue);

        return new RegisterIssueOutput(true, issueId, "이슈 등록 성공");
    }

    @Override
    public AssignIssueOutput assignIssue(AssignIssueInput input) {
        if (!hasRole(input.requesterUserId(), UserRole.PL)) {
            return new AssignIssueOutput(false, input.issueId(), "PL만 이슈를 배정할 수 있습니다.");
        }

        MockUserData assignee = database.users().get(input.assigneeUserId());

        if (assignee == null || assignee.role() != UserRole.DEV) {
            return new AssignIssueOutput(false, input.issueId(), "Assignee는 DEV여야 합니다.");
        }

        MockIssueData issue = database.issues().get(input.issueId());

        if (issue == null) {
            return new AssignIssueOutput(false, input.issueId(), "이슈가 존재하지 않습니다.");
        }

        issue.updateAssigneeUserId(input.assigneeUserId());
        issue.updateStatus(IssueStatus.ASSIGNED);

        addCommentInternal(issue, input.requesterUserId(), input.comment());

        return new AssignIssueOutput(true, input.issueId(), "이슈 배정 성공");
    }

    @Override
    public ChangeIssueStatusOutput changeIssueStatus(ChangeIssueStatusInput input) {
        MockIssueData issue = database.issues().get(input.issueId());

        if (issue == null) {
            return new ChangeIssueStatusOutput(false, input.issueId(), null, "이슈가 존재하지 않습니다.");
        }

        IssueStatus targetStatus = input.targetStatus();

        if (targetStatus == IssueStatus.FIXED) {
            if (!hasRole(input.requesterUserId(), UserRole.DEV)) {
                return new ChangeIssueStatusOutput(false, input.issueId(), issue.status(), "DEV만 fixed 처리할 수 있습니다.");
            }

            issue.updateFixerUserId(input.requesterUserId());
        }

        if (targetStatus == IssueStatus.RESOLVED) {
            if (!hasRole(input.requesterUserId(), UserRole.TESTER)) {
                return new ChangeIssueStatusOutput(false, input.issueId(), issue.status(), "TESTER만 resolved 처리할 수 있습니다.");
            }
        }

        if (targetStatus == IssueStatus.CLOSED) {
            if (!hasRole(input.requesterUserId(), UserRole.PL)) {
                return new ChangeIssueStatusOutput(false, input.issueId(), issue.status(), "PL만 closed 처리할 수 있습니다.");
            }
        }

        issue.updateStatus(targetStatus);

        return new ChangeIssueStatusOutput(true, input.issueId(), issue.status(), "상태 변경 성공");
    }

    @Override
    public AddIssueCommentOutput addIssueComment(AddIssueCommentInput input) {
        MockIssueData issue = database.issues().get(input.issueId());

        if (issue == null) {
            return new AddIssueCommentOutput(false, input.issueId(), null, "이슈가 존재하지 않습니다.");
        }

        int commentId = addCommentInternal(
                issue,
                input.authorUserId(),
                input.comment()
        );

        return new AddIssueCommentOutput(true, input.issueId(), commentId, "코멘트 추가 성공");
    }

    @Override
    public GetIssueDetailOutput getIssueDetail(GetIssueDetailInput input) {
        MockIssueData issue = database.issues().get(input.issueId());

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
                issue.issueId(),
                issue.projectId(),
                issue.issueTitle(),
                issue.issueDescription(),
                loginIdOf(issue.reporterUserId()),
                issue.reportedDate(),
                loginIdOf(issue.fixerUserId()),
                issue.assigneeUserId(),
                issue.priority(),
                issue.status(),
                issue.comments()
        );
    }

    @Override
    public GetIssueListOutput getIssueList(GetIssueListInput input) {
        List<IssueSummaryOutput> result = database.issues()
                .values()
                .stream()
                .filter(issue -> Objects.equals(issue.projectId(), input.projectId()))
                .filter(issue -> input.assigneeUserId() == null || Objects.equals(issue.assigneeUserId(), input.assigneeUserId()))
                .filter(issue -> input.reporterUserId() == null || Objects.equals(issue.reporterUserId(), input.reporterUserId()))
                .filter(issue -> input.fixerUserId() == null || Objects.equals(issue.fixerUserId(), input.fixerUserId()))
                .filter(issue -> input.status() == null || issue.status() == input.status())
                .filter(issue -> input.priority() == null || issue.priority() == input.priority())
                .filter(issue -> {
                    if (input.keyword() == null || input.keyword().isBlank()) {
                        return true;
                    }

                    String keyword = input.keyword().toLowerCase();

                    return issue.issueTitle().toLowerCase().contains(keyword)
                            || issue.issueDescription().toLowerCase().contains(keyword);
                })
                .map(this::toIssueSummary)
                .collect(Collectors.toList());

        return new GetIssueListOutput(true, "이슈 조회 성공", result);
    }

    @Override
    public RecommendAssigneeOutput recommendAssignees(RecommendAssigneeInput input) {
        List<MockUserData> devs = database.users()
                .values()
                .stream()
                .filter(user -> user.role() == UserRole.DEV)
                .limit(3)
                .toList();

        List<RecommendedAssigneeOutput> candidates =
                IntStream.range(0, devs.size())
                        .mapToObj(index -> new RecommendedAssigneeOutput(
                                devs.get(index).loginId(),
                                index + 1
                        ))
                        .toList();

        return new RecommendAssigneeOutput(true, "담당자 추천 성공", candidates);
    }

    @Override
    public DeleteIssueOutput deleteIssue(DeleteIssueInput input) {
        if (!isAdmin(input.requesterUserId())
                && !hasRole(input.requesterUserId(), UserRole.PL)) {
            return new DeleteIssueOutput(false, "Admin 또는 PL만 이슈를 삭제할 수 있습니다.");
        }

        if (!database.issues().containsKey(input.issueId())) {
            return new DeleteIssueOutput(false, "삭제할 이슈가 존재하지 않습니다.");
        }

        database.issues().remove(input.issueId());

        return new DeleteIssueOutput(true, "이슈 삭제 성공");
    }

    private int addCommentInternal(
            MockIssueData issue,
            Integer authorUserId,
            String content
    ) {
        if (content == null || content.isBlank()) {
            content = "(내용 없음)";
        }

        int commentId = database.nextCommentId();

        issue.comments().add(
                new CommentOutput(
                        commentId,
                        loginIdOf(authorUserId),
                        LocalDateTime.now(),
                        content
                )
        );

        return commentId;
    }

    private IssueSummaryOutput toIssueSummary(MockIssueData issue) {
        return new IssueSummaryOutput(
                issue.issueId(),
                issue.projectId(),
                issue.issueTitle(),
                loginIdOf(issue.reporterUserId()),
                loginIdOf(issue.assigneeUserId()),
                loginIdOf(issue.fixerUserId()),
                issue.priority(),
                issue.status(),
                issue.reportedDate()
        );
    }

    private boolean isAdmin(Integer userId) {
        return hasRole(userId, UserRole.ADMIN);
    }

    private boolean hasRole(Integer userId, UserRole role) {
        MockUserData user = database.users().get(userId);
        return user != null && user.role() == role;
    }

    private String loginIdOf(Integer userId) {
        if (userId == null) {
            return null;
        }

        MockUserData user = database.users().get(userId);

        return user == null ? null : user.loginId();
    }
}