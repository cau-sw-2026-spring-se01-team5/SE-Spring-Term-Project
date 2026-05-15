package session;

import enums.user.v1.UserRole;

public interface RoleResolver {

    UserRole resolveRole(Integer userId);

    String resolveLoginId(Integer userId);
}