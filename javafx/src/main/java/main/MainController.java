package main;

import app.JavaFxServices;
import main.dashboard.DashboardPanel;
import main.header.HeaderController;
import main.issue.IssueController;
import main.issue.IssuePanel;
import main.project.ProjectController;
import main.project.ProjectPanel;
import session.UserSession;

public class MainController {

    private final MainPanel mainPanel;
    private final JavaFxServices services;
    private final UserSession session;
    private final Runnable logoutCallback;

    public MainController(MainPanel mainPanel, JavaFxServices services, UserSession session, Runnable logoutCallback) {
        this.mainPanel = mainPanel;
        this.services = services;
        this.session = session;
        this.logoutCallback = logoutCallback;
    }

    public void start() {
        HeaderController headerController = new HeaderController(
                mainPanel.headerPanel(),
                this::showDashboard,
                this::showIssues,
                this::showProjects,
                logoutCallback
        );

        headerController.start(session.loginId(), session.role());

        if (session.selectedProjectId() == null) {
            showProjects();
            return;
        }
        showDashboard();
    }

    private void showDashboard() {
        mainPanel.setContent(new DashboardPanel(services, session, this::showIssues, this::showProjects));
    }

    private void showIssues() {
        IssuePanel issuePanel = new IssuePanel(session.role(), session.loginId());
        IssueController issueController = new IssueController(issuePanel, services, session);
        issueController.start();
        mainPanel.setContent(issuePanel);
    }

    private void showProjects() {
        ProjectPanel projectPanel = new ProjectPanel(session.role());
        ProjectController projectController = new ProjectController(projectPanel, services, session, this::showDashboard);
        projectController.start();
        mainPanel.setContent(projectPanel);
    }
}
