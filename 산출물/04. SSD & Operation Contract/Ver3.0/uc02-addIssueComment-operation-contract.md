# UC02 Operation Contract - addIssueComment

Operation: `addIssueComment(issueID, authorID, comment)`

Cross References: UC02 이슈 등록, UC08 코멘트 추가

Preconditions:
- issueID에 해당하는 이슈와 authorID에 해당하는 사용자가 존재한다.

Postconditions:
- Comment의 인스턴스 c가 생성되었다.
- c.issueId가 입력된 issue.ID로 설정되며 c가 해당 이슈와 연결되었다.
- c.authorId가 입력된 authorID로 설정되며 c가 작성자와 연결되었다.
- c.body가 주어진 comment로 설정되었다.
- c.createdAt이 코멘트를 작성한 시각으로 설정되었다.
- c.id가 새로운 코멘트 식별자로 부여되었다.

