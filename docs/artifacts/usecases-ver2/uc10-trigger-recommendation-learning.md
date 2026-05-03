# Use Case UC10

## 유스케이스 이름
추천 학습 수동 실행

## Scope
이슈 관리 시스템

## Level
user-goal

## Primary Actor
PL

## Preconditions
- PL이 인증되어 있다.
- 추천 품질 개선이 필요해 학습 실행을 선택했다(UC9의 extend).

## Success Guarantee
- 학습·재계산이 완료되거나 실패 사유가 보고된다.

## Main Success Scenario
1. PL이 학습 수동 실행을 선택한다.
2. System이 해결 이력 데이터를 사용해 모델·가중치 등을 갱신한다.
3. System이 완료 또는 오류를 알린다.

## Miscellaneous
- 요청사항 **extend 2번**에 해당한다.
- **담당자 후보 추천(UC9)**를 확장한다.
