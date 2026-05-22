package main.header;

import enums.user.v1.UserRole;

/*
 * 메인 화면의 공통 메뉴 영역을 표현하는 View 인터페이스이다.
 *
 * Swing의 HeaderView처럼 상위 컨트롤러가 구체 JavaFX 노드를 직접 만지지 않도록,
 * 사용자 정보 표시와 메뉴 이벤트 등록 기능만 외부에 공개한다.
 */
public interface HeaderView {

    void setUserInfo(String loginId, UserRole role);

    void onDashboard(Runnable handler);

    void onIssue(Runnable handler);

    void onProject(Runnable handler);

    void onLogout(Runnable handler);
}
