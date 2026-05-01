# Use Case UC5

## Use Case Name
Validate Issue State Transition (이슈 상태 변경 가능 여부 확인하기)

## Scope
이슈 관리 시스템

## Level
subfunction

## Primary Actor
System

## Stakeholders and Interests
- **PL/Dev/Tester:** 허용된 상태 변경만 수행되길 원한다.
- **품질 조직:** 워크플로우 정책 위반이 없길 원한다.

## Preconditions
- 상태 변경 요청이 발생했다.
- 현재 상태, 요청 상태, 요청자 역할 정보를 시스템이 알고 있다.

## Success Guarantee
- 시스템이 상태 변경 가능 여부와 사유를 결정한다.

## Main Success Scenario
1. System이 현재 상태와 요청 상태를 확인한다.
2. System이 역할 권한과 상태 변경 정책을 대조한다.
3. System이 허용/거부 결과를 반환한다.

## Extensions
*2a. 정책에 없는 상태 변경 요청이다.
1. System이 거부 결과를 반환한다.

*2b. 요청자 권한이 부족하다.
1. System이 거부 결과를 반환한다.

## Special Requirements
- 동일 입력에 대해 동일한 판단을 보장해야 한다.

## Technology and Data Variations List
- 정책 저장소는 코드/설정 파일/DB 중 하나를 사용할 수 있다.

## Frequency of Occurrence
상태 변경 요청마다 1회.

## Miscellaneous
- UC6에 include되는 공통 검증 기능이다.
