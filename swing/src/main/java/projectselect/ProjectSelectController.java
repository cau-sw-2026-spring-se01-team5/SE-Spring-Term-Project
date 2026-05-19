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

/* 프로젝트 선택 화면 이벤트 담당 컨트롤러 */
/* 프로젝트 목록 조회, 생성, 수정, 삭제, 입장 */
public class ProjectSelectController {

    private final ProjectSelectView view;
    private final Project projectService;
    private final Auth authService;
    private final UserSession session;
    private final Runnable enterProject;
    private final Runnable logout;

    public ProjectSelectController(
            ProjectSelectView view,
            Project projectService,
            Auth authService,
            UserSession session,
            Runnable enterProject,
            Runnable logout
    ) {
        this.view = view;
        this.projectService = projectService;
        this.authService = authService;
        this.session = session;
        this.enterProject = enterProject;
        this.logout = logout;

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

    // 프로젝트 리스트 받아오기
    private void loadProjects() {
        var output = projectService.getProjectList(new GetProjectListInput(session.userId()));

        if (!output.success()) {
            view.showMessage(output.message());
            return;
        }

        view.setProjects(output.projectList());
    }

    // 프로젝트 생성
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

    // 프로젝트 수정
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

    // 프로젝트 삭제
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

    // 프로젝트 입장
    private void enterProject() {
        Integer projectId = view.getSelectedProjectId();

        if (projectId == null) {
            view.showMessage("프로젝트를 선택하세요.");
            return;
        }

        String title = findProjectTitle(projectId);
        session.selectProject(projectId, title);
        enterProject.run();
    }

    // 프로젝트 id로 프로젝트 제목 찾기
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

    // 로그아웃
    private void logout() {
        authService.logout();
        session.logout();
        logout.run();
    }
}
