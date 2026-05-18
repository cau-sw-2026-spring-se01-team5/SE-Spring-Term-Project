package main.issue;

import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import ui.UiTheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// 검색 조건 입력 팝업 띄움
class IssueSearchConditionPanel {

    // 검색 조건 드롭박스에 들어갈 항목 객체
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

    private final Component owner;

    IssueSearchConditionPanel(Component owner) {
        this.owner = owner;
    }

    IssueView.SearchCondition show(List<IssueView.ProjectUserOption> projectUsers) {
        JComboBox<UserFilterOption> assigneeCombo = new JComboBox<>();
        JComboBox<UserFilterOption> reporterCombo = new JComboBox<>();
        JComboBox<UserFilterOption> fixerCombo = new JComboBox<>();
        JComboBox<IssueStatus> statusCombo = new JComboBox<>();
        JComboBox<IssuePriority> priorityCombo = new JComboBox<>();
        JTextField keywordField = new JTextField(14);
        UiTheme.styleCombo(assigneeCombo);
        UiTheme.styleCombo(reporterCombo);
        UiTheme.styleCombo(fixerCombo);
        UiTheme.styleCombo(statusCombo);
        UiTheme.styleCombo(priorityCombo);
        UiTheme.styleTextField(keywordField);

        assigneeCombo.addItem(new UserFilterOption(null, "(전체)"));
        reporterCombo.addItem(new UserFilterOption(null, "(전체)"));
        fixerCombo.addItem(new UserFilterOption(null, "(전체)"));

        for (IssueView.ProjectUserOption user : projectUsers) {
            UserFilterOption option = new UserFilterOption(
                    user.userId(),
                    user.loginId() + " (#" + user.userId() + ") / " + user.role()
            );
            assigneeCombo.addItem(option);
            reporterCombo.addItem(option);
            fixerCombo.addItem(option);
        }

        statusCombo.addItem(null);
        for (IssueStatus status : IssueStatus.values()) {
            statusCombo.addItem(status);
        }

        priorityCombo.addItem(null);
        for (IssuePriority priority : IssuePriority.values()) {
            priorityCombo.addItem(priority);
        }

        JPanel panel = new JPanel(new GridLayout(6, 2, 8, 8));
        panel.add(new JLabel("Assignee User ID"));
        panel.add(assigneeCombo);
        panel.add(new JLabel("Reporter User ID"));
        panel.add(reporterCombo);
        panel.add(new JLabel("Fixer User ID"));
        panel.add(fixerCombo);
        panel.add(new JLabel("Status"));
        panel.add(statusCombo);
        panel.add(new JLabel("Priority"));
        panel.add(priorityCombo);
        panel.add(new JLabel("Keyword"));
        panel.add(keywordField);

        int result = JOptionPane.showConfirmDialog(
                owner,
                panel,
                "이슈 조회/검색",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        String keyword = keywordField.getText().trim();
        UserFilterOption assignee = (UserFilterOption) assigneeCombo.getSelectedItem();
        UserFilterOption reporter = (UserFilterOption) reporterCombo.getSelectedItem();
        UserFilterOption fixer = (UserFilterOption) fixerCombo.getSelectedItem();

        return new IssueView.SearchCondition(
                assignee == null ? null : assignee.userId,
                reporter == null ? null : reporter.userId,
                fixer == null ? null : fixer.userId,
                (IssueStatus) statusCombo.getSelectedItem(),
                (IssuePriority) priorityCombo.getSelectedItem(),
                keyword.isBlank() ? null : keyword
        );
    }
}
