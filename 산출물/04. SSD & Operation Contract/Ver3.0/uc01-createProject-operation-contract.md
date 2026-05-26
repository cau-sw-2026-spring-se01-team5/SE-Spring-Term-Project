# UC01 Operation Contract - createProject

Operation: `createProject(title)`

Cross References: UC01 프로젝트 및 계정 관리

Preconditions:
- Admin이 인증된 상태이다.

Postconditions:
- Project의 인스턴스 p가 생성되었다.
- p.title이 입력된 title로 설정되었다.
- p.id가 새로운 프로젝트 식별자로 부여되었다.

