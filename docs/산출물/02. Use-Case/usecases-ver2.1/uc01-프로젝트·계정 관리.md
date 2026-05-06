# Use Case UC1

## 유스케이스 이름
프로젝트·계정 관리

## Scope
이슈 관리 시스템

## Level
user-goal

## Primary Actor
Admin

## Stakeholders and Interests
- **Admin:** 프로젝트를 만들고 admin / PL / dev / tester 계정을 추가·관리하고 싶다.
- **PL / dev / tester:** 데모·운영에 필요한 계정이 미리 준비되길 원한다.

## Preconditions
- Admin이 인증된 상태다.

## Success Guarantee
- 프로젝트가 생성·저장된다.
- 역할이 지정된 계정이 생성·갱신된다.

## Main Success Scenario
1. Admin이 프로젝트 생성·수정에 필요한 정보를 제출한다.
2. System이 프로젝트를 저장한다.
3. Admin이 계정(역할: admin, PL, dev, tester)과 소속 프로젝트 정보를 제출한다.
4. System이 계정을 저장하고 역할을 반영한다.

## Extensions
*1a. 프로젝트 식별자 충돌.
1. System이 거절 사유를 알리고 Admin이 식별자를 수정한다.

## Miscellaneous
- 예제 시나리오: Admin이 project1을 추가하고 PL / dev / tester 계정을 준비한다.
