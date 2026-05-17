package main.header;

import auth.v1.Auth;
import session.UserSession;

/* 메인 화면의 상단 헤더 바의 이벤트 흐름을 담당 */
public class HeaderController {
    private final HeaderView view; // 헤더 view 인터페이스 가져옴
    private final Auth authService; // 로그아웃 기능 처리 위해서
    private final UserSession session; // 로그인 정보 가져오기 위함
    private final Runnable backToProjectList; // 프로젝트 리스트 화면으로 이동 함수
    private final Runnable logout; // 로그아웃 함수

    public HeaderController(
            HeaderView view,
            Auth authService,
            UserSession session,
            Runnable backToProjectList,
            Runnable logout
    ) {
        this.view = view;
        this.authService = authService;
        this.session = session;
        this.backToProjectList = backToProjectList;
        this.logout = logout;
        bind();
    }

    // header 초기화 메서드
    public void start() {
        view.setUserInfo(session.userId(), session.loginId(), session.role()); // 현재 로그인 사용자 정보 출력
    }

    // 이벤트 연결 메서드
    private void bind() {
        view.onBackToProjectList(this::backToProjectList);
        view.onLogout(this::logout);
    }

    private void backToProjectList() {
        backToProjectList.run();
    }

    private void logout() {
        authService.logout();
        session.logout();
        logout.run();
    }
}
