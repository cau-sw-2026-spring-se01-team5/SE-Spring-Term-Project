package session;

import enums.user.v1.UserRole;

/* 현재 로그인 사용자와 현재 선택한 프로젝트를 저장하는 객체 */
/* 프론트 쪽 세션 저장소 */
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
        return userId != null;
    }
}
