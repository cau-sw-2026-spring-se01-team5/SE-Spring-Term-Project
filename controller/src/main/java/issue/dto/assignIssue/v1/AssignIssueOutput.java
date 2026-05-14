package issue.dto.assignIssue.v1;

public record AssignIssueOutput(
        boolean success, // 배정 성공 여부
        Integer issueId, // 배정한 이슈의 id
        String message // ui로 던져줄 메세지 - 실패시 실패 meg등
) {
}