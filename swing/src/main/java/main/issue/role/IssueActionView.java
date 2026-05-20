package main.issue.role;

import enums.issue.v1.IssueStatus;

import java.util.List;

// 각 정책들이 IssuePanel UI 자체를 모르더라도 화면 제어가 가능하도록 만든 추상화
public interface IssueActionView {

    // dev 할당 부분 보여줄지 여부
    void setAssignSectionVisible(boolean visible);
    // 추천 부분 보여줄지 여부
    void setRecommendButtonVisible(boolean visible);
    // 상태 보여줄지 여부
    void setStatusSectionVisible(boolean visible);
    // 이슈 삭제 보여줄지 여부
    void setDeleteButtonVisible(boolean visible);
    // 댓글 창 보여줄지 여부
    void setCommentSectionVisible(boolean visible);
    // 이슈 상태 리스트 지정
    void setStatusOptions(List<IssueStatus> statuses);
}
