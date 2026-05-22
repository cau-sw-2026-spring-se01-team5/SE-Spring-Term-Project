package mock;

import mock.model.MockProjectData;
import project.dto.createProject.v1.CreateProjectInput;
import project.dto.createProject.v1.CreateProjectOutput;
import project.dto.deleteProject.v1.DeleteProjectInput;
import project.dto.deleteProject.v1.DeleteProjectOutput;
import project.dto.getProjectList.v1.GetProjectListInput;
import project.dto.getProjectList.v1.GetProjectListOutput;
import project.dto.getProjectList.v1.ProjectInfoOutput;
import project.dto.updateProjectInfo.v1.UpdateProjectInfoInput;
import project.dto.updateProjectInfo.v1.UpdateProjectInfoOutput;
import project.v1.Project;

import java.util.List;

public class MockProject implements Project {

    private final MockDatabase database;

    public MockProject(MockDatabase database) {
        this.database = database;
    }

    @Override
    public CreateProjectOutput createProject(CreateProjectInput input) {
        if (input.title() == null || input.title().isBlank()) {
            return new CreateProjectOutput(false, null, "프로젝트 제목은 비어 있을 수 없습니다.");
        }

        int projectId = database.nextProjectId();

        database.projects().put(
                projectId,
                new MockProjectData(projectId, input.title())
        );

        return new CreateProjectOutput(true, projectId, "프로젝트 생성 성공");
    }

    @Override
    public GetProjectListOutput getProjectList(GetProjectListInput input) {
        List<ProjectInfoOutput> result = database.projects()
                .values()
                .stream()
                .map(project -> new ProjectInfoOutput(
                        project.projectId(),
                        project.title()
                ))
                .toList();

        return new GetProjectListOutput(true, "프로젝트 목록 조회 성공", result);
    }

    @Override
    public UpdateProjectInfoOutput updateProjectInfo(UpdateProjectInfoInput input) {
        if (!isAdmin(input.requesterUserId())) {
            return new UpdateProjectInfoOutput(false, "ADMIN만 프로젝트를 수정할 수 있습니다.");
        }

        MockProjectData project = database.projects().get(input.projectId());

        if (project == null) {
            return new UpdateProjectInfoOutput(false, "프로젝트가 존재하지 않습니다.");
        }

        if (input.title() == null || input.title().isBlank()) {
            return new UpdateProjectInfoOutput(false, "프로젝트 제목은 비어 있을 수 없습니다.");
        }

        project.updateTitle(input.title());

        return new UpdateProjectInfoOutput(true, "프로젝트 정보 수정 성공");
    }

    @Override
    public DeleteProjectOutput deleteProject(DeleteProjectInput input) {
        if (!isAdmin(input.requesterUserId())) {
            return new DeleteProjectOutput(false, "ADMIN만 프로젝트를 삭제할 수 있습니다.");
        }

        if (!database.projects().containsKey(input.projectId())) {
            return new DeleteProjectOutput(false, "프로젝트가 존재하지 않습니다.");
        }

        database.projects().remove(input.projectId());

        return new DeleteProjectOutput(true, "프로젝트 삭제 성공");
    }

    private boolean isAdmin(Integer userId) {
        return database.users().containsKey(userId)
                && database.users().get(userId).role() == enums.user.v1.UserRole.ADMIN;
    }
}
