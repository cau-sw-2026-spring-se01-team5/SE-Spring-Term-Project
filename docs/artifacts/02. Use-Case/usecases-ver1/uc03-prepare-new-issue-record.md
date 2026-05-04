# Use Case UC3

## Use Case Name
Capture Actor and Timestamp (작업자 및 현재 시각 기록하기)

## Scope
이슈 관리 시스템

## Level
subfunction

## Primary Actor
System

## Stakeholders and Interests
- **모든 사용자:** 작성자/수정자와 시간 정보가 일관되게 기록되길 원한다.

## Preconditions
- 호출한 상위 UC가 인증 사용자 정보를 가지고 있다.
- 시스템이 현재 시각을 조회할 수 있다.

## Success Guarantee
- 현재 행위를 수행한 사용자 식별자(actorId)가 결정된다.
- 현재 시각(timestamp)이 결정된다.
- 상위 UC가 목적에 맞게 필드에 기록할 수 있는 값이 반환된다.

## Main Success Scenario
1. System이 현재 인증 사용자 식별자를 읽는다.
2. System이 현재 시각을 읽는다.
3. System이 (actorId, timestamp) 값을 상위 UC로 반환한다.

## Extensions
-

## Special Requirements
- 시간/사용자 식별은 신뢰 가능한 출처에서 읽어야 한다.

## Technology and Data Variations List
- 시간 소스는 서버 시각 또는 시스템 시각을 사용할 수 있다.

## Frequency of Occurrence
매우 높음

## Miscellaneous
- UC2(등록)에서는 Reporter/Reported Date 기록에 사용한다.
- UC6(코멘트/수정)에서는 작성자/수정 시각 기록에 사용한다.
