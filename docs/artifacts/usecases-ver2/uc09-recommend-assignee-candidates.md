# Use Case UC9

## 유스케이스 이름
담당자 후보 추천

## Scope
이슈 관리 시스템

## Level
user-goal

## Primary Actor
PL

## Preconditions
- PL이 인증되어 있다.
- 이슈 배정(UC3) 흐름 등에서 추천을 요청했다(**UC3**의 extend).

## Success Guarantee
- fixed·closed·resolved 등 **해결된 이슈 이력**을 바탕으로 상위 후보 dev를 순위대로 제시한다(예: 상위 3명 순서).

## Main Success Scenario
1. PL이 추천 요청을 제출한다.
2. System이 이력을 분석해 후보를 산출·정렬한다.
3. System이 상위 3명(또는 설정된 개수)을 순서대로 표시한다.

## Extensions
*2a. 추천 품질 개선을 위해 학습이 필요하다.
1. 추천 학습 수동 실행(UC10)을 extend로 수행한다.
2. 필요 시 PL이 추천을 다시 요청한다.

## Miscellaneous
- 요청사항 **extend 1번**에 해당한다.
- **이슈 배정(UC3)**를 확장한다.
