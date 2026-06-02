# UC02 Operation Contract - registerIssue

Operation: `registerIssue(projectID, title, description, priority, reporterID)`

Cross References: UC02 이슈 등록

Preconditions:
- Reporter의 Role이 Tester이다.
- Title과 description이 비어 있지 않다.


Postconditions:
- Issue의 인스턴스 i가 생성되었다.
- i.projectId가 입력된 projectID로 설정되며 i가 해당 프로젝트와 연결되었다.
- i.description, i.priority, i.reporterID가 입력된 description, priority, reporterID로 저장되었다.
- i. reportedDate는 이슈가 등록된 시각으로 설정되었다.
- i.status는 New로 지정되었으며, i.id가 새로운 이슈 식별자로 부여되었다.
