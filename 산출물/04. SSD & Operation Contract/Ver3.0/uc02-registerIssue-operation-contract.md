# UC02 Operation Contract - registerIssue

Operation: `registerIssue(projectID, title, description, priority, reporterID)`

Cross References: UC02 이슈 등록

Preconditions:
- `reporterID`에 해당하는 사용자가 존재한다.
- Reporter의 역할이 `TESTER`이다.
- `projectID`에 해당하는 프로젝트가 존재한다.
- `title`과 `description`이 입력되어 있다.

Postconditions:
- `Issue` 인스턴스 `i`가 생성되었다. (instance creation)
- `i.projectId`가 입력된 `projectID`로 설정되어, `i`가 해당 `Project`와 연결되었다. (association formed)
- `i.title`이 입력된 `title`로 설정되었다. (attribute modification)
- `i.description`이 입력된 `description`으로 설정되었다. (attribute modification)
- `i.priority`가 입력된 `priority`로 설정되었다. 단, 값이 없으면 기본 우선순위가 설정되었다. (attribute modification)
- `i.reporterId`가 입력된 `reporterID`로 설정되어, `i`가 Reporter와 연결되었다. (association formed)
- `i.reportedDate`가 이슈 등록 시각으로 설정되었다. (attribute modification)
- `i.status`가 `NEW`로 설정되었다. (attribute modification)
- `i.id`가 새 이슈 식별자로 부여되었다. (attribute modification)
