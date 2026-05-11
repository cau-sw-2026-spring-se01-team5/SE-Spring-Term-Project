# Use Case UC10

## 유스케이스 이름
통계 분석

## Scope
이슈 관리 시스템

## Level
user-goal

## Primary Actor
PL

## Preconditions
- PL이 인증되어 있다.
- 이슈 브라우즈(UC7) 중 통계 분석이 필요해 확장 기능을 선택했다.

## Success Guarantee
- 통계 분석 결과가 제공되거나 실패 사유가 보고된다.

## Main Success Scenario
1. PL이 통계 분석 실행을 선택한다.
2. System이 이슈/처리 이력 데이터를 집계해 통계 지표를 계산한다.
3. System이 완료 또는 오류를 알린다.

## Miscellaneous
- 요청사항 **extend 2번**에 해당한다.
- **이슈 브라우즈(UC7)**를 확장한다.
