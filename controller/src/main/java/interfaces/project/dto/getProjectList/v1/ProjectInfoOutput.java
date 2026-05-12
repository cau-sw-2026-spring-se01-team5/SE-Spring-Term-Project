package interfaces.project.dto.getProjectList.v1;

public record ProjectInfoOutput(
        Integer projectId, // 프로젝트 고유 id
        String title // 프로젝트 제목
) {
}