# UC03 Operation Contract (메인 시나리오)

Operation: `assignIssueWithComment(issueId: ID, assigneeId: ID, commentText: String)`  
Cross References: Use Cases: UC03 이슈 배정  
Preconditions:
- PL이 인증된 상태였다.
- `issueId`에 해당하는 `Issue`가 존재했다.
- `assigneeId`에 해당하는 `Developer`가 존재했다.
- 상태 전이 정책이 `new -> assigned`를 허용했다.
Postconditions:
- 대상 `Issue.assignee`가 `assigneeId`가 되었다 (attribute modification).
- 대상 `Issue`가 선택된 `Developer`와 연관되었다 (association formed).
- `Comment` 인스턴스 `c`가 생성되었다 (instance creation).
- `c.text`가 `commentText`가 되었다 (attribute modification).
- `c.author`가 현재 로그인한 PL이 되었다 (attribute modification).
- `c`가 대상 `Issue`와 연관되었다 (association formed).
- 대상 `Issue.state`가 `assigned`가 되었다 (attribute modification).
