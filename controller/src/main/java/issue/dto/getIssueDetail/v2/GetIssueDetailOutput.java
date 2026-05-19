package issue.dto.getIssueDetail.v2;

import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import issue.dto.getIssueDetail.v1.CommentOutput;

import java.time.LocalDateTime;
import java.util.List;

public record GetIssueDetailOutput(
        boolean success, // 조회 성공 가부
        String message, // ui로 던질 메세지

        Integer issueId, // 대상 이슈 id
        Integer projectId, // 이슈가 등록된 프로젝트의 id
        String issueTitle, // 이슈 제목
        String issueDescription, // 이슈 설명
        String reporterUserId, // 이슈 리포트한 사람id
        LocalDateTime reportedDate, // 이슈 등록 날짜
        String fixerUserId, // 이슈 해결한 사람id
        Integer assigneeUserId, // 이슈 배정된 사람id
        IssuePriority priority, // 이슈의 우선순위
        IssueStatus status, // 이슈 해결 상태
        List<CommentOutput> comments, // 이 이슈 관련된 코멘트들
        List<IssueStatus> allowedNextStatus // 현재 상태에서 바뀔 수 있는 상태들
) {
}
