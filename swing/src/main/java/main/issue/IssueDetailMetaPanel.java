package main.issue;

import issue.dto.getIssueDetail.v1.GetIssueDetailOutput;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

// 상세 이슈 상단 정보 보여주는 내부 패널
class IssueDetailMetaPanel extends JPanel {

    private final JLabel reporterValueLabel;
    private final JLabel assigneeValueLabel;
    private final JLabel fixerValueLabel;
    private final JLabel priorityValueLabel;
    private final JLabel statusValueLabel;
    private final JLabel reportedValueLabel;
    private final JTextArea descriptionArea;

    IssueDetailMetaPanel() {
        super(new BorderLayout(8, 8));
        setBackground(new Color(247, 249, 252));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        reporterValueLabel = createMetaValueLabel();
        assigneeValueLabel = createMetaValueLabel();
        fixerValueLabel = createMetaValueLabel();
        priorityValueLabel = createMetaValueLabel();
        statusValueLabel = createMetaValueLabel();
        reportedValueLabel = createMetaValueLabel();

        JPanel metaGrid = new JPanel(new GridLayout(3, 4, 10, 8));
        metaGrid.setOpaque(false);
        metaGrid.add(createMetaTitleLabel("Reporter"));
        metaGrid.add(reporterValueLabel);
        metaGrid.add(createMetaTitleLabel("Assignee"));
        metaGrid.add(assigneeValueLabel);
        metaGrid.add(createMetaTitleLabel("Fixer"));
        metaGrid.add(fixerValueLabel);
        metaGrid.add(createMetaTitleLabel("Priority"));
        metaGrid.add(priorityValueLabel);
        metaGrid.add(createMetaTitleLabel("Status"));
        metaGrid.add(statusValueLabel);
        metaGrid.add(createMetaTitleLabel("Reported"));
        metaGrid.add(reportedValueLabel);

        descriptionArea = new JTextArea(4, 50);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setForeground(Color.BLACK);
        descriptionArea.setBackground(Color.WHITE);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230)),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JPanel descriptionPanel = new JPanel(new BorderLayout(6, 6));
        descriptionPanel.setOpaque(false);
        JLabel descriptionLabel = new JLabel("Description");
        descriptionLabel.setForeground(Color.BLACK);
        descriptionPanel.add(descriptionLabel, BorderLayout.NORTH);
        descriptionPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

        add(metaGrid, BorderLayout.NORTH);
        add(descriptionPanel, BorderLayout.CENTER);
    }

    // 상세 화면 데이터를 최신 데이터로 갱신
    void update(GetIssueDetailOutput output, String assigneeDisplay) {
        reporterValueLabel.setText(displayDefaultValue(output.reporterUserId()));
        assigneeValueLabel.setText(displayDefaultValue(assigneeDisplay));
        fixerValueLabel.setText(displayDefaultValue(output.fixerUserId()));
        priorityValueLabel.setText(displayDefaultValue(output.priority()));
        statusValueLabel.setText(displayDefaultValue(output.status()));
        reportedValueLabel.setText(output.reportedDate() == null
                ? "-"
                : output.reportedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        descriptionArea.setText(displayDefaultValue(output.issueDescription()));
    }

    // 이슈 상세 정보 항목 타이틀
    private JLabel createMetaTitleLabel(String title) {
        JLabel label = new JLabel(title);
        label.setForeground(new Color(90, 90, 90));
        return label;
    }

    // 이슈 상세 정보 항목 값
    private JLabel createMetaValueLabel() {
        JLabel label = new JLabel("-");
        label.setForeground(Color.BLACK);
        return label;
    }

    // 보여줄 값 null 이면 - 로 통일
    private String displayDefaultValue(Object value) {
        if (value == null) {
            return "-";
        }
        String text = value.toString();
        return text.isBlank() ? "-" : text;
    }
}
