package app;

import auth.LoginController;
import auth.LoginPanel;
import auth.v1.Auth;
import issue.v1.Issue;
import main.MainController;
import main.MainPanel;
import project.v1.Project;
import projectselect.ProjectSelectController;
import projectselect.ProjectSelectPanel;

import user.v1.RoleResolver;

import session.UserSession;
import user.dto.getUserInfo.v1.GetUserInfoInput;
import user.dto.getUserInfo.v1.GetUserInfoOutput;
import user.v1.User;

/* 화면 전환 담당 */
/* 로그인 화면 -> 프로젝트 선택 화면 -> 메인 화면 등등의 화면 전환 담당 */
public class AppController {

    private final AppFrame frame;
    private final UserSession session;
    private final Auth authService;
    private final Project projectService;
    private final User userService;
    private final Issue issueService;

    // 외부(Main)에서 필요한 객체들을 주입 받음
    // 객체 생성 책임을 분리
    public AppController(
            AppFrame frame,
            UserSession session,
            Auth authService,
            Project projectService,
            User userService,
            Issue issueService
    ) {
        this.frame = frame;
        this.session = session;
        this.authService = authService;
        this.projectService = projectService;
        this.userService = userService;
        this.issueService = issueService;
    }

    // 첫 로그인 화면 진입
    public void start() {
        showLoginScreen();
    }

    // 로그인 화면 출력
    private void showLoginScreen() {
        // 기존 로그인 되어 있다면 로그아웃
        session.logout();

        // 로그인 화면 UI 패널 생성
        LoginPanel loginPanel = new LoginPanel();

        // 로그인 성공시 onLoginSuccess 메서드 실행하도록 메서드 등록
        loginPanel.setLoginSuccessHandler(this::onLoginSuccess);

        // 로그인 이벤트 처리 담당 Controller 생성
        // 이때 UI랑 service 객체를 전달
        new LoginController(loginPanel, authService);

        // 로그인 화면 출력
        frame.showLogin(loginPanel);
    }

    // 로그인 성공 메서드
    private void onLoginSuccess(Integer userId) {
        GetUserInfoOutput userInfo = userService.getUserInfo(new GetUserInfoInput(userId, null));

        // 로그인한 계정의 정보를 session에 저장
        session.login(
                userInfo != null && userInfo.success() ? userInfo.userId() : userId,
                userInfo != null && userInfo.success() ? userInfo.loginId() : null,
                userInfo != null && userInfo.success() ? userInfo.role() : null
        );
        // 프로젝트 선택 화면으로 이동
        showProjectSelectScreen();
    }

    // 프로젝트 선택 화면으로 이동 메서드
    private void showProjectSelectScreen() {
        // 프로젝트 선택 화면의 UI 패널과 controller 생성
        ProjectSelectPanel selectPanel = new ProjectSelectPanel();
        ProjectSelectController controller = new ProjectSelectController(
                selectPanel,
                projectService,
                authService,
                session,
                this::showMainScreen, // 프로젝트 선택 화면에서 메인 화면 이동 위한 메서드 주입
                this::showLoginScreen // 프로젝트 선택 화면에서 로그인 화면으로 이동 위한 메서드 주입
        );

        controller.start();
        frame.showProjectSelect(selectPanel);
    }

    // 메인 화면 이동 메서드
    private void showMainScreen() {
        // 메인 화면 UI 패널 및 컨트롤러 생성
        MainPanel mainPanel = new MainPanel();
        MainController controller = new MainController(
                mainPanel.headerPanel(),
                mainPanel.userPanel(),
                mainPanel.issuePanel(),
                projectService,
                userService,
                issueService,
                authService,
                session,
                this::showProjectSelectScreen,
                this::showLoginScreen
        );

        controller.start();
        frame.showMain(mainPanel);
    }
}
