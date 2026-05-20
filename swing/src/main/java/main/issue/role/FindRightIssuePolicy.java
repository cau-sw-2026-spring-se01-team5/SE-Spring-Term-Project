package main.issue.role;

import enums.user.v1.UserRole;

import java.util.List;

/* role에 맞는 policy객체 찾아서 연결하는 역할 */
/* role별 if-else 구조를 줄이기 위함 */
public class FindRightIssuePolicy {

    private final List<IssuePolicy> policies; // 지금 존재하는 모든 정책 객체 저장 리스트
    private final IssuePolicy fallbackPolicy; // 예외처리용 -> 기본 디폴트 객체

    public FindRightIssuePolicy() {
        // 정책 초기화 -> 현재 사용할 정책 객체들 등록
        this.policies = List.of(
                new AdminIssuePolicy(),
                new PlIssuePolicy(),
                new DevIssuePolicy(),
                new TesterIssuePolicy()
        );
        this.fallbackPolicy = new DefaultIssuePolicy(); // 예외처리용 -> 기본 디폴트 객체
    }

    // 현재 role에 맞는 정책 객체 찾아서 매칭
    public IssuePolicy find(UserRole role) {
        for (IssuePolicy policy : policies) {
            if (policy.supports(role)) {
                return policy;
            }
        }
        return fallbackPolicy;
    }
}
