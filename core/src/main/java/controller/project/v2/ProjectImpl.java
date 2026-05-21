package controller.project.v2;

import enums.user.v1.UserRole;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import project.dto.createProject.v1.CreateProjectOutput;
import project.dto.deleteProject.v1.DeleteProjectInput;
import project.dto.deleteProject.v1.DeleteProjectOutput;
import project.dto.getProjectList.v1.GetProjectListInput;
import project.dto.getProjectList.v1.GetProjectListOutput;
import project.dto.getProjectList.v1.ProjectInfoOutput;
import project.dto.updateProjectInfo.v1.UpdateProjectInfoInput;
import project.dto.updateProjectInfo.v1.UpdateProjectInfoOutput;
import project.v2.Project;
import repository.ProjectRepository;
import repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ProjectImpl implements Project {
    @NonNull
    private UserRepository userRepository;
    @NonNull
    private ProjectRepository projectRepository;

    @Override
    public CreateProjectOutput createProject(project.dto.createProject.v2.CreateProjectInput input) {
        try {
            domain.User requester = userRepository.load(input.requesterUserId());
            if (requester.getRole() != UserRole.ADMIN) {
                return new CreateProjectOutput(false, null, "ADMIN만 프로젝트를 생성할 수 있다");
            }
        } catch (Exception e) {
            return new CreateProjectOutput(false, null, e.getMessage());
        }

        try {
            Integer projectId = projectRepository.save(new domain.Project(input.title()));
            return new CreateProjectOutput(true, projectId, null);
        } catch (Exception e) {
            return new CreateProjectOutput(false, null, e.getMessage());
        }
    }

    @Override
    public GetProjectListOutput getProjectList(GetProjectListInput input) {
        try {
            List<domain.Project> projects = projectRepository.list();
            List<ProjectInfoOutput> projectInfoOutputs = new ArrayList<>();
            for (domain.Project project : projects) {
                projectInfoOutputs.add(new ProjectInfoOutput(project.getId(), project.getName()));
            }
            return new GetProjectListOutput(true, null, projectInfoOutputs);
        } catch (Exception e) {
            return new GetProjectListOutput(false, e.getMessage(), null);
        }
    }

    @Override
    public UpdateProjectInfoOutput updateProjectInfo(UpdateProjectInfoInput input) {
        try {
            domain.User requester = userRepository.load(input.requesterUserId());
            if (requester.getRole() != UserRole.ADMIN) {
                return new UpdateProjectInfoOutput(false, "ADMIN만 프로젝트를 수정할 수 있다");
            }
        } catch (Exception e) {
            return new UpdateProjectInfoOutput(false, e.getMessage());
        }

        try {
            domain.Project project = projectRepository.load(input.projectId());
            project.setName(input.title());
            projectRepository.update(project);
            return new UpdateProjectInfoOutput(true, null);
        } catch (Exception e) {
            return new UpdateProjectInfoOutput(false, e.getMessage());
        }
    }

    @Override
    public DeleteProjectOutput deleteProject(DeleteProjectInput input) {
        try {
            domain.User requester = userRepository.load(input.requesterUserId());
            if (requester.getRole() != UserRole.ADMIN) {
                return new DeleteProjectOutput(false, "ADMIN만 프로젝트를 삭제할 수 있다");
            }
        } catch (Exception e) {
            return new DeleteProjectOutput(false, e.getMessage());
        }

        try {
            projectRepository.delete(input.projectId());
            return new DeleteProjectOutput(true, null);
        } catch (Exception e) {
            return new DeleteProjectOutput(false, e.getMessage());
        }
    }
}
