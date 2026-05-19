package main.issue.role;

import enums.issue.v1.IssueStatus;

import java.util.List;

/* 역할별 이슈에 대한 정책 -> UI단에서 처리해야 할 정책들 */
/* 공통 기능만 추상클래스로 묶음 */
abstract class BaseIssuePolicy implements IssuePolicy {

    // protected 지정해서 외부에서는 직접 호출 못하고 자식 클래스에서만 사용할 수 있도록
    protected void configure(
            IssueActionView view, // 보여줄 UI 인터페이스
            boolean assignVisible, // 이슈 할당 가부
            boolean recommendVisible, // 담당자 추천 버튼 표시 여부
            boolean statusVisible, // 상태 변경 가부
            boolean deleteVisible, // 이슈 삭제 버튼 표시 여부
            List<IssueStatus> statusOptions // 현재 role에서 선택 가능한 이슈 상태 리스트
    ) {
        view.setAssignSectionVisible(assignVisible); // assign 영역 표시 여부
        view.setRecommendButtonVisible(recommendVisible); // 추천 버튼 표시 여부
        view.setStatusSectionVisible(statusVisible); // status 변경 영역 표시 여부
        view.setDeleteButtonVisible(deleteVisible); // 이슈 삭제 표시 여부
        view.setCommentSectionVisible(true); // 댓글 달기 -> 모두 다 볼 수 있음
        view.setStatusOptions(statusOptions); // 보여줄 이슈 상태 리스트
    }
}
