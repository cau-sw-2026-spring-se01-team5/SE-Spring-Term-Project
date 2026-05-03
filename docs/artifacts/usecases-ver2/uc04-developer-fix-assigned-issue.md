# Use Case UC4

## 유스케이스 이름
배정된 이슈 해결

## Scope
이슈 관리 시스템

## Level
user-goal

## Primary Actor
dev

## Stakeholders and Interests
- **dev:** 배정된 이슈 맥락을 파악하고 처리 결과를 ITS에 남기고 싶다.

## Preconditions
- dev가 인증되어 있다.

## Success Guarantee
- 코멘트가 필요하면 저장되고, 정책이 허용하면 상태가 `fixed`로 바뀐다.
- fixed 처리 시 fixer가 현재 dev로 기록된다.

## Main Success Scenario
1. dev가 이슈 브라우즈(UC7)를 include하여 자신에게 assign된 이슈만 검색·브라우즈하고, 코멘트를 포함한 상세 정보를 확인한다.
2. 코드 수정 등 실제 수정 작업은 ITS 밖(별도 도구)에서 수행한다고 가정한다.
3. dev가 코멘트 추가(UC8)를 include하여 처리 내용을 코멘트로 남긴다.
4. dev가 이슈 상태를 `fixed`로 변경한다.
5. System이 fixer를 해당 dev로 기록한다.

## Extensions


## Miscellaneous
- 과제 **항목 4** 시나리오에 해당한다.
