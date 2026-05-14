package issue.dto.getIssueList.v1;

import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;

import java.time.LocalDateTime;

public record IssueSummaryOutput(
        Integer issueId, // 이슈 고유 id
        Integer projectId, // 해당 프로젝트 id
        String issueTitle, // 이슈 제목
        String reporterUserId, // 이슈 등록한 사람
        String assigneeUserId, // 이슈 배정된 사람
        String fixerUserId, // 이슈 해결한 사람
        IssuePriority priority, // 우선순위
        IssueStatus status, // 이슈 해결 상태
        LocalDateTime reportedDate // 이슈 등록 날짜
) {
}