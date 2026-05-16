package main.issue;

import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;
import issue.dto.getIssueDetail.v1.CommentOutput;
import issue.dto.getIssueDetail.v1.GetIssueDetailOutput;
import issue.dto.getIssueList.v1.IssueSummaryOutput;
import issue.dto.recommendAssignee.v1.RecommendAssigneeOutput;

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

    private UserRole currentRole;
    private Runnable assignIssueHandler = () -> {};
    private Runnable changeStatusHandler = () -> {};
    private Runnable addCommentHandler = () -> {};
    private Runnable recommendHandler = () -> {};
    private Runnable deleteIssueHandler = () -> {};

    private JDialog detailDialog;
    private JLabel detailHeaderLabel;
    private JTextArea overviewArea;
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

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(searchIssueButton);
        top.add(registerIssueButton);
        top.add(showDetailButton);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(issueTable), BorderLayout.CENTER);
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
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(920, 300));

        JPanel panel = new JPanel(new BorderLayout(8, 8));
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
        root.setBorder(new EmptyBorder(14, 14, 14, 14));

        detailHeaderLabel = new JLabel();
        detailHeaderLabel.setFont(new Font("Dialog", Font.BOLD, 20));

        overviewArea = new JTextArea();
        overviewArea.setEditable(false);
        overviewArea.setLineWrap(true);
        overviewArea.setWrapStyleWord(true);
        overviewArea.setFont(new Font("Dialog", Font.PLAIN, 13));
        overviewArea.setBackground(new Color(247, 249, 252));
        overviewArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JPanel commentsCard = new JPanel(new BorderLayout(8, 8));
        commentsCard.setBorder(BorderFactory.createTitledBorder("Comments"));

        commentsListPanel = new JPanel();
        commentsListPanel.setLayout(new BoxLayout(commentsListPanel, BoxLayout.Y_AXIS));
        commentsListPanel.setBackground(Color.WHITE);

        commentsScrollPane = new JScrollPane(commentsListPanel);
        commentsScrollPane.setPreferredSize(new Dimension(600, 250));
        commentsCard.add(commentsScrollPane, BorderLayout.CENTER);

        JPanel actionCard = buildDetailActionPanel();

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.add(overviewArea, BorderLayout.NORTH);
        center.add(commentsCard, BorderLayout.CENTER);
        center.add(actionCard, BorderLayout.SOUTH);

        root.add(detailHeaderLabel, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);

        detailDialog.setContentPane(root);
    }

    private JPanel buildDetailActionPanel() {
        JPanel actionCard = new JPanel(new BorderLayout(10, 10));
        actionCard.setBorder(BorderFactory.createTitledBorder("Actions"));

        JPanel topActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        detailAssigneeCombo = new JComboBox<>();
        detailAssignButton = new JButton("UC03 이슈 배정");
        detailRecommendButton = new JButton("담당자 추천");
        detailStatusCombo = new JComboBox<>();
        detailStatusButton = new JButton("UC04/05/06 상태 반영");
        detailDeleteButton = new JButton("이슈 삭제");

        detailAssigneeLabel = new JLabel("Assignee DEV");
        topActions.add(detailAssigneeLabel);
        topActions.add(detailAssigneeCombo);
        topActions.add(detailAssignButton);
        topActions.add(detailRecommendButton);
        topActions.add(new JLabel("Target Status"));
        topActions.add(detailStatusCombo);
        topActions.add(detailStatusButton);
        topActions.add(detailDeleteButton);

        JPanel commentComposer = new JPanel(new BorderLayout(8, 8));
        detailCommentArea = new JTextArea(3, 40);
        detailCommentArea.setLineWrap(true);
        detailCommentArea.setWrapStyleWord(true);
        detailCommentArea.setBorder(BorderFactory.createLineBorder(new Color(210, 214, 220)));

        detailCommentButton = new JButton("댓글 등록");

        commentComposer.add(new JLabel("새 댓글"), BorderLayout.NORTH);
        commentComposer.add(new JScrollPane(detailCommentArea), BorderLayout.CENTER);
        commentComposer.add(detailCommentButton, BorderLayout.EAST);

        detailAssignButton.addActionListener(e -> assignIssueHandler.run());
        detailRecommendButton.addActionListener(e -> recommendHandler.run());
        detailStatusButton.addActionListener(e -> changeStatusHandler.run());
        detailDeleteButton.addActionListener(e -> deleteIssueHandler.run());
        detailCommentButton.addActionListener(e -> addCommentHandler.run());

        actionCard.add(topActions, BorderLayout.NORTH);
        actionCard.add(commentComposer, BorderLayout.CENTER);

        return actionCard;
    }

    private void refreshDetailDialog(GetIssueDetailOutput output) {
        activeDetailIssueId = output.issueId();
        detailHeaderLabel.setText("Issue #" + output.issueId() + "  " + output.issueTitle());

        String assigneeDisplay = resolveAssigneeDisplay(output.assigneeUserId());
        String overview = "Reporter: " + output.reporterUserId()
                + "\nAssignee: " + assigneeDisplay
                + "\nFixer: " + output.fixerUserId()
                + "\nPriority: " + output.priority()
                + "\nStatus: " + output.status()
                + "\nReported: " + output.reportedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                + "\n\nDescription\n" + output.issueDescription();

        overviewArea.setText(overview);
        renderComments(output.comments());

        refreshAssigneeComboSelection(output.assigneeUserId());
        detailCommentArea.setText("");
        detailCommentArea.setEnabled(true);
        detailCommentArea.setEditable(true);

        applyRoleToDetailActions();
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
            meta.setForeground(new Color(82, 93, 110));
            meta.setFont(new Font("Dialog", Font.BOLD, 12));

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
        searchIssueButton.addActionListener(e -> handler.run());
    }

    @Override
    public void onRegisterIssue(Runnable handler) {
        registerIssueButton.addActionListener(e -> handler.run());
    }

    @Override
    public void onAssignIssue(Runnable handler) {
        this.assignIssueHandler = handler;
    }

    @Override
    public void onChangeIssueStatus(Runnable handler) {
        this.changeStatusHandler = handler;
    }

    @Override
    public void onAddIssueComment(Runnable handler) {
        this.addCommentHandler = handler;
    }

    @Override
    public void onShowIssueDetail(Runnable handler) {
        showDetailButton.addActionListener(e -> handler.run());
    }

    @Override
    public void onRecommendAssignee(Runnable handler) {
        this.recommendHandler = handler;
    }

    @Override
    public void onDeleteIssue(Runnable handler) {
        this.deleteIssueHandler = handler;
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

        boolean admin = currentRole == UserRole.ADMIN;
        boolean pl = currentRole == UserRole.PL;
        boolean dev = currentRole == UserRole.DEV;
        boolean tester = currentRole == UserRole.TESTER;

        detailAssignButton.setVisible(admin || pl);
        detailRecommendButton.setVisible(admin || pl);
        detailStatusButton.setVisible(admin || pl || dev || tester);
        detailDeleteButton.setVisible(admin || pl);
        detailCommentButton.setVisible(true);
        detailAssigneeLabel.setVisible(admin || pl);
        detailAssigneeCombo.setVisible(admin || pl);

        detailStatusCombo.removeAllItems();

        if (pl) {
            detailStatusCombo.addItem(IssueStatus.CLOSED);
        } else if (dev) {
            detailStatusCombo.addItem(IssueStatus.FIXED);
        } else if (tester) {
            detailStatusCombo.addItem(IssueStatus.RESOLVED);
        } else if (admin) {
            for (IssueStatus status : IssueStatus.values()) {
                detailStatusCombo.addItem(status);
            }
        }
    }

    @Override
    public void showMessage(String message) {
        Component parent = (detailDialog != null && detailDialog.isShowing()) ? detailDialog : this;
        JOptionPane.showMessageDialog(parent, message);
    }

}
