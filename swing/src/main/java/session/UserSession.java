package session;

import enums.user.v1.UserRole;

public class UserSession {

    private Integer userId;
    private String loginId;
    private UserRole role;

    public void login(Integer userId, String loginId, UserRole role) {
        this.userId = userId;
        this.loginId = loginId;
        this.role = role;
    }

    public void logout() {
        this.userId = null;
        this.loginId = null;
        this.role = null;
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

    public boolean isLoggedIn() {
        return userId != null;
    }
}