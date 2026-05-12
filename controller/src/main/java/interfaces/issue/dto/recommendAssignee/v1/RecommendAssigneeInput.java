package interfaces.issue.dto.recommendAssignee.v1;

public record RecommendAssigneeInput(
        Integer issueId, // 후보 추천할 이슈 id
        Integer projectId // 후보 추천할 이슈가 포함된 프로젝트 id
) {
}