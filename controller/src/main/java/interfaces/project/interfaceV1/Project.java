package interfaces.project.interfaceV1;

import interfaces.project.dto.createProject.v1.CreateProjectInput;
import interfaces.project.dto.createProject.v1.CreateProjectOutput;
import interfaces.project.dto.deleteProject.v1.DeleteProjectInput;
import interfaces.project.dto.deleteProject.v1.DeleteProjectOutput;
import interfaces.project.dto.getProjectList.v1.GetProjectListInput;
import interfaces.project.dto.getProjectList.v1.GetProjectListOutput;
import interfaces.project.dto.updateProjectInfo.v1.UpdateProjectInfoInput;
import interfaces.project.dto.updateProjectInfo.v1.UpdateProjectInfoOutput;

public interface Project {

    CreateProjectOutput createProject(CreateProjectInput input);

    GetProjectListOutput getProjectList(GetProjectListInput input);

    UpdateProjectInfoOutput updateProjectInfo(UpdateProjectInfoInput input);

    DeleteProjectOutput deleteProject(DeleteProjectInput input);
}