package app;

import auth.LoginController;
import auth.LoginPanel;
import main.MainController;
import main.MainPanel;
import session.UserSession;

/*
 * JavaFX 애플리케이션의 최상위 화면 흐름을 담당하는 컨트롤러이다.
 *
 * Swing의 AppController처럼 로그인 화면과 메인 화면 전환을 관리한다.
 * View가 직접 다음 화면을 생성하지 않게 하여 화면 흐름 책임을 한 곳에 모았다.
 */
public class AppController {

    private final AppFrame frame;
    private final UserSession session;
    private final JavaFxBackend backend;

    public AppController(AppFrame frame, UserSession session, JavaFxBackend backend) {
        this.frame = frame;
        this.session = session;
        this.backend = backend;
    }

    public void start() {
        showLoginScreen();
    }

    private void showLoginScreen() {
        session.logout();

        LoginPanel loginPanel = new LoginPanel();
        new LoginController(loginPanel, backend, user -> {
            session.login(user.loginId(), user.role());
            showMainScreen();
        });

        frame.showLogin(loginPanel);
    }

    private void showMainScreen() {
        MainPanel mainPanel = new MainPanel();
        MainController mainController = new MainController(
                mainPanel,
                backend,
                session,
                this::showLoginScreen
        );

        mainController.start();
        frame.showMain(mainPanel);
    }
}
