package session;

import enums.user.v1.UserRole;

/*
 * 현재 로그인한 사용자 정보를 보관하는 JavaFX 세션 객체이다.
 *
 * Swing 구조의 UserSession과 같은 역할이다.
 * 화면마다 로그인 ID와 역할을 따로 넘겨 다니면 흐름이 복잡해지므로,
 * AppController가 로그인 성공 시 세션에 저장하고 이후 화면들이 이 값을 기준으로 동작한다.
 */
public class UserSession {

    private Integer userId;
    private String loginId;
    private UserRole role;
    private Integer selectedProjectId;
    private String selectedProjectTitle;

    public void login(Integer userId, String loginId, UserRole role) {
        this.userId = userId;
        this.loginId = loginId;
        this.role = role;
    }

    public void logout() {
        this.userId = null;
        this.loginId = null;
        this.role = null;
        this.selectedProjectId = null;
        this.selectedProjectTitle = null;
    }

    public Integer userId() {
        return userId;
    }

    public String loginId() {
        return loginId;
    }

    public UserRole role() {
        return role;
    }

    public void selectProject(Integer projectId, String projectTitle) {
        this.selectedProjectId = projectId;
        this.selectedProjectTitle = projectTitle;
    }

    public Integer selectedProjectId() {
        return selectedProjectId;
    }

    public String selectedProjectTitle() {
        return selectedProjectTitle;
    }

    public boolean isLoggedIn() {
        return loginId != null && role != null;
    }
}
