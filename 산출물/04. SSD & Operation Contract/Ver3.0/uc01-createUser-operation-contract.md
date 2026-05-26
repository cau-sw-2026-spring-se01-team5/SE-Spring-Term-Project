# UC01 Operation Contract - createUser

Operation: `createUser(requesterID, loginID, password, role, projectID)`

Cross References: UC01 프로젝트 및 계정 관리

Preconditions:
- `requesterID`에 해당하는 사용자가 존재한다.
- 요청 사용자의 역할이 `ADMIN`이다.
- `projectID`에 해당하는 프로젝트가 존재한다.
- `loginID`, `password`, `role`이 입력되어 있다.
- 같은 `loginID`를 가진 사용자가 존재하지 않는다.

Postconditions:
- `User` 인스턴스 `u`가 생성되었다. (instance creation)
- `u.loginId`가 입력된 `loginID`로 설정되었다. (attribute modification)
- `u.password`가 입력된 `password`로 설정되었다. (attribute modification)
- `u.role`이 입력된 `role`로 설정되었다. (attribute modification)
- `u.id`가 새 사용자 식별자로 부여되었다. (attribute modification)
- `u`가 `projectID`에 해당하는 `Project`와 연결되었다. (association formed)
