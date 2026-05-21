package project.v2;

import project.dto.createProject.v1.CreateProjectOutput;
import project.dto.createProject.v2.CreateProjectInput;
import project.dto.deleteProject.v1.DeleteProjectInput;
import project.dto.deleteProject.v1.DeleteProjectOutput;
import project.dto.getProjectList.v1.GetProjectListInput;
import project.dto.getProjectList.v1.GetProjectListOutput;
import project.dto.updateProjectInfo.v1.UpdateProjectInfoInput;
import project.dto.updateProjectInfo.v1.UpdateProjectInfoOutput;

public interface Project {
    CreateProjectOutput createProject(CreateProjectInput input);

    GetProjectListOutput getProjectList(GetProjectListInput input);

    UpdateProjectInfoOutput updateProjectInfo(UpdateProjectInfoInput input);

    DeleteProjectOutput deleteProject(DeleteProjectInput input);
}
