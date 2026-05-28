package mock.model;

import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import issue.dto.getIssueDetail.v1.CommentOutput;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MockIssueData {

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

    public MockIssueData(
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

    public Integer issueId() {
        return issueId;
    }

    public Integer projectId() {
        return projectId;
    }

    public String issueTitle() {
        return issueTitle;
    }

    public String issueDescription() {
        return issueDescription;
    }

    public Integer reporterUserId() {
        return reporterUserId;
    }

    public LocalDateTime reportedDate() {
        return reportedDate;
    }

    public Integer fixerUserId() {
        return fixerUserId;
    }

    public void updateFixerUserId(Integer fixerUserId) {
        this.fixerUserId = fixerUserId;
    }

    public Integer assigneeUserId() {
        return assigneeUserId;
    }

    public void updateAssigneeUserId(Integer assigneeUserId) {
        this.assigneeUserId = assigneeUserId;
    }

    public IssuePriority priority() {
        return priority;
    }

    public IssueStatus status() {
        return status;
    }

    public void updateStatus(IssueStatus status) {
        this.status = status;
    }

    public List<CommentOutput> comments() {
        return comments;
    }
}