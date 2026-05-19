package session;

import enums.user.v1.UserRole;

/* userId 기반으로 사용자 정보 조회 인터페이스 */
public interface RoleResolver {

    UserRole resolveRole(Integer userId);

    String resolveLoginId(Integer userId);
}