# Use Case UC2

## Use Case Name
Register Issue (이슈 등록하기)

## Scope
이슈 관리 시스템

## Level
user-goal

## Primary Actor
Tester

## Stakeholders and Interests
- **Tester:** 수정이 필요한 이슈를 등록하고 싶다.
- **PL:** 배정 가능한 이슈들이 필요하다.
- **Dev:** 제목/설명/우선순위로 수정이 필요한 이슈를 이해하고 싶다.

## Preconditions
- Tester가 인증되어 있다.

## Success Guarantee
- 이슈가 생성/저장된다.
- 조회 가능한 이슈 식별자가 생성된다.

## Main Success Scenario
1. Tester가 제목과 설명을 제출한다.
2. Tester가 필요 시 우선순위를 제출한다. <- 이거는 여기서 넣을지 PL이 확인할 때 넣을지 고민중
3. System이 `Capture Actor and Timestamp` include 수행한다.
4. System이 반환값으로 Reporter와 Reported Date를 기록한다.
5. System이 신규 이슈 기본 상태(`new`)와 기본 우선순위(미지정 시)를 반영한다.
6. System이 이슈를 저장한다.

## Extensions
*1a. 제목 또는 설명이 비어 있다.
1. System이 등록을 거절한다.
2. Tester가 필수 필드를 보완한다.

## Special Requirements
- Title/Description 필수 검증이 항상 적용된다.

## Technology and Data Variations List
-

## Frequency of Occurrence
높음.

## Miscellaneous
- Reporter와 Reported Date는 자동 기록된다.
