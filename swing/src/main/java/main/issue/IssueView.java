package main.issue;

import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;
import issue.dto.getIssueDetail.v1.GetIssueDetailOutput;
import issue.dto.getIssueList.v1.IssueSummaryOutput;
import issue.dto.recommendAssignee.v1.RecommendAssigneeOutput;

import java.util.List;

// ui랑 controller이랑 묶는 인터페이스
public interface IssueView {

    record SearchCondition(
            Integer assigneeUserId,
            Integer reporterUserId,
            Integer fixerUserId,
            IssueStatus status,
            IssuePriority priority,
            String keyword
    ) {}

    record CreateIssueForm(
            String title,
            String description,
            IssuePriority priority
    ) {}

    record AssigneeCandidate(
            Integer userId,
            String loginId
    ) {}

    record ProjectUserOption(
            Integer userId,
            String loginId,
            UserRole role
    ) {}

    void setIssues(List<IssueSummaryOutput> issues);

    Integer getSelectedIssueId();

    Integer getActiveDetailIssueId();

    Integer getAssigneeUserIdInput();

    void setAssigneeCandidates(List<AssigneeCandidate> candidates);

    void setProjectUsers(List<ProjectUserOption> users);

    String getIssueCommentInput();

    IssueStatus getTargetIssueStatusInput();

    SearchCondition getSearchCondition();

    CreateIssueForm showCreateIssueDialog();

    void showIssueDetail(GetIssueDetailOutput output);

    void showRecommendations(RecommendAssigneeOutput output);

    void onSearchIssues(Runnable handler);

    void onRegisterIssue(Runnable handler);

    void onAssignIssue(Runnable handler);

    void onChangeIssueStatus(Runnable handler);

    void onAddIssueComment(Runnable handler);

    void onShowIssueDetail(Runnable handler);

    void onRecommendAssignee(Runnable handler);

    void onDeleteIssue(Runnable handler);

    void applyRole(UserRole role);

    void showMessage(String message);
}
