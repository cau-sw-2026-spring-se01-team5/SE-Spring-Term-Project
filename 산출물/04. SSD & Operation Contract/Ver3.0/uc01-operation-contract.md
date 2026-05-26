# IC1. Operation Contract (UC01 메인 시나리오)

Operation: `manageProjectAndAccounts(projectInfo: ProjectInfo, accountInfos: AccountInfo[])`  

Cross References: UC01 프로젝트·계정 관리

Preconditions:
- Admin이 인증된 상태였다.

Postconditions:
- Project의 인스턴스 p가 생성되었다.
- P.projectId, P.name, P.description이 주어진 projectInfo 값으로 반영되었다.
- projectCreated(projectId)의 결과값이 반환되었다.
- Loop 구간에서 각 accountInfo마다 적절한 Account 인스턴스가 생성되었다.
- a.role이 입력한 역할로 저장되었다.
- 모든 계정 생성이 완료된 후 projectSetupCompleted() 결과값이 반환되었다.
