# Use Case UC6

## Use Case Name
Examine Issue (이슈 상세 확인 및 수정하기)

## Scope
이슈 관리 시스템

## Level
user-goal

## Primary Actor
PL / Dev / Tester

## Stakeholders and Interests
- **PL:** 배정/상태 변경으로 흐름을 관리하고 싶다.
- **Dev:** 코멘트 맥락을 이해하고 처리 결과를 기록하고 싶다.
- **Tester:** 검증 근거를 남기고 상태를 반영하고 싶다.

## Preconditions
- 사용자가 인증되어 있다.
- 대상 이슈가 존재한다.
- 일반적으로 UC4(이슈 조회 및 검색하기)에서 대상 이슈를 선택한 상태다.

## Success Guarantee
- 이슈 필드와 코멘트 이력이 제공된다.
- 허용된 변경(코멘트/배정/상태)이 저장된다.

## Main Success Scenario
1. Actor가 UC4를 통해 선택한 이슈를 상세 확인 대상으로 지정한다. (반드시 UC4를 통해서만 들어올 필요는 없음)
2. System이 이슈 상세(필드)와 코멘트 이력을 제공한다.
3. Actor가 필요한 수정(코멘트 추가, 담당자 변경, 상태 변경)을 요청한다.
4. System이 요청 유형에 맞는 검증을 수행하며, 상태 변경 요청인 경우 `Validate Issue State Transition`(UC5)를 include 수행한다.
5. System이 수정 내용을 저장하고 변경된 이슈 정보를 다시 제공한다.

## Extensions

*4a. Actor가 요청한 수정 권한이 없다.
1. System이 수정을 거절하고 사유를 알린다.
2. 시나리오는 2단계로 복귀한다.

*4b. 상태 변경 가능 여부 확인에 실패한다.
1. System이 상태 변경을 거절하고 사유를 알린다.
2. 시나리오는 2단계로 복귀한다.

*4c. PL이 상태 변경/배정 결정을 위해 담당자 추천을 요청한다(extend).
1. `Recommend Assignee Candidates`(UC7)가 확장 수행된다.
2. 시나리오는 2단계로 복귀한다.


## Special Requirements
- 코멘트 타임라인 순서/시간 무결성이 유지되어야 한다.

## Technology and Data Variations List
- 상세 표현 UI는 툴킷별로 달라도 동일 규칙을 적용한다.

## Frequency of Occurrence
높음.

## Miscellaneous
- Fixer는 수정 완료 상태 변경 시점에 기록한다.