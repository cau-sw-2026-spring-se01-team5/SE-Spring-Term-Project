package issue.dto.changeIssueStatus.v1;

import enums.issue.v1.IssueStatus;

public record ChangeIssueStatusInput(
        Integer issueId, // 상태 바꿀 대상 이슈ID
        Integer requesterUserId, // 상태 바꾸기 요청한 userId
        IssueStatus targetStatus // 바꿀 상태
) {
}