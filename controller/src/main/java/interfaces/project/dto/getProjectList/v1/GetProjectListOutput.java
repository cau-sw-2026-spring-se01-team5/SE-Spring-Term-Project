package interfaces.project.dto.getProjectList.v1;

import java.util.List;

public record GetProjectListOutput(
        boolean success, // 조회 성공 가부
        String message, // ui로 던질 메세지
        List<ProjectInfoOutput> projectList // 전체 프로젝트 리스트
) {
}