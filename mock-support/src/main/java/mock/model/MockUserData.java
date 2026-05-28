package mock.model;

import enums.user.v1.UserRole;

public class MockUserData {

    private final Integer userId;
    private final String loginId;
    private final String password;
    private final UserRole role;
    private final Integer projectId;

    public MockUserData(
            Integer userId,
            String loginId,
            String password,
            UserRole role,
            Integer projectId
    ) {
        this.userId = userId;
        this.loginId = loginId;
        this.password = password;
        this.role = role;
        this.projectId = projectId;
    }

    public Integer userId() {
        return userId;
    }

    public String loginId() {
        return loginId;
    }

    public String password() {
        return password;
    }

    public UserRole role() {
        return role;
    }

    public Integer projectId() {
        return projectId;
    }
}