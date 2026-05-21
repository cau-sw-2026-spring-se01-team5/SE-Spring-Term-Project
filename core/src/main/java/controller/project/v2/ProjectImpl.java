package controller.project.v2;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import project.dto.createProject.v1.CreateProjectInput;
import project.dto.createProject.v1.CreateProjectOutput;
import project.dto.deleteProject.v1.DeleteProjectInput;
import project.dto.deleteProject.v1.DeleteProjectOutput;
import project.dto.getProjectList.v1.GetProjectListInput;
import project.dto.getProjectList.v1.GetProjectListOutput;
import project.dto.updateProjectInfo.v1.UpdateProjectInfoInput;
import project.dto.updateProjectInfo.v1.UpdateProjectInfoOutput;
import project.v2.Project;
import repository.ProjectRepository;
import repository.UserRepository;

@RequiredArgsConstructor
public class ProjectImpl implements Project {
    @NonNull
    private UserRepository userRepository;
    @NonNull
    private ProjectRepository projectRepository;

    @Override
    public CreateProjectOutput createProject(project.dto.createProject.v2.CreateProjectInput input) {
        return null;
    }

    @Override
    public GetProjectListOutput getProjectList(GetProjectListInput input) {
        return null;
    }

    @Override
    public UpdateProjectInfoOutput updateProjectInfo(UpdateProjectInfoInput input) {
        return null;
    }

    @Override
    public DeleteProjectOutput deleteProject(DeleteProjectInput input) {
        return null;
    }
}
