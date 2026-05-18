package main.issue;

import enums.issue.v1.IssuePriority;
import ui.UiTheme;

import javax.swing.*;
import java.awt.*;

// 이슈 등록 입력창
class IssueCreatePanel {

    private final Component owner;

    IssueCreatePanel(Component owner) {
        this.owner = owner;
    }

    IssueView.CreateIssueForm show() {
        JTextField titleField = new JTextField(20);
        JTextField descriptionField = new JTextField(20);
        JComboBox<IssuePriority> priorityCombo = new JComboBox<>(IssuePriority.values());
        UiTheme.styleTextField(titleField);
        UiTheme.styleTextField(descriptionField);
        UiTheme.styleCombo(priorityCombo);

        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        panel.add(new JLabel("Title"));
        panel.add(titleField);
        panel.add(new JLabel("Description"));
        panel.add(descriptionField);
        panel.add(new JLabel("Priority"));
        panel.add(priorityCombo);

        int result = JOptionPane.showConfirmDialog(
                owner,
                panel,
                "이슈 생성",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        return new IssueView.CreateIssueForm(
                titleField.getText().trim(),
                descriptionField.getText().trim(),
                (IssuePriority) priorityCombo.getSelectedItem()
        );
    }
}
