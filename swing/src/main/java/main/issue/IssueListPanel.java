package main.issue;

import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
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
    private final JButton resetFilterButton = new JButton("Reset");
    private final JButton registerIssueButton = new JButton("Add Issue");
    private final JButton showDetailButton = new JButton("Details");

    private final JComboBox<UserFilterOption> assigneeFilterCombo = new JComboBox<>();
    private final JComboBox<UserFilterOption> reporterFilterCombo = new JComboBox<>();
    private final JComboBox<UserFilterOption> fixerFilterCombo = new JComboBox<>();
    private final JComboBox<IssueStatus> statusFilterCombo = new JComboBox<>();
    private final JComboBox<IssuePriority> priorityFilterCombo = new JComboBox<>();
    private final JTextField keywordField = new JTextField(12);

    // 각 버튼 클릭 이벤트를 Controller에 전달하기 위한 이벤트 객체
    private final UiEvent searchIssuesEvent = new UiEvent();
    private final UiEvent registerIssueEvent = new UiEvent();
    private final UiEvent showIssueDetailEvent = new UiEvent();

    private final IssueCreatePanel createDialog = new IssueCreatePanel(this);

    private static class UserFilterOption {
        private final Integer userId;
        private final String label;

        private UserFilterOption(Integer userId, String label) {
            this.userId = userId;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public IssueListPanel() {
        setLayout(new BorderLayout());
        setBackground(UiTheme.BG);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(10, 10, 6, 10));

        JPanel filterRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterRow1.setOpaque(false);
        filterRow1.add(new JLabel("Assignee"));
        filterRow1.add(assigneeFilterCombo);
        filterRow1.add(new JLabel("Reporter"));
        filterRow1.add(reporterFilterCombo);
        filterRow1.add(new JLabel("Fixer"));
        filterRow1.add(fixerFilterCombo);

        JPanel filterRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterRow2.setOpaque(false);
        filterRow2.add(new JLabel("Status"));
        filterRow2.add(statusFilterCombo);
        filterRow2.add(new JLabel("Priority"));
        filterRow2.add(priorityFilterCombo);
        filterRow2.add(new JLabel("Keyword"));
        filterRow2.add(keywordField);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(searchIssueButton);
        buttonRow.add(resetFilterButton);
        buttonRow.add(registerIssueButton);
        buttonRow.add(showDetailButton);

        top.add(filterRow1);
        top.add(Box.createVerticalStrut(6));
        top.add(filterRow2);
        top.add(Box.createVerticalStrut(6));
        top.add(buttonRow);

        // 이벤트 등록
        searchIssueButton.addActionListener(e -> searchIssuesEvent.emit());
        resetFilterButton.addActionListener(e -> {
            clearFilterInputs();
            searchIssuesEvent.emit();
        });
        registerIssueButton.addActionListener(e -> registerIssueEvent.emit());
        showDetailButton.addActionListener(e -> showIssueDetailEvent.emit());

        UiTheme.styleCombo(assigneeFilterCombo);
        UiTheme.styleCombo(reporterFilterCombo);
        UiTheme.styleCombo(fixerFilterCombo);
        UiTheme.styleCombo(statusFilterCombo);
        UiTheme.styleCombo(priorityFilterCombo);
        applyAllLabelRenderer(statusFilterCombo);
        applyAllLabelRenderer(priorityFilterCombo);
        UiTheme.styleTextField(keywordField);
        UiTheme.styleSecondaryButton(searchIssueButton);
        UiTheme.styleSecondaryButton(resetFilterButton);
        UiTheme.stylePrimaryButton(registerIssueButton);
        UiTheme.stylePrimaryButton(showDetailButton);
        UiTheme.styleTable(issueTable);

        assigneeFilterCombo.setPreferredSize(new Dimension(180, 34));
        reporterFilterCombo.setPreferredSize(new Dimension(180, 34));
        fixerFilterCombo.setPreferredSize(new Dimension(180, 34));
        statusFilterCombo.setPreferredSize(new Dimension(130, 34));
        priorityFilterCombo.setPreferredSize(new Dimension(130, 34));
        keywordField.setPreferredSize(new Dimension(180, 34));

        initFilterCombos();

        // 테이블에 스크롤 적용
        JScrollPane tableScroll = new JScrollPane(issueTable);
        tableScroll.setBorder(UiTheme.cardBorder(6));
        tableScroll.setPreferredSize(new Dimension(0, 420));

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

    public void setProjectUsers(List<IssueView.ProjectUserOption> users) {
        List<UserFilterOption> options = new ArrayList<>();
        options.add(new UserFilterOption(null, "(전체)"));
        for (IssueView.ProjectUserOption user : users) {
            options.add(new UserFilterOption(
                    user.userId(),
                    user.loginId() + " (#" + user.userId() + ") / " + user.role()
            ));
        }

        setUserFilterOptions(assigneeFilterCombo, options);
        setUserFilterOptions(reporterFilterCombo, options);
        setUserFilterOptions(fixerFilterCombo, options);
    }

    public IssueView.SearchCondition getSearchCondition() {
        UserFilterOption assignee = (UserFilterOption) assigneeFilterCombo.getSelectedItem();
        UserFilterOption reporter = (UserFilterOption) reporterFilterCombo.getSelectedItem();
        UserFilterOption fixer = (UserFilterOption) fixerFilterCombo.getSelectedItem();
        String keyword = keywordField.getText() == null ? "" : keywordField.getText().trim();

        return new IssueView.SearchCondition(
                assignee == null ? null : assignee.userId,
                reporter == null ? null : reporter.userId,
                fixer == null ? null : fixer.userId,
                (IssueStatus) statusFilterCombo.getSelectedItem(),
                (IssuePriority) priorityFilterCombo.getSelectedItem(),
                keyword.isBlank() ? null : keyword
        );
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

    private void initFilterCombos() {
        assigneeFilterCombo.addItem(new UserFilterOption(null, "(전체)"));
        reporterFilterCombo.addItem(new UserFilterOption(null, "(전체)"));
        fixerFilterCombo.addItem(new UserFilterOption(null, "(전체)"));

        statusFilterCombo.addItem(null);
        for (IssueStatus status : IssueStatus.values()) {
            statusFilterCombo.addItem(status);
        }

        priorityFilterCombo.addItem(null);
        for (IssuePriority priority : IssuePriority.values()) {
            priorityFilterCombo.addItem(priority);
        }
    }

    private void setUserFilterOptions(JComboBox<UserFilterOption> combo, List<UserFilterOption> options) {
        combo.removeAllItems();
        for (UserFilterOption option : options) {
            combo.addItem(option);
        }
        combo.setSelectedIndex(0);
    }

    private void clearFilterInputs() {
        assigneeFilterCombo.setSelectedIndex(0);
        reporterFilterCombo.setSelectedIndex(0);
        fixerFilterCombo.setSelectedIndex(0);
        statusFilterCombo.setSelectedItem(null);
        priorityFilterCombo.setSelectedItem(null);
        keywordField.setText("");
    }

    private static <T> void applyAllLabelRenderer(JComboBox<T> comboBox) {
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                Object display = value == null ? "(전체)" : value;
                return super.getListCellRendererComponent(list, display, index, isSelected, cellHasFocus);
            }
        });
    }
}
