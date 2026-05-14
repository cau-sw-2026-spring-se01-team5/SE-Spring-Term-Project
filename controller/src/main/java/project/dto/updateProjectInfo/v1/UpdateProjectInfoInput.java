package project.dto.updateProjectInfo.v1;

public record UpdateProjectInfoInput(
        Integer requesterUserId, // 수정 요청한 유저(권한 확인용)
        Integer projectId, // 수정 대상 proj 아이디
        String title // 수정할 프로젝트 제목
) {
}