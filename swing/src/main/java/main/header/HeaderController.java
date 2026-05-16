package main.header;

import auth.v1.Auth;
import session.UserSession;

public class HeaderController {
    private final HeaderView view;
    private final Auth authService;
    private final UserSession session;
    private final Runnable backToProjectListCallback;
    private final Runnable logoutCallback;
    public HeaderController(
            HeaderView view,
            Auth authService,
            UserSession session,
            Runnable backToProjectListCallback,
            Runnable logoutCallback
    ) {
        this.view = view;
        this.authService = authService;
        this.session = session;
        this.backToProjectListCallback = backToProjectListCallback;
        this.logoutCallback = logoutCallback;
        bind();
    }
    public void start() {
        view.setUserInfo(session.userId(), session.loginId(), session.role());
    }
    private void bind() {
        view.onBackToProjectList(this::backToProjectList);
        view.onLogout(this::logout);
    }

    private void backToProjectList() {
        backToProjectListCallback.run();
    }

    private void logout() {
        authService.logout();
        session.logout();
        logoutCallback.run();
    }
}
