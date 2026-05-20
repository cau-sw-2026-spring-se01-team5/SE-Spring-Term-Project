package main;

import app.JavaFxBackend;
import main.dashboard.DashboardPanel;
import main.issue.IssuePanel;
import main.project.ProjectPanel;
import session.UserSession;

/*
 * 메인 화면의 하위 기능 전환을 담당하는 컨트롤러이다.
 *
 * Swing의 MainController처럼 여러 기능 화면을 묶어서 시작하고,
 * 사이드바 버튼 이벤트를 각 기능 패널로 연결한다.
 */
public class MainController {

    private final MainPanel mainPanel;
    private final JavaFxBackend backend;
    private final UserSession session;
    private final Runnable logoutCallback;

    public MainController(MainPanel mainPanel, JavaFxBackend backend, UserSession session, Runnable logoutCallback) {
        this.mainPanel = mainPanel;
        this.backend = backend;
        this.session = session;
        this.logoutCallback = logoutCallback;
        bind();
    }

    public void start() {
        mainPanel.setUserInfo(session.loginId(), session.role());
        showDashboard();
    }

    private void bind() {
        mainPanel.onDashboard(this::showDashboard);
        mainPanel.onIssue(this::showIssues);
        mainPanel.onProject(this::showProjects);
        mainPanel.onLogout(logoutCallback);
    }

    private void showDashboard() {
        mainPanel.setContent(new DashboardPanel(backend, session, this::showIssues, this::showProjects));
    }

    private void showIssues() {
        mainPanel.setContent(new IssuePanel(backend, session));
    }

    private void showProjects() {
        mainPanel.setContent(new ProjectPanel(backend, session));
    }
}
