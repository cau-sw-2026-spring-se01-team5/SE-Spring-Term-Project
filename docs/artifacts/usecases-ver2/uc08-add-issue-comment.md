# Use Case UC8

## 유스케이스 이름
코멘트 추가

## Scope
이슈 관리 시스템

## Level
subfunction

## Primary Actor
User

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
- **이슈 등록(UC2)**·**이슈 배정(UC3)**·**배정된 이슈 해결(UC4)**에서 include된다.
