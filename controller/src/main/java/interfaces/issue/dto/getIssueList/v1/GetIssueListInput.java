package interfaces.issue.dto.getIssueList.v1;

public record GetIssueListInput(
        Integer projectId, // 모든 이슈 리스트 조회할 프로젝트 id
        Integer requesterUserId // 조회 요청한 유저
) {
}