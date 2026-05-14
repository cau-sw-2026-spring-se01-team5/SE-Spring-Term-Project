package issue.dto.changeIssueStatus.v1;

import enums.issue.v1.IssueStatus;

public record ChangeIssueStatusOutput(
        boolean success, // 성공 여부
        Integer issueId, // 바꾼 상태의 Id
        IssueStatus changedStatus, // 바뀌어진 상태
        String message // 실패시 ui로 던질 메세지
) {
}