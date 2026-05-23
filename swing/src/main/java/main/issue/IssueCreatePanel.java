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
        JTextField titleField = new JTextField(16);
        JTextField descriptionField = new JTextField(16);
        JComboBox<IssuePriority> priorityCombo = new JComboBox<>(IssuePriority.values());
        JTextArea commentArea = new JTextArea(4, 20);
        JScrollPane commentScroll = new JScrollPane(commentArea);
        UiTheme.styleTextField(titleField);
        UiTheme.styleTextField(descriptionField);
        UiTheme.styleCombo(priorityCombo);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setForeground(Color.BLACK);

        titleField.setPreferredSize(new Dimension(260, 34));
        descriptionField.setPreferredSize(new Dimension(260, 34));
        priorityCombo.setPreferredSize(new Dimension(260, 34));
        commentScroll.setPreferredSize(new Dimension(260, 120));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        formPanel.add(new JLabel("Title"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Description"));
        formPanel.add(descriptionField);
        formPanel.add(new JLabel("Priority"));
        formPanel.add(priorityCombo);

        JPanel panel = new JPanel(new BorderLayout(8, 10));
        panel.add(formPanel, BorderLayout.NORTH);

        JPanel commentPanel = new JPanel(new BorderLayout(0, 6));
        commentPanel.add(new JLabel("Comment *"), BorderLayout.NORTH);
        commentPanel.add(commentScroll, BorderLayout.CENTER);
        panel.add(commentPanel, BorderLayout.CENTER);

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
                (IssuePriority) priorityCombo.getSelectedItem(),
                commentArea.getText().trim()
        );
    }
}
