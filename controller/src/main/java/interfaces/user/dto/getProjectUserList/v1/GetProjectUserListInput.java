package interfaces.user.dto.getProjectUserList.v1;

public record GetProjectUserListInput(
        Integer projectId // 조회 대상 프로젝트
) {
}