# UC09 담당자 후보 추천(extend)

| 항목 | 내용 |
| --- | --- |
| Use-Case Name | 담당자 후보 추천 |
| Level | subfunction |
| Trigger | 이슈 배정(UC3)에서 PL이 담당자 후보 추천을 원함 |
| Primary Actor | PL |
| Stakeholders and Interests | PL: 이전 작업 이력을 참고해 이슈 수정에 적절한 dev를 추천받고 싶음. |
| Preconditions | PL이 인증되어 있음.<br>UC3 이슈 배정에서 PL이 담당자 후보 추천을 받길 원함. |
| Success Guarantee | PL에게 추천 dev 목록을 보여준다. |
| Main Success Scenario | 1. PL이 추천을 요청한다<br>2. System이 기존 이슈 수정 이력을 참고해 후보를 산출한다.<br>3. System이 상위 3명의 dev를 표시한다. |
| Extensions |  |
| Miscellaneous | 예제 시나리오: PL2는 현재 new 상태인 이슈들을 브라우즈하고, 상세 정보 보기 기능을 통해 특정 이슈의 상세한 내용과 기존 코멘트를 살펴봄. 이때 시스템은 fixed된 이슈들의 이력을 이용해서 가장 적절한 개발자를 추천해 줌. (예를 들어 “best candidate: dev2, dev5, dev1 등으로”, 가장 가능성이 높은 후보 3명을 순서대로 추천함) |
