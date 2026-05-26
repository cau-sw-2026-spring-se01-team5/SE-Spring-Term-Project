# UC03 Operation Contract - assignIssue

Operation: `assignIssue(issueID, assigneeID)`

Cross References: UC03 이슈 배정

Preconditions:
- PL 또는 Admin이 인증된 상태이다.
- `issueID`에 해당하는 이슈가 존재한다.
- 해당 이슈의 상태가 `NEW` 또는 `REOPENED`이다.
- `assigneeID`에 해당하는 사용자가 존재한다.
- Assignee의 역할이 `DEV`이다.

Postconditions:
- 대상 `Issue` 인스턴스 `i`의 `assigneeId`가 입력된 `assigneeID`로 설정되어, `i`가 담당 개발자와 연결되었다. (association formed)
- `i.status`가 `ASSIGNED`로 변경되었다. (attribute modification)
