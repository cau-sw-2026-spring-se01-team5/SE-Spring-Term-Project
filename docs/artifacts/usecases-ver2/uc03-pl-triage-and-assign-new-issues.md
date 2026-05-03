# Use Case UC3

## 유스케이스 이름
이슈 배정

## Scope
이슈 관리 시스템

## Level
user-goal

## Primary Actor
PL

## Stakeholders and Interests
- **PL:** new 상태 이슈를 찾아 담당 dev를 정하고 흐름을 assigned로 넘기고 싶다.
- **dev:** 자신에게 배정된 이슈만 골라 작업할 수 있게 하고 싶다.

## Preconditions
- PL이 인증되어 있다.
- 대상 프로젝트에 이슈가 존재할 수 있다.

## Success Guarantee
- 선택한 이슈에 assignee가 기록되고, 필요 시 코멘트가 남는다.
- 정책이 허용하면 이슈 상태가 `assigned`로 바뀐다.

## Main Success Scenario
1. PL이 이슈 브라우즈(UC7)를 include하여 new 상태 이슈를 검색·목록·상세로 확인한다.
2. (선택) PL이 담당자 추천이 필요하면 담당자 후보 추천(UC9)을 **extend**로 실행해 후보를 참고한다.
3. PL이 tester1이 reporter인 이슈 등 대상에 대해 assignee(예: dev1)를 지정한다.
4. PL이 코멘트 추가(UC8)를 include하여 배정에 맞는 코멘트를 남긴다.
5. System이 이슈 상태를 `assigned`로 반영한다.

## Extensions
*3a. 전이 또는 권한이 허용되지 않는다.
1. System이 거절 사유를 알린다.

## Miscellaneous
- 과제 **항목 3** 시나리오에 해당한다.
