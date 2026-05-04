# UC01 Operation Contract (메인 시나리오)

Operation: `manageProjectAndAccounts(projectInfo: ProjectInfo, accountInfos: AccountInfo[])`  
Cross References: Use Cases: UC01 프로젝트·계정 관리  
Preconditions:
- Admin이 인증된 상태였다.
- `projectInfo`와 `accountInfos`가 필수 항목을 포함하고 있었다.
Postconditions:
- `Project` 인스턴스 `p`가 생성되었다 (instance creation / attribute modification).
- `p.projectId`, `p.name`, `p.description`이 `projectInfo` 값으로 반영되었다 (attribute modification).
- 입력된 계정마다 `Account` 인스턴스가 생성되거나 갱신되었다 (instance creation / attribute modification).
- 각 `Account.role`이 입력 역할(`admin`, `PL`, `dev`, `tester`)로 반영되었다 (attribute modification).
- 각 `Account`가 대상 `Project`와 연관되었다 (association formed).
