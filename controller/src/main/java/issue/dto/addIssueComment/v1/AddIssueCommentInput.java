package issue.dto.addIssueComment.v1;

public record AddIssueCommentInput(
        Integer issueId, // 코멘트 달 이슈 Id
        Integer authorUserId, // 코멘트 작성자 id
        String comment // 코멘트 내용
) {
}