package main;

import auth.v1.Auth;
import issue.v1.Issue;
import main.header.HeaderController;
import main.header.HeaderView;
import main.issue.IssueController;
import main.issue.IssueView;
import main.project.ProjectController;
import main.project.ProjectView;
import main.user.UserController;
import main.user.UserView;
import project.v1.Project;
import session.UserSession;
import user.v1.User;

public class MainController {

    private final HeaderController headerController;
    private final ProjectController projectController;
    private final UserController userController;
    private final IssueController issueController;

    public MainController(
            HeaderView headerView,
            ProjectView projectView,
            UserView userView,
            IssueView issueView,

            Project projectService,
            User userService,
            Issue issueService,
            Auth authService,

            UserSession session,
            Runnable logoutCallback
    ) {
        this.headerController = new HeaderController(
                headerView,
                projectService,
                authService,
                session,
                logoutCallback
        );

        this.projectController = new ProjectController(
                projectView,
                projectService,
                session,
                headerController
        );

        this.userController = new UserController(
                userView,
                userService,
                session,
                headerController
        );

        this.issueController = new IssueController(
                issueView,
                issueService,
                session,
                headerController
        );

        this.headerController.setProjectSelectedCallback(() -> {
            userController.refreshUsers();
            issueController.searchIssues();
        });
    }

    public void start() {
        projectController.applyRole();
        userController.applyRole();
        issueController.applyRole();

        headerController.start();
    }
}