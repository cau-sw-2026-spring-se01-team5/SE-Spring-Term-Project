package mock;

import enums.user.v1.UserRole;
import mock.model.MockUserData;
import user.v1.RoleResolver;

public class MockRoleResolver implements RoleResolver {

    private final MockDatabase database;

    public MockRoleResolver(MockDatabase database) {
        this.database = database;
    }

    @Override
    public UserRole resolveRole(Integer userId) {
        MockUserData user = database.users().get(userId);
        return user == null ? null : user.role();
    }

    @Override
    public String resolveLoginId(Integer userId) {
        MockUserData user = database.users().get(userId);
        return user == null ? null : user.loginId();
    }
}