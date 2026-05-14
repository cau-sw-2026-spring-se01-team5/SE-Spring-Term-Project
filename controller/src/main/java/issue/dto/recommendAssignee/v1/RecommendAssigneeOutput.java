package issue.dto.recommendAssignee.v1;

import java.util.List;

public record RecommendAssigneeOutput(
        boolean success, // 후보 추천 성공 여부
        String message, // ui로 던질 메세지
        List<RecommendedAssigneeOutput> candidates // 추천 후보 리스트
) {
}