package interfaces.issue.dto.assignIssue.v1;

public record AssignIssueInput(
        Integer issueId, // 배정할 이슈 ID
        Integer requesterUserId, // 배정 요청한 사용자(PL 권한 확인용)
        Integer assigneeUserId, // 이슈 담당자 ID(dev 유저 확인용)
        String comment // PL이 남기는 배정 코멘트
) {
}