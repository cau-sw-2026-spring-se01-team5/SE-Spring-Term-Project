package issue.dto.addIssueComment.v1;

public record AddIssueCommentOutput(
        boolean success, // 성공 여부
        Integer issueId, // 코멘트 단 대상 이슈 id
        Integer commentId, // 개별 코멘트의 고유 id
        String message // ui로 던질 메세지
) {
}