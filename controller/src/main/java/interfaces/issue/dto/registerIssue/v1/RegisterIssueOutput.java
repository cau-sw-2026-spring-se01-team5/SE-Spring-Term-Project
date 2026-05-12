package interfaces.issue.dto.registerIssue.v1;

public record RegisterIssueOutput(
        boolean success, // 생성 성공 여부
        Integer issueId, // 생성된 이슈의 고유 ID
        String message // ui로 던져줄 메세지 - 실패시 실패 meg등
) {
}