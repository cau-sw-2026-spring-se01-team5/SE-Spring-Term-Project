package main.issue.role;

import enums.user.v1.UserRole;

/* role별 이슈 화면 정책 공통 인터페이스 */
public interface IssuePolicy {

    // 이 정책이 어떤 role을 처리할 수 있는지 -> 현재 role에 맞는 정책 선택 위함
    // Strategy Pattern 구현한 부분 -> role에 따라 다른 정책 객체 선택 가능 -> 행동을 객체로 캡슐화
    boolean supports(UserRole role);

    // 실제 정책 적용 메서드
    void apply(IssueActionView view);
}
