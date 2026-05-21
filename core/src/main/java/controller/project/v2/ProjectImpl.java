package controller.project.v2;

import project.dto.createProject.v1.CreateProjectInput;
import project.dto.createProject.v1.CreateProjectOutput;
import project.dto.deleteProject.v1.DeleteProjectInput;
import project.dto.deleteProject.v1.DeleteProjectOutput;
import project.dto.getProjectList.v1.GetProjectListInput;
import project.dto.getProjectList.v1.GetProjectListOutput;
import project.dto.updateProjectInfo.v1.UpdateProjectInfoInput;
import project.dto.updateProjectInfo.v1.UpdateProjectInfoOutput;
import project.v2.Project;

public class ProjectImpl implements Project {
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
