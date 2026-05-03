# Use Case UC2

## 유스케이스 이름
이슈 등록

## Scope
이슈 관리 시스템

## Level
user-goal

## Primary Actor
Tester

## Stakeholders and Interests
- **tester:** 결함·요청 사항을 등록하고 초기 코멘트를 남기고 싶다.
- **PL / dev:** 제목·설명이 있는 신규 이슈를 작업 대상으로 보고 싶다.

## Preconditions
- tester가 인증되어 있다.
- 대상 프로젝트에 이슈 등록 권한이 있다.

## Success Guarantee
- 이슈가 저장되고 식별자가 부여된다.
- Reporter·Reported Date는 등록 시점에 시스템이 자동 반영한다.
- 기본 상태는 `new`다.

## Main Success Scenario
1. tester가 제목·설명(필수)과 필요한 나머지 필드를 제출한다.
2. System이 현재 로그인 계정을 reporter로, 등록 시각을 reported date로 반영하고 이슈를 `new` 상태로 저장한다.
3. (선택) tester가 같은 이슈에 대해 코멘트 추가(UC8)를 include하여 코멘트를 추가한다.

## Extensions
*1a. 제목 또는 설명이 비었다.
1. System이 등록을 거절하고 tester가 필드를 보완한다.

## Miscellaneous
- 예제: tester1이 이슈를 만들고 코멘트를 하나 추가하면 reporter는 tester1, 상태는 new로 유지된다.
