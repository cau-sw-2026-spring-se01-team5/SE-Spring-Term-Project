package issue.dto.registerIssue.v1;

import enums.issue.v1.IssuePriority;

public record RegisterIssueInput(
        Integer projectId, // 이슈 등록할 프로젝트
        String issueTitle, // 이슈 제목
        String issueDescription, // 이슈 설명
        IssuePriority priority, // 이슈 우선순위
        Integer reporterUserId // 리포트한 사람의 ID (테스터 아니면 reject)
) {
}