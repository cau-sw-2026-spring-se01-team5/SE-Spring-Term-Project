package main.issue;

import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;
import issue.dto.getIssueDetail.v1.GetIssueDetailOutput;
import issue.dto.getIssueList.v1.IssueSummaryOutput;
import issue.dto.recommendAssignee.v1.RecommendAssigneeOutput;

import java.util.List;

public interface IssueView {

    void setIssues(List<IssueSummaryOutput> issues);

    Integer getSelectedIssueId();

    String getIssueTitleInput();

    String getIssueDescriptionInput();

    IssuePriority getIssuePriorityInput();

    Integer getAssigneeUserIdInput();

    String getIssueCommentInput();

    IssueStatus getTargetIssueStatusInput();

    Integer getFilterAssigneeUserId();

    Integer getFilterReporterUserId();

    Integer getFilterFixerUserId();

    IssueStatus getFilterStatus();

    IssuePriority getFilterPriority();

    String getFilterKeyword();

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