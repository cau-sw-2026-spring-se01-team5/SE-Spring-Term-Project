package issue.dto.getIssueList.v1;

import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;

public record GetIssueListInput(
        Integer projectId, // 모든 이슈 리스트 조회할 프로젝트 id
        Integer requesterUserId, // 조회 요청한 유저

        Integer assigneeUserId, // 이슈 배정된 사람
        Integer reporterUserId, // 이슈 리포트 한 사람
        Integer fixerUserId, // 이슈 고친사람
        IssueStatus status, // 이슈 상태
        IssuePriority priority, // 이슈 우선순위
        String keyword // 제목, 요약에서 찾을 키워드
) {
}