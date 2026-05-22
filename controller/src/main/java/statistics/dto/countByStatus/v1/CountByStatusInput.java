package statistics.dto.countByStatus.v1;

import enums.issue.v1.IssueStatus;

public record CountByStatusInput(
        Integer projectId,
        IssueStatus status
) {
}
