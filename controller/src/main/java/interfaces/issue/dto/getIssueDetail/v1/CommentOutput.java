package interfaces.issue.dto.getIssueDetail.v1;

import java.time.LocalDateTime;

public record CommentOutput(
        Integer commentId, // 코멘트 고유 Id
        String authorUserId, // 코멘트 작성한 유저
        LocalDateTime createdAt, // 코멘트 작성일
        String comment // 코멘트 내용
) {
}