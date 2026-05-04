# UC02 Operation Contract (메인 시나리오)

Operation: `registerIssueWithComment(issueData: IssueData, commentText: String)`  
Cross References: Use Cases: UC02 이슈 등록  
Preconditions:
- Tester가 인증된 상태였다.
- Tester가 대상 프로젝트에서 이슈 등록 권한을 가지고 있었다.
- `issueData.title`, `issueData.description`이 비어 있지 않았다.
Postconditions:
- `Issue` 인스턴스 `i`가 생성되었다 (instance creation).
- `i.title`, `i.description`이 입력값으로 반영되었다 (attribute modification).
- `i.reporter`가 현재 로그인한 tester가 되었다 (attribute modification).
- `i.reportedDate`가 현재 시스템 시각이 되었다 (attribute modification).
- `i.state`가 `new`가 되었다 (attribute modification).
- `Comment` 인스턴스 `c`가 생성되었다 (instance creation).
- `c.text`가 `commentText`가 되었다 (attribute modification).
- `c.author`가 현재 로그인한 tester가 되었다 (attribute modification).
- `c`가 `i`와 연관되었다 (association formed).
