# Use Case UC8

## Use Case Name
Trigger Recommendation Learning (추천 학습 수동 실행하기)

## Scope
Issue Tracking System (ITS)

## Level
user-goal

## Primary Actor
PL

## Stakeholders and Interests
- **PL:** 추천 정확도를 빠르게 개선하고 싶다.
- **Dev/Tester:** 추천 결과가 더 신뢰 가능하길 원한다.

## Preconditions
- PL이 인증되어 있다.
- 학습 가능한 해결 이력 데이터가 존재한다.
- `Recommend Assignee Candidates`(UC7) 실행 중 학습 필요 조건이 발생했다(extend).

## Success Guarantee
- 추천 모델 또는 인덱스가 최신 이력 기준으로 갱신된다.
- 이후 추천 요청에서 갱신된 결과를 사용할 수 있다.

## Main Success Scenario
1. PL이 Learn Now 실행을 요청한다.
2. System이 해결/종료 이력 데이터를 수집한다.
3. System이 추천 모델/인덱스를 갱신한다.
4. System이 학습 완료 상태를 반환한다.

## Extensions
*2a. 학습 데이터가 부족하다.
1. System이 데이터 부족 사유를 알린다.
2. 유스케이스가 실패 종료된다.

*3a. 학습 실행 중 오류가 발생한다.
1. System이 오류를 기록하고 실패를 알린다.
2. 유스케이스가 실패 종료된다.

## Special Requirements
- 학습 수행 중에도 기존 조회 기능은 서비스 중단 없이 동작해야 한다.

## Technology and Data Variations List
- 학습 방식은 배치 룰 업데이트, 유사도 인덱스 갱신, ML 재학습 중 선택 가능하다.

## Frequency of Occurrence
낮음~중간(모델 정확도 저하 시 수동 실행).

## Miscellaneous
- `Recommend Assignee Candidates`(UC7)의 확장 UC이다.
