package main;

import auth.v1.Auth;
import issue.v1.Issue;
import main.header.HeaderController;
import main.header.HeaderView;
import main.issue.IssueController;
import main.issue.IssueView;
import main.user.UserController;
import main.user.UserView;
import project.v1.Project;
import session.UserSession;
import user.v1.User;

// main화면에 필요한 개별 controller를 모두 합침
// header, issue, user controller 관리
public class MainController {

    private final HeaderController headerController;
    private final UserController userController;
    private final IssueController issueController;

    public MainController(
            HeaderView headerView,
            UserView userView,
            IssueView issueView,

            Project projectService,
            User userService,
            Issue issueService,
            Auth authService,

            UserSession session,
            Runnable backToProjectListCallback,
            Runnable logoutCallback
    ) {
        this.headerController = new HeaderController(
                headerView,
                authService,
                session,
                backToProjectListCallback,
                logoutCallback
        );

        this.userController = new UserController(
                userView,
                userService,
                session
        );

        this.issueController = new IssueController(
                issueView,
                issueService,
                userService,
                session
        );
    }

    // 각 기능 시작
    public void start() {
        userController.applyRole();
        issueController.applyRole();

        headerController.start();
        userController.refreshUsers();
        issueController.loadAllIssues();
    }
}
