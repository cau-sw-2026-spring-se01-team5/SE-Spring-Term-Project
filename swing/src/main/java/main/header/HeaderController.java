package main.header;

import auth.v1.Auth;
import project.dto.getProjectList.v1.GetProjectListInput;
import project.v1.Project;
import session.UserSession;

public class HeaderController {
    private final HeaderView view;
    private final Project projectService;
    private final Auth authService;
    private final UserSession session;
    private final Runnable logoutCallback;
    private Runnable projectSelectedCallback = () -> {};
    public HeaderController(
            HeaderView view,
            Project projectService,
            Auth authService,
            UserSession session,
            Runnable logoutCallback
    ) {
        this.view = view;
        this.projectService = projectService;
        this.authService = authService;
        this.session = session;
        this.logoutCallback = logoutCallback;
        bind();
    }
    public void start() {
        view.setUserInfo(session.userId(), session.loginId(), session.role());
        refreshProjects();
    }
    private void bind() {
        //view.onRefreshProjects(this::refreshProjects);
        view.onLogout(this::logout);
        view.onProjectSelected(() -> {
            if (projectSelectedCallback != null) {
                projectSelectedCallback.run();
            }
        });
    }

    public void setProjectSelectedCallback(Runnable projectSelectedCallback) {
        this.projectSelectedCallback = projectSelectedCallback;
    }

    public void refreshProjects() {
        var output = projectService.getProjectList(
                new GetProjectListInput(session.userId())
        );

        if (!output.success()) {
            view.showMessage(output.message());
            return;
        }

        view.setProjects(output.projectList());
    }

    public Integer getSelectedProjectId() {
        return view.getSelectedProjectId();
    }

    private void logout() {
        authService.logout();
        session.logout();
        logoutCallback.run();
    }
}