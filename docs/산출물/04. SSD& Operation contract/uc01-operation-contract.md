# IC1. Operation Contract (UC01 메인 시나리오)

Operation: `manageProjectAndAccounts(projectInfo: ProjectInfo, accountInfos: AccountInfo[])`  
Cross References: Use Cases: UC01 프로젝트·계정 관리, SSD `manageProjectAndAccounts`  
Preconditions:
- Admin이 인증된 상태였다.
- `projectInfo`가 프로젝트 생성에 필요한 필수 항목을 포함하고 있었다.
- `accountInfos`가 1개 이상의 계정 정보를 포함하고 있었다.

Postconditions:
- `Project` 인스턴스 `p`가 생성되었다 (instance creation).
- `p.projectId`, `p.name`, `p.description`이 `projectInfo` 값으로 반영되었다 (attribute modification).
- `projectCreated(projectId)` 결과가 반환되었다.
- 반복 구간(loop)에서 각 `accountInfo`마다 `Account` 인스턴스 `a`가 생성되거나 갱신되었다 (instance creation / attribute modification).
- 각 `a.role`이 입력 역할(`admin`, `PL`, `dev`, `tester`)로 반영되었다 (attribute modification).
- 각 `a`가 `p`와 연관되었다 (association formed).
- 모든 계정 반영이 끝난 뒤 `projectSetupCompleted()` 결과가 반환되었다.
