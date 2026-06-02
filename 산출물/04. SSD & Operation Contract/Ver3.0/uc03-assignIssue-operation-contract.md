# UC03 Operation Contract - assignIssue

Operation: `assignIssue(issueID, assigneeID)`

Cross References: UC03 이슈 배정

Preconditions:
- PL이 인증된 상태이다.
- Issue의 status가 New 또는 Reopened이다.
- assigneeID에 해당하는 사용자의 역할이 Dev이다.


Postconditions:
- 대상 Issue 인스턴스 i의 assigneeId가 입력된 assigneeID로 설정되며, i가 담당하는 dev와 연결되었다.
- i.status가 Assigend로 설정되었다.

