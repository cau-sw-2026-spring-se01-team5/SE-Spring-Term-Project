# UC02 Operation Contract - addIssueComment

Operation: `addIssueComment(issueID, authorID, comment)`

Cross References: UC02 이슈 등록, UC08 코멘트 추가

Preconditions:
- `issueID`에 해당하는 이슈가 존재한다.
- `authorID`에 해당하는 사용자가 존재한다.

Postconditions:
- `Comment` 인스턴스 `c`가 생성되었다. (instance creation)
- `c.issueId`가 입력된 `issueID`로 설정되어, `c`가 해당 `Issue`와 연결되었다. (association formed)
- `c.authorId`가 입력된 `authorID`로 설정되어, `c`가 작성자와 연결되었다. (association formed)
- `c.body`가 입력된 `comment`로 설정되었다. 단, 값이 없으면 빈 내용에 해당하는 기본 문구가 설정되었다. (attribute modification)
- `c.createdAt`이 코멘트 작성 시각으로 설정되었다. (attribute modification)
- `c.id`가 새 코멘트 식별자로 부여되었다. (attribute modification)
