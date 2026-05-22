package main.header;

import enums.user.v1.UserRole;

/*
 * 메인 메뉴의 사용자 표시와 버튼 이벤트 연결을 담당하는 Controller이다.
 *
 * Panel은 화면만 만들고, Controller가 화면 전환 콜백을 연결한다.
 * 이 구조는 Swing의 HeaderController와 같은 의도를 가진다.
 */
public class HeaderController {

    private final HeaderView view;
    private final Runnable dashboardCallback;
    private final Runnable issueCallback;
    private final Runnable projectCallback;
    private final Runnable logoutCallback;

    public HeaderController(
            HeaderView view,
            Runnable dashboardCallback,
            Runnable issueCallback,
            Runnable projectCallback,
            Runnable logoutCallback
    ) {
        this.view = view;
        this.dashboardCallback = dashboardCallback;
        this.issueCallback = issueCallback;
        this.projectCallback = projectCallback;
        this.logoutCallback = logoutCallback;
    }

    public void start(String loginId, UserRole role) {
        view.setUserInfo(loginId, role);
        view.onDashboard(dashboardCallback);
        view.onIssue(issueCallback);
        view.onProject(projectCallback);
        view.onLogout(logoutCallback);
    }
}
