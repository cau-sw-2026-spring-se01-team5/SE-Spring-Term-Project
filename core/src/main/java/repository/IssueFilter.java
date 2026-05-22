package repository;

import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;

public record IssueFilter(
        Integer projectId,
        Integer assigneeId,
        Integer reporterId,
        Integer fixerId,
        IssueStatus status,
        IssuePriority priority,
        String keyword
) {

}
