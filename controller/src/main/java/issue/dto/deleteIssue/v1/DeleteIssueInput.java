package issue.dto.deleteIssue.v1;

public record DeleteIssueInput(
        Integer requesterUserId, // 이슈 삭제 요청한 userId
        Integer issueId
) {
}