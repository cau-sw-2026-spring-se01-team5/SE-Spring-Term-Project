package interfaces.project.dto.getProjectList.v1;

public record GetProjectListInput(
        Integer requesterUserId // 프로젝트 전체 리스트 조회 요청 유저 고유 id
) {
}