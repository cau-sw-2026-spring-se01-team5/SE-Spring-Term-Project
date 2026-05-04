# Use Case UC7

## 유스케이스 이름
이슈 브라우즈

## Scope
이슈 관리 시스템

## Level
subfunction

## Primary Actor
User (PL / dev / tester 중 실제 로그인 역할)

## Stakeholders and Interests
- **PL:** 목록·검색으로 이슈를 고르고 내용을 파악하고 싶다.
- **dev:** 배정·상태 조건으로 자신의 작업 대상을 좁히고 싶다.
- **tester:** 본인이 보고한 이슈의 처리 상황을 보고 싶다.

## Preconditions
- 사용자가 인증되어 있다.
- 프로젝트(또는 프로젝트1) 맥락이 선택되어 있다.

## Success Guarantee
- 조건에 맞는 이슈 목록이 제공된다(0건 포함).
- 선택한 이슈에 대해 필드 값과 코멘트 이력을 조회할 수 있다.

## Main Success Scenario
1. Actor가 검색·필터 조건을 설정하거나 기본 목록을 연다.
2. System이 조건에 맞는 이슈 목록을 보여준다.
3. Actor가 한 이슈를 선택해 상세 보기를 연다.
4. System이 해당 이슈의 필드와 코멘트 타임라인을 표시한다.
(extend) 검색된 이슈들의 통계 분석(일/월 별 이슈 발생 횟수 및 트랜드 등 표시, UC10)을 **extend**로 실행해 표시한다

## Extensions
*1a. 조건이 비어 있다.
1. System이 기본 정렬·기본 목록을 제공한다.

## Special Requirements
- 검색·목록·상세는 **UC3·UC4·UC5·UC6**에서 공통으로 include된다(**항목 7 · include 1번**).

## Frequency of Occurrence
매우 높음.

## Miscellaneous
- **이슈 배정(UC3)**, **배정된 이슈 해결(UC4)**, **수정된 이슈 확인(UC5)**, **이슈 종료처리(UC6)**에서 include된다.
