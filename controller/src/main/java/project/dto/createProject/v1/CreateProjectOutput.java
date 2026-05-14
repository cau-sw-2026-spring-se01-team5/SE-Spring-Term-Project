package project.dto.createProject.v1;

public record CreateProjectOutput(
        boolean success, // 생성 성공 여부
        Integer projectId, // 생성된 프로젝트 고유 id
        String message // ui로 던져줄 메세지 - 실패시 실패 meg등
) {
}