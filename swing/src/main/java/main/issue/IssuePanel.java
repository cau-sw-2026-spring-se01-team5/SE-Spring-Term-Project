package main.issue;

import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;
import issue.dto.getIssueDetail.v1.CommentOutput;
import issue.dto.getIssueDetail.v1.GetIssueDetailOutput;
import issue.dto.getIssueList.v1.IssueSummaryOutput;
import issue.dto.recommendAssignee.v1.RecommendAssigneeOutput;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class IssuePanel extends JPanel implements IssueView {

    private final IssueTableModel issueTableModel = new IssueTableModel();
    private final JTable issueTable = new JTable(issueTableModel);

    private final JTextField filterAssigneeField = new JTextField(6);
    private final JTextField filterReporterField = new JTextField(6);
    private final JTextField filterFixerField = new JTextField(6);
    private final JComboBox<IssueStatus> filterStatusComboBox = new JComboBox<>();
    private final JComboBox<IssuePriority> filterPriorityComboBox = new JComboBox<>();
    private final JTextField filterKeywordField = new JTextField(10);
    private final JButton searchIssueButton = new JButton("이슈 조회/검색");

    private final JTextField issueTitleField = new JTextField(12);
    private final JTextField issueDescriptionField = new JTextField(18);
    private final JComboBox<IssuePriority> issuePriorityComboBox = new JComboBox<>(IssuePriority.values());
    private final JButton registerIssueButton = new JButton("이슈 등록");

    private final JTextField assigneeUserIdField = new JTextField(6);
    private final JButton assignIssueButton = new JButton("이슈 배정");

    private final JComboBox<IssueStatus> targetStatusComboBox = new JComboBox<>(IssueStatus.values());
    private final JButton changeStatusButton = new JButton("상태 변경");

    private final JTextField issueCommentField = new JTextField(20);
    private final JButton addCommentButton = new JButton("코멘트 추가");

    private final JButton showDetailButton = new JButton("상세 조회");
    private final JButton recommendButton = new JButton("담당자 추천");
    private final JButton deleteIssueButton = new JButton("이슈 삭제");

    public IssuePanel() {
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        top.add(createIssueSearchPanel(), BorderLayout.NORTH);
        top.add(new JScrollPane(issueTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(4, 1));
        bottom.add(createRegisterIssuePanel());
        bottom.add(createAssignPanel());
        bottom.add(createStatusAndCommentPanel());
        bottom.add(createIssueActionPanel());

        add(top, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel createIssueSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        filterStatusComboBox.addItem(null);
        for (IssueStatus status : IssueStatus.values()) {
            filterStatusComboBox.addItem(status);
        }

        filterPriorityComboBox.addItem(null);
        for (IssuePriority priority : IssuePriority.values()) {
            filterPriorityComboBox.addItem(priority);
        }

        panel.add(new JLabel("assignee"));
        panel.add(filterAssigneeField);
        panel.add(new JLabel("reporter"));
        panel.add(filterReporterField);
        panel.add(new JLabel("fixer"));
        panel.add(filterFixerField);
        panel.add(new JLabel("status"));
        panel.add(filterStatusComboBox);
        panel.add(new JLabel("priority"));
        panel.add(filterPriorityComboBox);
        panel.add(new JLabel("keyword"));
        panel.add(filterKeywordField);
        panel.add(searchIssueButton);

        return panel;
    }

    private JPanel createRegisterIssuePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(new JLabel("Title"));
        panel.add(issueTitleField);
        panel.add(new JLabel("Description"));
        panel.add(issueDescriptionField);
        panel.add(new JLabel("Priority"));
        panel.add(issuePriorityComboBox);
        panel.add(registerIssueButton);

        return panel;
    }

    private JPanel createAssignPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(new JLabel("Assignee User ID"));
        panel.add(assigneeUserIdField);
        panel.add(assignIssueButton);
        panel.add(recommendButton);

        return panel;
    }

    private JPanel createStatusAndCommentPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(new JLabel("Target Status"));
        panel.add(targetStatusComboBox);
        panel.add(changeStatusButton);

        panel.add(new JLabel("Comment"));
        panel.add(issueCommentField);
        panel.add(addCommentButton);

        return panel;
    }

    private JPanel createIssueActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(showDetailButton);
        panel.add(deleteIssueButton);

        return panel;
    }

    @Override
    public void setIssues(List<IssueSummaryOutput> issues) {
        issueTableModel.setIssues(issues);
    }

    @Override
    public Integer getSelectedIssueId() {
        int row = issueTable.getSelectedRow();

        if (row < 0) {
            return null;
        }

        int modelRow = issueTable.convertRowIndexToModel(row);
        IssueSummaryOutput issue = issueTableModel.getIssueAt(modelRow);

        return issue == null ? null : issue.issueId();
    }

    @Override
    public String getIssueTitleInput() {
        return issueTitleField.getText().trim();
    }

    @Override
    public String getIssueDescriptionInput() {
        return issueDescriptionField.getText().trim();
    }

    @Override
    public IssuePriority getIssuePriorityInput() {
        return (IssuePriority) issuePriorityComboBox.getSelectedItem();
    }

    @Override
    public Integer getAssigneeUserIdInput() {
        return parseNullableInt(assigneeUserIdField.getText());
    }

    @Override
    public String getIssueCommentInput() {
        return issueCommentField.getText().trim();
    }

    @Override
    public IssueStatus getTargetIssueStatusInput() {
        return (IssueStatus) targetStatusComboBox.getSelectedItem();
    }

    @Override
    public Integer getFilterAssigneeUserId() {
        return parseNullableInt(filterAssigneeField.getText());
    }

    @Override
    public Integer getFilterReporterUserId() {
        return parseNullableInt(filterReporterField.getText());
    }

    @Override
    public Integer getFilterFixerUserId() {
        return parseNullableInt(filterFixerField.getText());
    }

    @Override
    public IssueStatus getFilterStatus() {
        return (IssueStatus) filterStatusComboBox.getSelectedItem();
    }

    @Override
    public IssuePriority getFilterPriority() {
        return (IssuePriority) filterPriorityComboBox.getSelectedItem();
    }

    @Override
    public String getFilterKeyword() {
        String keyword = filterKeywordField.getText().trim();
        return keyword.isBlank() ? null : keyword;
    }

    @Override
    public void showIssueDetail(GetIssueDetailOutput output) {
        if (!output.success()) {
            JOptionPane.showMessageDialog(this, output.message());
            return;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("Issue #").append(output.issueId()).append("\n");
        sb.append("Title: ").append(output.issueTitle()).append("\n");
        sb.append("Description: ").append(output.issueDescription()).append("\n");
        sb.append("Reporter: ").append(output.reporterUserId()).append("\n");
        sb.append("Assignee: ").append(output.assigneeUserId()).append("\n");
        sb.append("Fixer: ").append(output.fixerUserId()).append("\n");
        sb.append("Priority: ").append(output.priority()).append("\n");
        sb.append("Status: ").append(output.status()).append("\n");
        sb.append("Reported Date: ").append(output.reportedDate()).append("\n\n");
        sb.append("[Comments]\n");

        for (CommentOutput comment : output.comments()) {
            sb.append("#").append(comment.commentId())
                    .append(" / author=").append(comment.authorUserId())
                    .append(" / ").append(comment.createdAt())
                    .append("\n")
                    .append(comment.comment())
                    .append("\n\n");
        }

        JTextArea area = new JTextArea(sb.toString(), 22, 70);
        area.setEditable(false);

        JOptionPane.showMessageDialog(
                this,
                new JScrollPane(area),
                "Issue Detail",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void showRecommendations(RecommendAssigneeOutput output) {
        if (!output.success()) {
            JOptionPane.showMessageDialog(this, output.message());
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                output.candidates().toString(),
                "Recommended Assignees",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void onSearchIssues(Runnable handler) {
        searchIssueButton.addActionListener(e -> handler.run());
    }

    @Override
    public void onRegisterIssue(Runnable handler) {
        registerIssueButton.addActionListener(e -> handler.run());
    }

    @Override
    public void onAssignIssue(Runnable handler) {
        assignIssueButton.addActionListener(e -> handler.run());
    }

    @Override
    public void onChangeIssueStatus(Runnable handler) {
        changeStatusButton.addActionListener(e -> handler.run());
    }

    @Override
    public void onAddIssueComment(Runnable handler) {
        addCommentButton.addActionListener(e -> handler.run());
    }

    @Override
    public void onShowIssueDetail(Runnable handler) {
        showDetailButton.addActionListener(e -> handler.run());
    }

    @Override
    public void onRecommendAssignee(Runnable handler) {
        recommendButton.addActionListener(e -> handler.run());
    }

    @Override
    public void onDeleteIssue(Runnable handler) {
        deleteIssueButton.addActionListener(e -> handler.run());
    }

    @Override
    public void applyRole(UserRole role) {
        boolean admin = role == UserRole.ADMIN;
        boolean pl = role == UserRole.PL;
        boolean dev = role == UserRole.DEV;
        boolean tester = role == UserRole.TESTER;

        registerIssueButton.setVisible(tester);
        assignIssueButton.setVisible(pl);
        recommendButton.setVisible(pl);
        changeStatusButton.setVisible(pl || dev || tester);
        deleteIssueButton.setVisible(admin || pl);

        targetStatusComboBox.removeAllItems();

        if (pl) {
            targetStatusComboBox.addItem(IssueStatus.CLOSED);
            targetStatusComboBox.addItem(IssueStatus.ASSIGNED);
        } else if (dev) {
            targetStatusComboBox.addItem(IssueStatus.FIXED);
        } else if (tester) {
            targetStatusComboBox.addItem(IssueStatus.RESOLVED);
            targetStatusComboBox.addItem(IssueStatus.REOPENED);
        } else if (admin) {
            for (IssueStatus status : IssueStatus.values()) {
                targetStatusComboBox.addItem(status);
            }
        }
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private Integer parseNullableInt(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        return Integer.parseInt(text.trim());
    }
}