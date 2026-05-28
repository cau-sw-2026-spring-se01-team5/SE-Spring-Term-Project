# UC07 이슈 브라우즈(include)

| 항목 | 내용 |
| --- | --- |
| Use-Case Name | 이슈 브라우즈 |
| Level | Subfunction |
| Primary Actor | User |
| Stakeholders and Interests | PL: 원하는 필터를 설정해 내용을 파악하고 싶음<br>Dev: 본인에게 배정되었거나 수정했던 이슈들을 확인하고 싶음<br>Tester: 본인이 보고했던 이슈의 처리 상황을 확인하고 싶음 |
| Preconditions | User가 인증된 상태이다.<br>프로젝트가 선택된 상태이다. |
| Success Guarantee | 조건에 맞는 이슈 목록들을 보여준다. |
| Main Success Scenario | 1. Actor가 기본 목록을 열거나 검색 조건을 설정한다.<br>2. System이 선택한 조건에 맞는 이슈들의 목록을 보여준다.<br>(extend) 선택한 조건에 맞는 이슈들의 통계 분석(UC10) 결과를 보여준다. |
| Extensions | 1a. 조건이 비어졌다.<br>1. System이 기본 목록을 보여준다. |
| Miscellaneous |  |
