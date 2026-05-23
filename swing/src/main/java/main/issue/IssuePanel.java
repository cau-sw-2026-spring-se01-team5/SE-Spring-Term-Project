package main.issue;

import enums.user.v1.UserRole;
import issue.dto.getIssueDetail.v1.GetIssueDetailOutput;
import issue.dto.getIssueList.v1.IssueSummaryOutput;
import issue.dto.recommendAssignee.v1.RecommendAssigneeOutput;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// 이슈 관련 패널
/* IssueListPanel + IssueDetailPanel을 묶어서 Controller에게 하나의 IssueView 처럼 보이게 Facade 역할 함 */
public class IssuePanel extends JPanel implements IssueView {

    private final IssueListPanel issueListPanel = new IssueListPanel(); // 이슈 전체 리스트 보는 패널
    private final IssueDetailPanel issueDetailPanel = new IssueDetailPanel(); // 상세 이슈 팝업

    public IssuePanel() {
        setLayout(new BorderLayout());
        add(issueListPanel, BorderLayout.CENTER);
    }

    // 이슈 목록 데이터를 목록 패널에 전달
    @Override
    public void setIssues(List<IssueSummaryOutput> issues) {
        issueListPanel.setIssues(issues);
    }

    // 현재 선택된 이슈 ID 반환
    @Override
    public Integer getSelectedIssueId() {
        return issueListPanel.getSelectedIssueId();
    }

    // 현재 상세 팝업에서 보고 있는 이슈 ID 반환
    @Override
    public Integer getActiveDetailIssueId() {
        return issueDetailPanel.getActiveDetailIssueId();
    }

    // 상세 팝업에서 선택된 담당자 ID 반환
    @Override
    public Integer getAssigneeUserIdInput() {
        return issueDetailPanel.getAssigneeUserIdInput();
    }

    // 배정 가능한 dev 목록을 상세 팝업에 전달
    @Override
    public void setAssigneeCandidates(List<AssigneeCandidate> candidates) {
        issueDetailPanel.setAssigneeCandidates(candidates);
    }

    // 프로젝트 전체 사용자 목록을 목록 패널에 전달
    @Override
    public void setProjectUsers(List<ProjectUserOption> users) {
        issueListPanel.setProjectUsers(users);
    }

    // 상세 팝업의 댓글 입력값 반환
    @Override
    public String getIssueCommentInput() {
        return issueDetailPanel.getIssueCommentInput();
    }

    // 상태 변경 드롭박스에서 선택된 상태 반환
    @Override
    public enums.issue.v1.IssueStatus getTargetIssueStatusInput() {
        return issueDetailPanel.getTargetIssueStatusInput();
    }

    // 검색 조건 입력값 반환
    @Override
    public SearchCondition getSearchCondition() {
        return issueListPanel.getSearchCondition();
    }

    // 이슈 생성 입력 팝업 표시
    @Override
    public CreateIssueForm showCreateIssueDialog() {
        return issueListPanel.showCreateIssuePopup();
    }

    // 상세 팝업 표시
    @Override
    public void showIssueDetail(GetIssueDetailOutput output) {
        issueDetailPanel.showIssueDetail(output);
    }

    // 추천 결과 표시
    @Override
    public void showRecommendations(RecommendAssigneeOutput output) {
        issueDetailPanel.showRecommendations(output);
    }

    @Override
    public void closeIssueDetail() {
        issueDetailPanel.closeIssueDetail();
    }

    // 버튼에 이벤트 등록
    @Override
    public void onSearchIssues(Runnable handler) {
        issueListPanel.onSearchIssues(handler);
    }

    @Override
    public void onRegisterIssue(Runnable handler) {
        issueListPanel.onRegisterIssue(handler);
    }

    @Override
    public void onAssignIssue(Runnable handler) {
        issueDetailPanel.onAssignIssue(handler);
    }

    @Override
    public void onChangeIssueStatus(Runnable handler) {
        issueDetailPanel.onChangeIssueStatus(handler);
    }

    @Override
    public void onAddIssueComment(Runnable handler) {
        issueDetailPanel.onAddIssueComment(handler);
    }

    @Override
    public void onShowIssueDetail(Runnable handler) {
        issueListPanel.onShowIssueDetail(handler);
    }

    @Override
    public void onRecommendAssignee(Runnable handler) {
        issueDetailPanel.onRecommendAssignee(handler);
    }

    @Override
    public void onDeleteIssue(Runnable handler) {
        issueDetailPanel.onDeleteIssue(handler);
    }

    @Override
    public void applyRole(UserRole role) {
        issueDetailPanel.applyRole(role);
        issueListPanel.setRegisterVisible(role == UserRole.TESTER);
    }

    @Override
    public void showMessage(String message) {
        issueDetailPanel.showMessage(message);
    }
}
