# Use Case UC7

## Use Case Name
Browse and View Issue Detail (이슈 브라우즈·상세 보기)

## Scope
이슈 관리 시스템

## Level
subfunction

## Primary Actor
Project Member (상호작용을 시작한 구성원; PL / Dev / Tester 중 실제 로그인 역할)

## Stakeholders and Interests
- **PL:** 목록·검색으로 이슈를 고르고 내용을 파악하고 싶다.
- **Dev:** 배정·상태 조건으로 자신의 작업 대상을 좁히고 싶다.
- **Tester:** 본인이 보고한 이슈의 처리 상황을 보고 싶다.

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

## Extensions
*1a. 조건이 비어 있다.
1. System이 기본 정렬·기본 목록을 제공한다.

## Special Requirements
- 검색·목록·상세는 **UC3·UC4·UC5·UC6**에서 공통으로 include된다(**항목 7 · include 1번**).

## Frequency of Occurrence
매우 높음.

## Miscellaneous
- **Triage and Assign New Issues (UC3)**, **Fix Assigned Issue (UC4)**, **Verify Fixed Issues and Resolve (UC5)**, **Close Resolved Issues (UC6)**에서 include된다.
