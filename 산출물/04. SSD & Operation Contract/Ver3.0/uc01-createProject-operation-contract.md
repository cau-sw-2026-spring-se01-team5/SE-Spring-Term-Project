# UC01 Operation Contract - createProject

Operation: `createProject(title)`

Cross References: UC01 프로젝트 및 계정 관리

Preconditions:
- Admin이 인증된 상태이다.
- `title`이 입력되어 있다.
- 같은 이름의 프로젝트가 존재하지 않는다.

Postconditions:
- `Project` 인스턴스 `p`가 생성되었다. (instance creation)
- `p.name`이 입력된 `title`로 설정되었다. (attribute modification)
- `p.id`가 새 프로젝트 식별자로 부여되었다. (attribute modification)
