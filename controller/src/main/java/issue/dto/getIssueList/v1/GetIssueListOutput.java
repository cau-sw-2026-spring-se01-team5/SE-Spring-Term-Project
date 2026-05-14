package issue.dto.getIssueList.v1;

import java.util.List;

public record GetIssueListOutput(
        boolean success, // 조회 성공 가부
        String message, // ui로 던질 메세지
        List<IssueSummaryOutput> issues // 이슈 요약 정보들
) {
}