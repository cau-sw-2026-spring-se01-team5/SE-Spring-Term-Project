package app;

import auth.LoginController;
import auth.LoginPanel;
import auth.v1.Auth;
import issue.v1.Issue;
import main.MainController;
import main.MainPanel;
import mock.*;
import project.v1.Project;
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

            Runnable showLogin = () -> {
                session.logout();

                LoginPanel loginPanel = new LoginPanel();

                loginPanel.setLoginSuccessHandler(userId -> {
                    session.login(
                            userId,
                            roleResolver.resolveLoginId(userId),
                            roleResolver.resolveRole(userId)
                    );

                    MainPanel mainPanel = new MainPanel();

                    MainController mainController = new MainController(
                            mainPanel.headerPanel(),
                            mainPanel.projectPanel(),
                            mainPanel.userPanel(),
                            mainPanel.issuePanel(),

                            project,
                            user,
                            issue,
                            auth,

                            session,
                            () -> frame.showLogin(loginPanel)
                    );

                    mainController.start();

                    frame.showMain(mainPanel);
                });

                new LoginController(loginPanel, auth);

                frame.showLogin(loginPanel);
            };

            showLogin.run();

            frame.setVisible(true);
        });
    }
}