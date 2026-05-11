# UC03 Operation Contract (UC03 메인 시나리오)

Operation: `assignIssue(issueId: ID)`  

Cross References: UC03 이슈 배정

Preconditions:
- PL이 인증된 상태였다.
- `issueId`에 해당하는 `Issue`가 존재했다.
- 대상 `Issue`가 배정 가능한 상태(`new`)였다.

Postconditions:
- (opt) PL이 추천을 요청한 경우, `AssigneeRecommendation`이 `issueId` 기반 후보 목록을 반환하였다.
- PL이 선택한 `assigneeId`가 대상 `Issue.assignee`로 반영되었다 (attribute modification).
- 대상 `Issue`가 선택된 `Developer`와 연관되었다 (association formed).
- PL 입력 코멘트가 있을 경우 `Comment` 인스턴스 `c`가 생성되었다 (instance creation).
- `c.text`가 입력 코멘트가 되었다 (attribute modification).
- `c.author`가 현재 로그인한 PL이 되었다 (attribute modification).
- `c`가 대상 `Issue`와 연관되었다 (association formed).
- 대상 `Issue.state`가 `assigned`가 되었다 (attribute modification).
- `issueAssigned()` 결과가 반환되었다.
- `displayIssueAssigned()` 결과가 표시되었다.
