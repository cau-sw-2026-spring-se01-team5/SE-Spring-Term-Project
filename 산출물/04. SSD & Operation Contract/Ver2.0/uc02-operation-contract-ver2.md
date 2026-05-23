# UC02 Operation Contract (UC02 메인 시나리오)

Operation: `registerIssue(issueData: IssueData, commentText: String)`  

Cross References: UC02 이슈 등록

Preconditions:
- Tester가 인증된 상태였다.
- Tester가 해당 프로젝트의 이슈 등록 권한을 가지고 있었다.
- issueData.title 또는 issueData.description이 비어져 있지 않았다.

Postconditions:
- Issue의 인스턴스 i가 생성되었다.
- i.title과 i.description이 반영되고, i.reporter과 i.reportedDate는 각각 로그인한 tester의 아이디와 현재 시스템 시각으로 반영되었다.
- i.state가 new로 저장되었다.
- Comment의 인스턴스 c가 생성되고 c.text는 commnetText, c.author가 로그인한 tester가 되었다.
- C는 현재 i와 연결되었다.
- issueRegistered(issueId)의 결과값이 반환되었다.

