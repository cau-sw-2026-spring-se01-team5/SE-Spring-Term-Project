package projectselect;

import auth.v1.Auth;
import enums.user.v1.UserRole;
import project.dto.createProject.v1.CreateProjectInput;
import project.dto.deleteProject.v1.DeleteProjectInput;
import project.dto.getProjectList.v1.GetProjectListInput;
import project.dto.getProjectList.v1.ProjectInfoOutput;
import project.dto.updateProjectInfo.v1.UpdateProjectInfoInput;
import project.v1.Project;
import session.UserSession;

public class ProjectSelectController {

    private final ProjectSelectView view;
    private final Project projectService;
    private final Auth authService;
    private final UserSession session;
    private final Runnable enterProjectCallback;
    private final Runnable logoutCallback;

    public ProjectSelectController(
            ProjectSelectView view,
            Project projectService,
            Auth authService,
            UserSession session,
            Runnable enterProjectCallback,
            Runnable logoutCallback
    ) {
        this.view = view;
        this.projectService = projectService;
        this.authService = authService;
        this.session = session;
        this.enterProjectCallback = enterProjectCallback;
        this.logoutCallback = logoutCallback;

        bind();
    }

    public void start() {
        view.applyAdminPermission(session.role() == UserRole.ADMIN);
        loadProjects();
    }

    private void bind() {
        view.onLoadProjects(this::loadProjects);
        view.onCreateProject(this::createProject);
        view.onUpdateProject(this::updateProject);
        view.onDeleteProject(this::deleteProject);
        view.onEnterProject(this::enterProject);
        view.onLogout(this::logout);
    }

    private void loadProjects() {
        var output = projectService.getProjectList(new GetProjectListInput(session.userId()));

        if (!output.success()) {
            view.showMessage(output.message());
            return;
        }

        view.setProjects(output.projectList());
    }

    private void createProject() {
        String title = view.getNewProjectTitleInput();

        if (title == null) {
            return;
        }

        var output = projectService.createProject(
                new CreateProjectInput(title)
        );

        view.showMessage(output.message());

        if (output.success()) {
            loadProjects();
        }
    }

    private void updateProject() {
        Integer projectId = view.getSelectedProjectId();

        if (projectId == null) {
            view.showMessage("수정할 프로젝트를 선택하세요.");
            return;
        }

        String title = view.getUpdateProjectTitleInput();

        if (title == null) {
            return;
        }

        var output = projectService.updateProjectInfo(
                new UpdateProjectInfoInput(
                        session.userId(),
                        projectId,
                        title
                )
        );

        view.showMessage(output.message());

        if (output.success()) {
            loadProjects();
        }
    }

    private void deleteProject() {
        Integer projectId = view.getSelectedProjectId();

        if (projectId == null) {
            view.showMessage("삭제할 프로젝트를 선택하세요.");
            return;
        }

        var output = projectService.deleteProject(
                new DeleteProjectInput(session.userId(), projectId)
        );

        view.showMessage(output.message());

        if (output.success()) {
            loadProjects();
        }
    }

    private void enterProject() {
        Integer projectId = view.getSelectedProjectId();

        if (projectId == null) {
            view.showMessage("프로젝트를 선택하세요.");
            return;
        }

        String title = findProjectTitle(projectId);
        session.selectProject(projectId, title);
        enterProjectCallback.run();
    }

    private String findProjectTitle(Integer projectId) {
        var output = projectService.getProjectList(new GetProjectListInput(session.userId()));

        if (!output.success()) {
            return null;
        }

        for (ProjectInfoOutput project : output.projectList()) {
            if (project.projectId().equals(projectId)) {
                return project.title();
            }
        }

        return null;
    }

    private void logout() {
        authService.logout();
        session.logout();
        logoutCallback.run();
    }
}
