package main;

import backend.JavaFxBackend;
import main.dashboard.DashboardPanel;
import main.header.HeaderController;
import main.issue.IssueController;
import main.issue.IssuePanel;
import main.project.ProjectController;
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
        /*
         * HeaderController는 왼쪽 메뉴 영역의 이벤트 연결만 담당한다.
         * MainController는 각 메뉴가 눌렸을 때 어떤 본문 화면을 보여줄지 콜백으로 넘긴다.
         */
        HeaderController headerController = new HeaderController(
                mainPanel.headerPanel(),
                this::showDashboard,
                this::showIssues,
                this::showProjects,
                logoutCallback
        );

        /*
         * 현재 로그인 사용자 정보를 헤더에 표시한다.
         * 역할에 따라 프로젝트 메뉴 문구도 달라지므로 role도 함께 넘긴다.
         */
        headerController.start(session.loginId(), session.role());

        /*
         * 메인 화면에 처음 들어오면 대시보드를 기본 화면으로 보여준다.
         */
        showDashboard();
    }

    private void bind() {
        /*
         * 현재 MainController의 이벤트 연결은 HeaderController 생성 시 콜백으로 처리된다.
         * Swing 구조와 메서드 형태를 맞추기 위해 bind 메서드를 남겼다.
         */
    }

    private void showDashboard() {
        /*
         * DashboardPanel은 단순 요약 화면이므로 별도 Controller를 두지 않았다.
         * 이슈/프로젝트로 이동하는 콜백만 전달해서 화면 전환은 MainController가 계속 담당한다.
         */
        mainPanel.setContent(new DashboardPanel(backend, session, this::showIssues, this::showProjects));
    }

    private void showIssues() {
        /*
         * 이슈 화면은 기능이 많으므로 Panel과 Controller를 분리했다.
         * IssuePanel은 화면 표시와 입력 수집만 담당하고,
         * IssueController가 backend 호출과 상태 변경 흐름을 담당한다.
         */
        IssuePanel issuePanel = new IssuePanel(session.role(), session.loginId());
        IssueController issueController = new IssueController(issuePanel, backend, session);
        issueController.start();
        mainPanel.setContent(issuePanel);
    }

    private void showProjects() {
        /*
         * 프로젝트/계정 화면도 Panel과 Controller를 분리했다.
         * ProjectPanel은 목록과 다이얼로그를 보여주고,
         * ProjectController가 프로젝트 생성, 계정 생성, 삭제 요청을 처리한다.
         */
        ProjectPanel projectPanel = new ProjectPanel(session.role());
        ProjectController projectController = new ProjectController(projectPanel, backend, session);
        projectController.start();
        mainPanel.setContent(projectPanel);
    }
}
