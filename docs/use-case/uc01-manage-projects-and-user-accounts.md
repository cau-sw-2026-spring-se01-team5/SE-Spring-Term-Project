# Use Case UC1

## Use Case Name
Manage Projects and User Accounts (프로젝트 및 사용자 계정 관리하기)

## Scope
이슈 관리 시스템

## Level
user-goal

## Primary Actor
Admin

## Stakeholders and Interests
- **Admin:** 프로젝트/계정을 생성하고 분배하고 싶다.
- **PL/Dev/Tester:** 역할에 맞는 권한으로 기능을 사용하고 싶다.
- **조직(Offstage):** 권한 정책이 일관되게 유지되길 원한다.

## Preconditions
- Admin이 인증된 상태다.

## Success Guarantee
- 프로젝트가 생성된다.
- 역할이 포함된 계정이 저장된다.

## Main Success Scenario
1. Admin이 프로젝트 정보를 제출한다.
2. System이 프로젝트를 생성해 저장한다.
3. Admin이 계정과 역할 정보를 제출한다.
4. System이 계정을 생성하고 역할을 부여해 저장한다.

## Extensions
*1a. 프로젝트 식별자가 중복된다.
1. System이 생성을 거절한다.
2. Admin이 프로젝트 식별자를 수정한다.

*3a. 사용자 식별자가 중복된다.
1. System이 생성을 거절한다.
2. Admin이 사용자 식별자를 수정한다.

## Special Requirements
- 관리 기능은 Admin 권한으로만 수행된다.

## Technology and Data Variations List
- 저장소는 파일 시스템 또는 DBMS일 수 있다.

## Frequency of Occurrence
낮음(초기 세팅/인력 변경 시에만).

## Miscellaneous
- 데모 계정 세트(`PL1`, `PL2`, `dev1~10`, `tester1~5`)를 준비한다.
