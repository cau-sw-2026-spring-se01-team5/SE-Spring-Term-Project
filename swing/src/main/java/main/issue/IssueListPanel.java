package main.issue;

import issue.dto.getIssueList.v1.IssueSummaryOutput;
import ui.UiTheme;
import ui.event.UiEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// 이슈 목록 화면
public class IssueListPanel extends JPanel {

    private final IssueTableModel issueTableModel = new IssueTableModel(); // 이슈 목록을 테이블에 보여주기 위함
    private final JTable issueTable = new JTable(issueTableModel);

    private final JButton searchIssueButton = new JButton("Search Issue");
    private final JButton registerIssueButton = new JButton("Add Issue");
    private final JButton showDetailButton = new JButton("Details");

    // 각 버튼 클릭 이벤트를 Controller에 전달하기 위한 이벤트 객체
    private final UiEvent searchIssuesEvent = new UiEvent();
    private final UiEvent registerIssueEvent = new UiEvent();
    private final UiEvent showIssueDetailEvent = new UiEvent();

    // 검색 조건에서 사용하기 위한 프로젝트 유저 목록
    private List<IssueView.ProjectUserOption> projectUsers = new ArrayList<>();

    private final IssueSearchConditionPanel searchConditionDialog = new IssueSearchConditionPanel(this);
    private final IssueSearchResultPanel searchResultDialog = new IssueSearchResultPanel(this);
    private final IssueCreatePanel createDialog = new IssueCreatePanel(this);

    public IssueListPanel() {
        setLayout(new BorderLayout());
        setBackground(UiTheme.BG);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(10, 10, 6, 10));
        top.add(searchIssueButton);
        top.add(registerIssueButton);
        top.add(showDetailButton);

        // 이벤트 등록
        searchIssueButton.addActionListener(e -> searchIssuesEvent.emit());
        registerIssueButton.addActionListener(e -> registerIssueEvent.emit());
        showDetailButton.addActionListener(e -> showIssueDetailEvent.emit());

        UiTheme.styleSecondaryButton(searchIssueButton);
        UiTheme.stylePrimaryButton(registerIssueButton);
        UiTheme.stylePrimaryButton(showDetailButton);
        UiTheme.styleTable(issueTable);

        // 테이블에 스크롤 적용
        JScrollPane tableScroll = new JScrollPane(issueTable);
        tableScroll.setBorder(UiTheme.cardBorder(6));

        add(top, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
    }

    // controller로 받아온 이슈 목록을 화면에 반영
    public void setIssues(List<IssueSummaryOutput> issues) {
        issueTableModel.setIssues(issues);
    }

    public Integer getSelectedIssueId() {
        int row = issueTable.getSelectedRow();
        if (row < 0) {
            return null;
        }

        int modelRow = issueTable.convertRowIndexToModel(row);
        IssueSummaryOutput issue = issueTableModel.getIssueAt(modelRow);
        return issue == null ? null : issue.issueId();
    }

    // 검색 조건에서 사용할 프로젝트 유저 목록 세팅
    public void setProjectUsers(List<IssueView.ProjectUserOption> users) {
        this.projectUsers = new ArrayList<>(users);
    }

    // 검색 조건 입력 팝업 띄움
    public IssueView.SearchCondition showSearchPopup() {
        return searchConditionDialog.show(projectUsers);
    }

    // 사용자가 선택한 이슈 ID를 반환
    public Integer getSelectedIssueId(List<IssueSummaryOutput> issues) {
        Integer selectedIssueId = searchResultDialog.show(issues);
        if (selectedIssueId != null && selectedIssueId < 0) {
            showMessage("이슈를 선택하세요.");
            return null;
        }
        return selectedIssueId;
    }

    // 이슈 등록 입력창
    public IssueView.CreateIssueForm showCreateIssuePopup() {
        return createDialog.show();
    }

    // 이슈 등록 버튼 표시 여부 제어
    public void setRegisterVisible(boolean visible) {
        registerIssueButton.setVisible(visible);
    }

    // 이벤트 등록 메서드들 -> UI랑 controller랑 연결
    public void onSearchIssues(Runnable handler) {
        searchIssuesEvent.subscribe(handler);
    }

    public void onRegisterIssue(Runnable handler) {
        registerIssueEvent.subscribe(handler);
    }

    public void onShowIssueDetail(Runnable handler) {
        showIssueDetailEvent.subscribe(handler);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
