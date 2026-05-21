package controller.project.v1;

import domain.Project;
import domain.User;
import enums.user.v1.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.dto.createProject.v1.CreateProjectInput;
import project.dto.createProject.v1.CreateProjectOutput;
import project.dto.deleteProject.v1.DeleteProjectInput;
import project.dto.deleteProject.v1.DeleteProjectOutput;
import project.dto.getProjectList.v1.GetProjectListInput;
import project.dto.getProjectList.v1.GetProjectListOutput;
import project.dto.updateProjectInfo.v1.UpdateProjectInfoInput;
import project.dto.updateProjectInfo.v1.UpdateProjectInfoOutput;
import repository.ProjectRepository;
import repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectImplTest {
    ProjectImpl projectImpl;
    User admin, nonAdmin;
    Project newProject;

    @BeforeEach
    void setUp() throws Exception {
        admin = new User("admin", "1234", UserRole.ADMIN);
        admin.setId(1);
        nonAdmin = new User("non-admin", "1234", UserRole.DEV);
        nonAdmin.setId(2);
        newProject = new Project("Test Project");
        newProject.setId(10);

        UserRepository userRepository = mock(UserRepository.class);
        ProjectRepository projectRepository = mock(ProjectRepository.class);

        when(userRepository.load(admin.getId())).thenReturn(admin);
        when(userRepository.load(nonAdmin.getId())).thenReturn(nonAdmin);
        when(projectRepository.save(any())).thenReturn(newProject.getId());
        when(projectRepository.list()).thenReturn(List.of(newProject));
        when(projectRepository.load(newProject.getId())).thenReturn(newProject);

        projectImpl = new ProjectImpl(userRepository, projectRepository);
    }

    @Test
    void createProject() {
        CreateProjectOutput output = projectImpl.createProject(
                new CreateProjectInput(newProject.getName())
        );

        assertEquals(true, output.success());
        assertEquals(newProject.getId(), output.projectId());
    }

    @Test
    void getProjectList() {
        GetProjectListOutput output = projectImpl.getProjectList(
                new GetProjectListInput(admin.getId())
        );

        assertEquals(true, output.success());
        assertEquals(1, output.projectList().size());
        assertEquals(newProject.getId(), output.projectList().get(0).projectId());
    }

    @Test
    void updateProjectInfo() {
        UpdateProjectInfoOutput output = projectImpl.updateProjectInfo(
                new UpdateProjectInfoInput(admin.getId(), newProject.getId(), "Updated Title")
        );

        assertEquals(true, output.success());
    }

    @Test
    void updateProjectInfoByNonAdmin() {
        UpdateProjectInfoOutput output = projectImpl.updateProjectInfo(
                new UpdateProjectInfoInput(nonAdmin.getId(), newProject.getId(), "Updated Title")
        );

        assertEquals(false, output.success());
    }

    @Test
    void deleteProject() {
        DeleteProjectOutput output = projectImpl.deleteProject(
                new DeleteProjectInput(admin.getId(), newProject.getId())
        );

        assertEquals(true, output.success());
    }

    @Test
    void deleteProjectByNonAdmin() {
        DeleteProjectOutput output = projectImpl.deleteProject(
                new DeleteProjectInput(nonAdmin.getId(), newProject.getId())
        );

        assertEquals(false, output.success());
    }
}
