package app;

import auth.LoginController;
import auth.LoginPanel;
import auth.v1.Auth;
import issue.v1.Issue;
import main.MainController;
import main.MainPanel;
import mock.*;
import project.v1.Project;
import projectselect.ProjectSelectController;
import projectselect.ProjectSelectPanel;
import session.UserSession;
import user.v1.User;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MockDatabase database = new MockDatabase();

            Auth auth = new MockAuth(database);
            Project project = new MockProject(database);
            User user = new MockUser(database);
            Issue issue = new MockIssue(database);

            MockRoleResolver roleResolver = new MockRoleResolver(database);

            UserSession session = new UserSession();
            AppFrame frame = new AppFrame();
            final Runnable[] showLoginRef = new Runnable[1];
            final Runnable[] showProjectSelectRef = new Runnable[1];

            Runnable showLogin = () -> {
                session.logout();

                LoginPanel loginPanel = new LoginPanel();

                loginPanel.setLoginSuccessHandler(userId -> {
                    session.login(
                            userId,
                            roleResolver.resolveLoginId(userId),
                            roleResolver.resolveRole(userId)
                    );

                    Runnable showProjectSelect = () -> {
                        ProjectSelectPanel selectPanel = new ProjectSelectPanel();
                        ProjectSelectController selectController = new ProjectSelectController(
                                selectPanel,
                                project,
                                auth,
                                session,
                                () -> {
                                    MainPanel mainPanel = new MainPanel();

                                    MainController mainController = new MainController(
                                            mainPanel.headerPanel(),
                                            mainPanel.userPanel(),
                                            mainPanel.issuePanel(),

                                            project,
                                            user,
                                            issue,
                                            auth,

                                            session,
                                            showProjectSelectRef[0],
                                            showLoginRef[0]
                                    );

                                    mainController.start();
                                    frame.showMain(mainPanel);
                                },
                                showLoginRef[0]
                        );

                        selectController.start();
                        frame.showProjectSelect(selectPanel);
                    };
                    showProjectSelectRef[0] = showProjectSelect;
                    showProjectSelect.run();
                });

                new LoginController(loginPanel, auth);

                frame.showLogin(loginPanel);
            };
            showLoginRef[0] = showLogin;

            showLogin.run();

            frame.setVisible(true);
        });
    }
}
