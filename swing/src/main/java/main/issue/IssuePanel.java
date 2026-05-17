package main.issue;

import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;
import issue.dto.getIssueDetail.v1.CommentOutput;
import issue.dto.getIssueDetail.v1.GetIssueDetailOutput;
import issue.dto.getIssueList.v1.IssueSummaryOutput;
import issue.dto.recommendAssignee.v1.RecommendAssigneeOutput;
import main.issue.role.IssueActionPolicyRegistry;
import main.issue.role.IssueActionView;
import ui.UiTheme;
import ui.event.UiEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class IssuePanel extends JPanel implements IssueView {

    private final IssueTableModel issueTableModel = new IssueTableModel();
    private final JTable issueTable = new JTable(issueTableModel);

    private final JButton searchIssueButton = new JButton("이슈 조회/검색");
    private final JButton registerIssueButton = new JButton("이슈 등록");
    private final JButton showDetailButton = new JButton("상세 조회");

    private final UiEvent searchIssuesEvent = new UiEvent();
    private final UiEvent registerIssueEvent = new UiEvent();
    private final UiEvent showIssueDetailEvent = new UiEvent();
    private final UiEvent assignIssueEvent = new UiEvent();
    private final UiEvent changeStatusEvent = new UiEvent();
    private final UiEvent addCommentEvent = new UiEvent();
    private final UiEvent recommendEvent = new UiEvent();
    private final UiEvent deleteIssueEvent = new UiEvent();

    private final IssueActionPolicyRegistry issueActionPolicyRegistry = new IssueActionPolicyRegistry();
    private final DetailActionView detailActionView = new DetailActionView();

    private UserRole currentRole;

    private JDialog detailDialog;
    private JLabel detailHeaderLabel;
    private JLabel detailReporterValueLabel;
    private JLabel detailAssigneeValueLabel;
    private JLabel detailFixerValueLabel;
    private JLabel detailPriorityValueLabel;
    private JLabel detailStatusValueLabel;
    private JLabel detailReportedValueLabel;
    private JTextArea detailDescriptionArea;
    private JPanel commentsListPanel;
    private JScrollPane commentsScrollPane;

    private JComboBox<AssigneeOption> detailAssigneeCombo;
    private JComboBox<IssueStatus> detailStatusCombo;
    private JTextArea detailCommentArea;

    private JButton detailAssignButton;
    private JButton detailRecommendButton;
    private JButton detailStatusButton;
    private JButton detailDeleteButton;
    private JButton detailCommentButton;
    private JLabel detailAssigneeLabel;
    private JLabel detailStatusLabel;
    private List<AssigneeCandidate> assigneeCandidates = new ArrayList<>();
    private List<ProjectUserOption> projectUsers = new ArrayList<>();
    private Integer activeDetailIssueId;

    private static class AssigneeOption {
        private final Integer userId;
        private final String label;

        private AssigneeOption(Integer userId, String label) {
            this.userId = userId;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

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

    public IssuePanel() {
        setLayout(new BorderLayout());
        setBackground(UiTheme.BG);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(10, 10, 6, 10));
        top.add(searchIssueButton);
        top.add(registerIssueButton);
        top.add(showDetailButton);

        searchIssueButton.addActionListener(e -> searchIssuesEvent.emit());
        registerIssueButton.addActionListener(e -> registerIssueEvent.emit());
        showDetailButton.addActionListener(e -> showIssueDetailEvent.emit());

        UiTheme.styleSecondaryButton(searchIssueButton);
        UiTheme.stylePrimaryButton(registerIssueButton);
        UiTheme.stylePrimaryButton(showDetailButton);
        UiTheme.styleTable(issueTable);

        JScrollPane tableScroll = new JScrollPane(issueTable);
        tableScroll.setBorder(UiTheme.cardBorder(6));

        add(top, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
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
    public Integer getActiveDetailIssueId() {
        return activeDetailIssueId;
    }

    @Override
    public Integer getAssigneeUserIdInput() {
        if (detailAssigneeCombo == null) {
            return null;
        }
        AssigneeOption selected = (AssigneeOption) detailAssigneeCombo.getSelectedItem();
        return selected == null ? null : selected.userId;
    }

    @Override
    public void setAssigneeCandidates(List<AssigneeCandidate> candidates) {
        this.assigneeCandidates = new ArrayList<>(candidates);

        if (detailAssigneeCombo == null) {
            return;
        }

        detailAssigneeCombo.removeAllItems();
        for (AssigneeCandidate candidate : assigneeCandidates) {
            detailAssigneeCombo.addItem(new AssigneeOption(
                    candidate.userId(),
                    candidate.loginId() + " (#" + candidate.userId() + ")"
            ));
        }
    }

    @Override
    public void setProjectUsers(List<ProjectUserOption> users) {
        this.projectUsers = new ArrayList<>(users);
    }

    @Override
    public String getIssueCommentInput() {
        return detailCommentArea == null ? "" : detailCommentArea.getText().trim();
    }

    @Override
    public IssueStatus getTargetIssueStatusInput() {
        return detailStatusCombo == null ? null : (IssueStatus) detailStatusCombo.getSelectedItem();
    }

    @Override
    public SearchCondition showSearchDialog() {
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

        for (ProjectUserOption user : projectUsers) {
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
                this,
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

        return new SearchCondition(
                assignee == null ? null : assignee.userId,
                reporter == null ? null : reporter.userId,
                fixer == null ? null : fixer.userId,
                (IssueStatus) statusCombo.getSelectedItem(),
                (IssuePriority) priorityCombo.getSelectedItem(),
                keyword.isBlank() ? null : keyword
        );
    }

    @Override
    public Integer showSearchResultAndSelectIssue(List<IssueSummaryOutput> issues) {
        IssueTableModel popupTableModel = new IssueTableModel();
        popupTableModel.setIssues(issues);
        JTable table = new JTable(popupTableModel);
        UiTheme.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(920, 300));

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(UiTheme.CARD_BG);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(new JLabel("이슈를 선택한 뒤 '상세 조회'를 누르세요."), BorderLayout.SOUTH);

        String[] options = {"상세 조회", "닫기"};
        int result = JOptionPane.showOptionDialog(
                this,
                panel,
                "검색 결과 (" + issues.size() + "건)",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (result != 0) {
            return null;
        }

        int row = table.getSelectedRow();
        if (row < 0) {
            showMessage("이슈를 선택하세요.");
            return null;
        }

        int modelRow = table.convertRowIndexToModel(row);
        IssueSummaryOutput selected = popupTableModel.getIssueAt(modelRow);
        return selected == null ? null : selected.issueId();
    }

    @Override
    public CreateIssueForm showCreateIssueDialog() {
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
                this,
                panel,
                "이슈 생성",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        return new CreateIssueForm(
                titleField.getText().trim(),
                descriptionField.getText().trim(),
                (IssuePriority) priorityCombo.getSelectedItem()
        );
    }

    @Override
    public void showIssueDetail(GetIssueDetailOutput output) {
        if (!output.success()) {
            JOptionPane.showMessageDialog(this, output.message());
            return;
        }

        ensureDetailDialog();
        refreshDetailDialog(output);
        detailDialog.setVisible(true);
        detailDialog.toFront();
        detailDialog.requestFocus();
        SwingUtilities.invokeLater(() -> detailCommentArea.requestFocusInWindow());
    }

    private void ensureDetailDialog() {
        if (detailDialog != null) {
            return;
        }

        Window owner = SwingUtilities.getWindowAncestor(this);
        detailDialog = new JDialog(owner, "Issue Detail", Dialog.ModalityType.MODELESS);
        detailDialog.setSize(900, 680);
        detailDialog.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(UiTheme.BG);
        root.setBorder(new EmptyBorder(14, 14, 14, 14));

        detailHeaderLabel = new JLabel();
        detailHeaderLabel.setForeground(Color.BLACK);

        JPanel commentsCard = new JPanel(new BorderLayout(8, 8));
        commentsCard.setBorder(BorderFactory.createTitledBorder("Comments"));

        commentsListPanel = new JPanel();
        commentsListPanel.setLayout(new BoxLayout(commentsListPanel, BoxLayout.Y_AXIS));
        commentsListPanel.setBackground(Color.WHITE);

        commentsScrollPane = new JScrollPane(commentsListPanel);
        commentsScrollPane.setPreferredSize(new Dimension(600, 250));
        commentsCard.add(commentsScrollPane, BorderLayout.CENTER);

        JPanel actionCard = buildDetailActionPanel();
        JPanel issueOverviewCard = buildIssueOverviewCard();

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.add(issueOverviewCard, BorderLayout.NORTH);
        center.add(commentsCard, BorderLayout.CENTER);
        center.add(actionCard, BorderLayout.SOUTH);

        root.add(detailHeaderLabel, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);

        detailDialog.setContentPane(root);
    }

    private JPanel buildIssueOverviewCard() {
        JPanel overviewCard = new JPanel(new BorderLayout(8, 8));
        overviewCard.setBackground(new Color(247, 249, 252));
        overviewCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        detailReporterValueLabel = createMetaValueLabel();
        detailAssigneeValueLabel = createMetaValueLabel();
        detailFixerValueLabel = createMetaValueLabel();
        detailPriorityValueLabel = createMetaValueLabel();
        detailStatusValueLabel = createMetaValueLabel();
        detailReportedValueLabel = createMetaValueLabel();

        JPanel metaGrid = new JPanel(new GridLayout(3, 4, 10, 8));
        metaGrid.setOpaque(false);
        metaGrid.add(createMetaTitleLabel("Reporter"));
        metaGrid.add(detailReporterValueLabel);
        metaGrid.add(createMetaTitleLabel("Assignee"));
        metaGrid.add(detailAssigneeValueLabel);
        metaGrid.add(createMetaTitleLabel("Fixer"));
        metaGrid.add(detailFixerValueLabel);
        metaGrid.add(createMetaTitleLabel("Priority"));
        metaGrid.add(detailPriorityValueLabel);
        metaGrid.add(createMetaTitleLabel("Status"));
        metaGrid.add(detailStatusValueLabel);
        metaGrid.add(createMetaTitleLabel("Reported"));
        metaGrid.add(detailReportedValueLabel);

        detailDescriptionArea = new JTextArea(4, 50);
        detailDescriptionArea.setEditable(false);
        detailDescriptionArea.setLineWrap(true);
        detailDescriptionArea.setWrapStyleWord(true);
        detailDescriptionArea.setForeground(Color.BLACK);
        detailDescriptionArea.setBackground(Color.WHITE);
        detailDescriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JPanel descriptionPanel = new JPanel(new BorderLayout(6, 6));
        descriptionPanel.setOpaque(false);
        JLabel descriptionLabel = new JLabel("Description");
        descriptionLabel.setForeground(Color.BLACK);
        descriptionPanel.add(descriptionLabel, BorderLayout.NORTH);
        descriptionPanel.add(new JScrollPane(detailDescriptionArea), BorderLayout.CENTER);

        overviewCard.add(metaGrid, BorderLayout.NORTH);
        overviewCard.add(descriptionPanel, BorderLayout.CENTER);
        return overviewCard;
    }

    private JLabel createMetaTitleLabel(String title) {
        JLabel label = new JLabel(title);
        label.setForeground(new Color(90, 90, 90));
        return label;
    }

    private JLabel createMetaValueLabel() {
        JLabel label = new JLabel("-");
        label.setForeground(Color.BLACK);
        return label;
    }

    private JPanel buildDetailActionPanel() {
        JPanel actionCard = new JPanel(new BorderLayout(10, 10));
        actionCard.setBorder(BorderFactory.createTitledBorder("Actions"));
        actionCard.setBackground(UiTheme.CARD_BG);

        JPanel topActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topActions.setOpaque(false);
        detailAssigneeCombo = new JComboBox<>();
        detailAssignButton = new JButton("UC03 이슈 배정");
        detailRecommendButton = new JButton("담당자 추천");
        detailStatusCombo = new JComboBox<>();
        detailStatusButton = new JButton("상태 변경");
        detailDeleteButton = new JButton("이슈 삭제");

        detailAssigneeLabel = new JLabel("Assignee DEV");
        topActions.add(detailAssigneeLabel);
        topActions.add(detailAssigneeCombo);
        topActions.add(detailAssignButton);
        topActions.add(detailRecommendButton);
        topActions.add(detailDeleteButton);

        JPanel statusActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statusActionPanel.setOpaque(false);
        detailStatusLabel = new JLabel("Target Status");
        statusActionPanel.add(detailStatusLabel);
        statusActionPanel.add(detailStatusCombo);
        statusActionPanel.add(detailStatusButton);

        JPanel commentComposer = new JPanel(new BorderLayout(8, 8));
        commentComposer.setOpaque(false);
        detailCommentArea = new JTextArea(3, 40);
        detailCommentArea.setLineWrap(true);
        detailCommentArea.setWrapStyleWord(true);
        detailCommentArea.setBorder(BorderFactory.createLineBorder(new Color(210, 214, 220)));

        detailCommentButton = new JButton("댓글 등록");

        commentComposer.add(new JLabel("새 댓글"), BorderLayout.NORTH);
        commentComposer.add(new JScrollPane(detailCommentArea), BorderLayout.CENTER);
        commentComposer.add(detailCommentButton, BorderLayout.EAST);

        detailAssignButton.addActionListener(e -> assignIssueEvent.emit());
        detailRecommendButton.addActionListener(e -> recommendEvent.emit());
        detailStatusButton.addActionListener(e -> changeStatusEvent.emit());
        detailDeleteButton.addActionListener(e -> deleteIssueEvent.emit());
        detailCommentButton.addActionListener(e -> addCommentEvent.emit());

        JPanel body = new JPanel(new BorderLayout(8, 8));
        body.setOpaque(false);
        body.add(statusActionPanel, BorderLayout.NORTH);
        body.add(commentComposer, BorderLayout.CENTER);

        UiTheme.styleCombo(detailAssigneeCombo);
        UiTheme.styleCombo(detailStatusCombo);
        UiTheme.stylePrimaryButton(detailAssignButton);
        UiTheme.styleSecondaryButton(detailRecommendButton);
        UiTheme.stylePrimaryButton(detailStatusButton);
        UiTheme.styleDangerButton(detailDeleteButton);
        UiTheme.stylePrimaryButton(detailCommentButton);

        actionCard.add(topActions, BorderLayout.NORTH);
        actionCard.add(body, BorderLayout.CENTER);

        return actionCard;
    }

    private void refreshDetailDialog(GetIssueDetailOutput output) {
        activeDetailIssueId = output.issueId();
        detailHeaderLabel.setText("Issue #" + output.issueId() + "  " + output.issueTitle());

        String assigneeDisplay = resolveAssigneeDisplay(output.assigneeUserId());
        detailReporterValueLabel.setText(displayValue(output.reporterUserId()));
        detailAssigneeValueLabel.setText(displayValue(assigneeDisplay));
        detailFixerValueLabel.setText(displayValue(output.fixerUserId()));
        detailPriorityValueLabel.setText(displayValue(output.priority()));
        detailStatusValueLabel.setText(displayValue(output.status()));
        detailReportedValueLabel.setText(output.reportedDate() == null
                ? "-"
                : output.reportedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        detailDescriptionArea.setText(displayValue(output.issueDescription()));

        renderComments(output.comments());

        refreshAssigneeComboSelection(output.assigneeUserId());
        detailCommentArea.setText("");
        detailCommentArea.setEnabled(true);
        detailCommentArea.setEditable(true);

        applyRoleToDetailActions();
    }

    private String displayValue(Object value) {
        if (value == null) {
            return "-";
        }

        String text = value.toString();
        return text.isBlank() ? "-" : text;
    }

    private void refreshAssigneeComboSelection(Integer assigneeUserId) {
        if (detailAssigneeCombo == null) {
            return;
        }

        detailAssigneeCombo.removeAllItems();
        AssigneeOption selected = null;

        for (AssigneeCandidate candidate : assigneeCandidates) {
            AssigneeOption option = new AssigneeOption(
                    candidate.userId(),
                    candidate.loginId() + " (#" + candidate.userId() + ")"
            );
            detailAssigneeCombo.addItem(option);
            if (assigneeUserId != null && assigneeUserId.equals(candidate.userId())) {
                selected = option;
            }
        }

        if (selected != null) {
            detailAssigneeCombo.setSelectedItem(selected);
        }
    }

    private String resolveAssigneeDisplay(Integer assigneeUserId) {
        if (assigneeUserId == null) {
            return "-";
        }

        for (AssigneeCandidate candidate : assigneeCandidates) {
            if (assigneeUserId.equals(candidate.userId())) {
                return candidate.loginId();
            }
        }

        return "#" + assigneeUserId;
    }

    private void renderComments(List<CommentOutput> comments) {
        commentsListPanel.removeAll();

        for (CommentOutput comment : comments) {
            JPanel bubble = new JPanel(new BorderLayout(6, 6));
            bubble.setBorder(BorderFactory.createCompoundBorder(
                    new EmptyBorder(6, 2, 6, 2),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(225, 229, 236)),
                            new EmptyBorder(8, 10, 8, 10)
                    )
            ));
            bubble.setBackground(new Color(250, 251, 253));

            JLabel meta = new JLabel(comment.authorUserId() + "  ·  "
                    + comment.createdAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            meta.setForeground(Color.BLACK);

            JTextArea text = new JTextArea(comment.comment());
            text.setEditable(false);
            text.setLineWrap(true);
            text.setWrapStyleWord(true);
            text.setOpaque(false);

            bubble.add(meta, BorderLayout.NORTH);
            bubble.add(text, BorderLayout.CENTER);

            commentsListPanel.add(bubble);
            commentsListPanel.add(Box.createVerticalStrut(6));
        }

        commentsListPanel.revalidate();
        commentsListPanel.repaint();
        JScrollBar bar = commentsScrollPane.getVerticalScrollBar();
        bar.setValue(bar.getMaximum());
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
        searchIssuesEvent.subscribe(handler);
    }

    @Override
    public void onRegisterIssue(Runnable handler) {
        registerIssueEvent.subscribe(handler);
    }

    @Override
    public void onAssignIssue(Runnable handler) {
        assignIssueEvent.subscribe(handler);
    }

    @Override
    public void onChangeIssueStatus(Runnable handler) {
        changeStatusEvent.subscribe(handler);
    }

    @Override
    public void onAddIssueComment(Runnable handler) {
        addCommentEvent.subscribe(handler);
    }

    @Override
    public void onShowIssueDetail(Runnable handler) {
        showIssueDetailEvent.subscribe(handler);
    }

    @Override
    public void onRecommendAssignee(Runnable handler) {
        recommendEvent.subscribe(handler);
    }

    @Override
    public void onDeleteIssue(Runnable handler) {
        deleteIssueEvent.subscribe(handler);
    }

    @Override
    public void applyRole(UserRole role) {
        this.currentRole = role;

        boolean tester = role == UserRole.TESTER;
        registerIssueButton.setVisible(tester);

        applyRoleToDetailActions();
    }

    private void applyRoleToDetailActions() {
        if (currentRole == null || detailDialog == null) {
            return;
        }

        issueActionPolicyRegistry.resolve(currentRole).apply(detailActionView);
    }

    @Override
    public void showMessage(String message) {
        Component parent = (detailDialog != null && detailDialog.isShowing()) ? detailDialog : this;
        JOptionPane.showMessageDialog(parent, message);
    }

    private class DetailActionView implements IssueActionView {

        @Override
        public void setAssignSectionVisible(boolean visible) {
            if (detailAssigneeLabel == null || detailAssigneeCombo == null || detailAssignButton == null) {
                return;
            }
            detailAssigneeLabel.setVisible(visible);
            detailAssigneeCombo.setVisible(visible);
            detailAssignButton.setVisible(visible);
        }

        @Override
        public void setRecommendButtonVisible(boolean visible) {
            if (detailRecommendButton == null) {
                return;
            }
            detailRecommendButton.setVisible(visible);
        }

        @Override
        public void setStatusSectionVisible(boolean visible) {
            if (detailStatusLabel == null || detailStatusCombo == null || detailStatusButton == null) {
                return;
            }
            detailStatusLabel.setVisible(visible);
            detailStatusCombo.setVisible(visible);
            detailStatusButton.setVisible(visible);
        }

        @Override
        public void setDeleteButtonVisible(boolean visible) {
            if (detailDeleteButton == null) {
                return;
            }
            detailDeleteButton.setVisible(visible);
        }

        @Override
        public void setCommentSectionVisible(boolean visible) {
            if (detailCommentArea == null || detailCommentButton == null) {
                return;
            }
            detailCommentArea.setEnabled(visible);
            detailCommentArea.setEditable(visible);
            detailCommentButton.setVisible(visible);
        }

        @Override
        public void setStatusOptions(List<IssueStatus> statuses) {
            if (detailStatusCombo == null) {
                return;
            }
            detailStatusCombo.removeAllItems();
            for (IssueStatus status : statuses) {
                detailStatusCombo.addItem(status);
            }
        }
    }
}
