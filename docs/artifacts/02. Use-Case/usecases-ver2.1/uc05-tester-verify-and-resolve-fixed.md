# Use Case UC5

## 유스케이스 이름
수정된 이슈 확인

## Scope
이슈 관리 시스템

## Level
user-goal

## Primary Actor
Tester

## Stakeholders and Interests
- **tester:** 본인이 보고한 이슈 중 수정 완료된 것을 찾아 검증 결과를 반영하고 싶다.

## Preconditions
- tester가 인증되어 있다.

## Success Guarantee
- 선택한 이슈가 정책이 허용하면 `resolved` 상태로 바뀐다.

## Main Success Scenario
1. tester가 이슈 브라우즈(UC7)를 include하여 **본인이 reporter인 이슈** 중 `fixed` 상태인 이슈를 검색·목록·상세로 확인한다.
2. tester가 적절히 수정된 이슈를 선택하고 상태를 `resolved`로 변경한다.

## Extensions
*2a. 전이가 허용되지 않는다.
1. System이 거절 사유를 알린다.

## Miscellaneous
- 과제 **항목 5** 시나리오에 해당한다.
