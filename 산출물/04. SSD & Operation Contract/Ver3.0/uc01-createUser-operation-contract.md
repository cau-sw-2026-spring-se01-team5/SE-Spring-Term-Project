# UC01 Operation Contract - createUser

Operation: `createUser(requesterID, loginID, password, role, projectID)`

Cross References: UC01 프로젝트 및 계정 관리

Preconditions:
- registerID에 해당하는 사용자가 존재한다.
- 해당 사용자의 Role이 Admin이다.

Postconditions:
- User의 인스턴스 u가 생성되었다.
- u.loginId, u.password`, u.role이 입력된 loginID, password, role로 설정되었다.
- u가 projectID이 해당되는 Project와 연결되었다.
