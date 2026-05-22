package session;

import enums.user.v1.UserRole;

/*
 * 사용자 번호를 화면에서 필요한 로그인 ID와 역할로 바꾸는 역할 조회 인터페이스이다.
 *
 * Swing 구조와 같은 방식으로 사용자 역할 조회를 분리할 때 사용할 수 있는 최소 인터페이스이다.
 * 현재 실행 흐름에서는 직접 쓰지 않지만, session 패키지의 역할을 명확히 하기 위해 남겨둔다.
 */
public interface RoleResolver {

    UserRole resolveRole(Integer userId);

    String resolveLoginId(Integer userId);
}
