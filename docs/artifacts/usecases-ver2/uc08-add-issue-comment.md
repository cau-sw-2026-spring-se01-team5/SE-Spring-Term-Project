# Use Case UC8

## Use Case Name
Add Issue Comment (이슈에 코멘트 추가하기)

## Scope
이슈 관리 시스템

## Level
subfunction

## Primary Actor
Project Member

## Preconditions
- 대상 이슈가 존재한다.
- 사용자가 코멘트 작성 권한을 가진다.

## Success Guarantee
- 코멘트 본문이 저장되고, 작성 시각·작성자가 이력에 반영된다.

## Main Success Scenario
1. Actor가 코멘트 텍스트를 제출한다.
2. System이 작성자·시각을 붙여 코멘트를 저장하고 타임라인에 반영한다.

## Miscellaneous
- 요청사항 **include 2번(코멘트 추가)**에 해당한다.
- **Register Issue (UC2)**·**Triage and Assign New Issues (UC3)**·**Fix Assigned Issue (UC4)**에서 include된다.
