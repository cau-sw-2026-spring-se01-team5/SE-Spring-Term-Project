package issue.dto.recommendAssignee.v1;

public record RecommendedAssigneeOutput(
        String userId, // 추천된 유저 id
        Integer rank // 추천 순위
) {
}