package interfaces.project.dto.deleteProject.v1;

public record DeleteProjectInput(
        Integer requesterUserId, // 프로젝트 삭제 요청한 userid(권한 확인용)
        Integer projectId // 삭제 대상 프로젝트 id
) {
}