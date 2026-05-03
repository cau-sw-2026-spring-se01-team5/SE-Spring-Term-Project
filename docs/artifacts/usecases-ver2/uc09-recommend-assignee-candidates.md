# Use Case UC9

## Use Case Name
Recommend Assignee Candidates (담당자 후보 추천하기)

## Scope
이슈 관리 시스템

## Level
user-goal

## Primary Actor
PL

## Preconditions
- PL이 인증되어 있다.
- new 이슈 트리아지 등에서 추천을 요청했다(**UC3**의 extend).

## Success Guarantee
- fixed·closed·resolved 등 **해결된 이슈 이력**을 바탕으로 상위 후보 개발자를 순위대로 제시한다(예: best candidate: dev2, dev5, dev1 — 상위 3명).

## Main Success Scenario
1. PL이 추천 요청을 제출한다.
2. System이 이력을 분석해 후보를 산출·정렬한다.
3. System이 상위 3명(또는 설정된 개수)을 순서대로 표시한다.

## Extensions
*2a. 추천 품질 개선을 위해 학습이 필요하다.
1. `Trigger Recommendation Learning`(UC10)를 extend로 수행한다.
2. 필요 시 PL이 추천을 다시 요청한다.

## Miscellaneous
- 요청사항 **extend 1번**에 해당한다.
- **Triage and Assign New Issues (UC3)**를 확장한다.
