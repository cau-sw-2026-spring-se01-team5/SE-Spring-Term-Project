# Use Case UC7

## Use Case Name
Recommend Assignee Candidates (담당자 후보 추천하기)

## Scope
Issue Tracking System (ITS)

## Level
user-goal

## Primary Actor
PL

## Stakeholders and Interests
- **PL:** 담당자 선정 시간을 줄이고 싶다.
- **Dev:** 추천 결과가 공정하고 설명 가능하길 원한다.

## Preconditions
- PL이 인증되어 있다.
- 이슈 상세 맥락에서 추천이 요청되었다(extend).

## Success Guarantee
- 시스템이 후보 목록(예: 상위 3명)을 제시한다.

## Main Success Scenario
1. PL이 추천 요청을 제출한다.
2. System이 이력 기반으로 후보를 산출한다.
3. System이 후보 순위를 제공한다.

## Extensions
*2a. 학습/인덱스 최신화가 필요하다.
1. `Trigger Recommendation Learning`이 extend로 수행된다.
2. 기본 흐름 1로 복귀해 추천을 재요청한다.

*2b. 후보 산출이 불가능하다.
1. System이 데이터 부족 사유를 알린다.

## Special Requirements
- 추천 응답은 상호작용 가능한 시간 내에 제공되어야 한다.

## Technology and Data Variations List
- 룰 기반, 유사도 기반, ML 기반 등 알고리즘 변형을 허용한다.

## Frequency of Occurrence
중간.

## Miscellaneous
- UC6을 확장하는 선택 기능이다.
- Learn Now는 별도 확장 UC(`Trigger Recommendation Learning`)로 분리한다.
