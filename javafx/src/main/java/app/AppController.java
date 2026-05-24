package app;

import auth.LoginController;
import auth.LoginPanel;
import backend.JavaFxBackend;
import main.MainController;
import main.MainPanel;
import main.project.ProjectController;
import main.project.ProjectPanel;
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
        /*
         * 애플리케이션 시작 시에는 항상 로그인 화면부터 보여준다.
         * 로그인 여부와 관계없이 시작 화면을 한 곳에서 결정하기 위해 AppController가 담당한다.
         */
        showLoginScreen();
    }

    private void showLoginScreen() {
        /*
         * 로그인 화면으로 돌아올 때 기존 사용자 정보를 지운다.
         * 로그아웃 후 다른 사용자로 다시 로그인할 수 있게 하기 위한 처리이다.
         */
        session.logout();

        /*
         * LoginPanel은 아이디/비밀번호 입력 UI만 담당한다.
         * LoginController는 입력값을 읽고 backend.login()을 호출한다.
         */
        LoginPanel loginPanel = new LoginPanel();

        /*
         * 로그인 성공 시 실행할 콜백을 Controller에 넘긴다.
         * 이 콜백은 세션에 사용자 정보를 저장하고 메인 화면으로 이동한다.
         * 화면 전환 책임을 LoginPanel이 아니라 AppController가 가지도록 한 부분이다.
         */
        new LoginController(loginPanel, backend, user -> {
            session.login(user.loginId(), user.role());
            showProjectSelectScreen();
        });

        frame.showLogin(loginPanel);
    }

    private void showProjectSelectScreen() {
        ProjectPanel projectPanel = new ProjectPanel(session.role(), false, true);
        ProjectController projectController = new ProjectController(
                projectPanel,
                backend,
                session,
                this::showMainScreen
        );

        projectController.start();
        frame.showMain(projectPanel);
    }

    private void showMainScreen() {
        /*
         * MainPanel은 메인 화면의 틀이다.
         * 왼쪽 메뉴와 가운데 본문 영역만 가지고, 어떤 기능 화면을 보여줄지는 MainController가 결정한다.
         */
        MainPanel mainPanel = new MainPanel();

        /*
         * MainController에는 같은 backend와 session을 전달한다.
         * 이렇게 해야 대시보드, 이슈, 프로젝트 화면이 모두 같은 로그인 사용자와 같은 데이터 흐름을 공유한다.
         */
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
