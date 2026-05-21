package controller.project.v2;

import domain.Project;
import domain.User;
import enums.user.v1.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.dto.createProject.v1.CreateProjectOutput;
import project.dto.createProject.v2.CreateProjectInput;
import repository.ProjectRepository;
import repository.UserRepository;

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

        projectImpl = new ProjectImpl(userRepository, projectRepository);
    }

    @Test
    void createProject() {
        CreateProjectOutput output = projectImpl.createProject(
                new CreateProjectInput(admin.getId(), newProject.getName())
        );

        assertEquals(newProject.getId(), output.projectId());
    }

    @Test
    void nonAdmin() {
        CreateProjectOutput output = projectImpl.createProject(
                new CreateProjectInput(nonAdmin.getId(), newProject.getName())
        );

        assertEquals(false, output.success());
    }
}
