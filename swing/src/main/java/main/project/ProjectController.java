package main.project;

import enums.user.v1.UserRole;
import project.dto.createProject.v1.CreateProjectInput;
import project.dto.deleteProject.v1.DeleteProjectInput;
import project.dto.updateProjectInfo.v1.UpdateProjectInfoInput;
import project.v1.Project;
import session.UserSession;

public class ProjectController {

    private final ProjectView view;
    private final Project projectService;
    private final UserSession session;

    public ProjectController(
            ProjectView view,
            Project projectService,
            UserSession session
    ) {
        this.view = view;
        this.projectService = projectService;
        this.session = session;

        bind();
    }

    public void applyRole() {
        boolean admin = session.role() == UserRole.ADMIN;
        view.applyAdminPermission(admin);
    }

    private void bind() {
        view.onCreateProject(this::createProject);
        view.onUpdateProject(this::updateProject);
        view.onDeleteProject(this::deleteProject);
    }

    private void createProject() {
        var output = projectService.createProject(
                new CreateProjectInput(
                        view.getProjectTitleInput()
                )
        );

        view.showMessage(output.message());
    }

    private void updateProject() {
        Integer projectId = requireProjectId();

        if (projectId == null) {
            return;
        }

        var output = projectService.updateProjectInfo(
                new UpdateProjectInfoInput(
                        session.userId(),
                        projectId,
                        view.getProjectTitleInput()
                )
        );

        view.showMessage(output.message());
    }

    private void deleteProject() {
        Integer projectId = requireProjectId();

        if (projectId == null) {
            return;
        }

        var output = projectService.deleteProject(
                new DeleteProjectInput(
                        session.userId(),
                        projectId
                )
        );

        view.showMessage(output.message());
    }

    private Integer requireProjectId() {
        Integer projectId = session.selectedProjectId();

        if (projectId == null) {
            view.showMessage("프로젝트를 선택하세요.");
            return null;
        }

        return projectId;
    }
}
