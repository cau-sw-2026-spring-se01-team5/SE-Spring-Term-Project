package app;

import auth.LoginController;
import auth.LoginPanel;
import main.MainController;
import main.MainPanel;
import main.project.ProjectController;
import main.project.ProjectPanel;
import session.UserSession;

public class AppController {

    private final AppFrame frame;
    private final UserSession session;
    private final JavaFxServices services;

    public AppController(AppFrame frame, UserSession session, JavaFxServices services) {
        this.frame = frame;
        this.session = session;
        this.services = services;
    }

    public void start() {
        showLoginScreen();
    }

    private void showLoginScreen() {
        session.logout();

        LoginPanel loginPanel = new LoginPanel();
        new LoginController(loginPanel, services, user -> {
            session.login(user.userId(), user.loginId(), user.role());
            showProjectSelectScreen();
        });

        frame.showLogin(loginPanel);
    }

    private void showProjectSelectScreen() {
        ProjectPanel projectPanel = new ProjectPanel(session.role(), false, true);
        ProjectController projectController = new ProjectController(
                projectPanel,
                services,
                session,
                this::showMainScreen
        );

        projectController.start();
        frame.showMain(projectPanel);
    }

    private void showMainScreen() {
        MainPanel mainPanel = new MainPanel();
        MainController mainController = new MainController(
                mainPanel,
                services,
                session,
                this::showLoginScreen
        );

        mainController.start();
        frame.showMain(mainPanel);
    }
}
