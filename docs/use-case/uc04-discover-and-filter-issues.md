# Use Case UC4

## Use Case Name
Discover and Filter Issues (이슈 브라우즈)

## Scope
이슈 관리 시스템

## Level
user-goal

## Primary Actor
PL / Dev / Tester

## Stakeholders and Interests
- **PL:** 상태/담당자 기준으로 이슈를 분류해 관리하고 싶다.
- **Dev:** 자신에게 배정된 이슈만 빠르게 보고 싶다.
- **Tester:** 자신이 등록한 이슈의 처리 상태를 추적하고 싶다.

## Preconditions
- 사용자가 인증되어 있다.
- 프로젝트가 선택되어 있다.

## Success Guarantee
- 조건에 맞는 이슈 목록이 제공된다(0건 포함).
- Actor는 후속 처리(상세 확인/수정)에 사용할 대상 이슈를 선택할 수 있다.

## Main Success Scenario
1. Actor가 검색 조건을 제출한다.
2. System이 조건으로 이슈를 조회한다.
3. System이 결과 목록을 제공한다.
4. Actor가 후속 상세 확인/수정을 위해 대상 이슈를 선택한다.

## Extensions
*1a. 검색 조건이 비어 있다.
1. System이 기본 목록을 제공한다.

## Special Requirements
-

## Technology and Data Variations List
- 결과 출력은 테이블/카드 등 다양한 UI 형식을 허용한다.

## Frequency of Occurrence
매우 높음.

## Miscellaneous
- 통계 화면은 동일 이슈 데이터 집계를 사용한다.
- UC6(이슈 상세 확인 및 수정하기)의 일반적인 선행 유스케이스로 사용된다.
