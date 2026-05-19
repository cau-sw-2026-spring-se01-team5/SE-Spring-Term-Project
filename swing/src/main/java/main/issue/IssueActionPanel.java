package main.issue;

import enums.issue.v1.IssueStatus;
import main.issue.role.IssueActionView;
import ui.UiTheme;
import ui.event.UiEvent;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// 개별 이슈에 대해 적용할 액션 모음 박스
class IssueActionPanel extends JPanel implements IssueActionView {

    // 개별 이슈에 배정 가능한 담당자 드롭박스
    private static class AssigneeOption {
        private final Integer userId;
        private final String label;

        private AssigneeOption(Integer userId, String label) {
            this.userId = userId; // api로 넘길 유저 고유 id값
            this.label = label; // 실제로 보여줄 텍스트
        }

        @Override
        public String toString() {
            return label;
        }
    }

    // 상세 팝업 내부 버튼 이벤트를 api로 전달하기 위한 객체들
    private final UiEvent assignIssueEvent = new UiEvent();
    private final UiEvent changeStatusEvent = new UiEvent();
    private final UiEvent addCommentEvent = new UiEvent();
    private final UiEvent recommendEvent = new UiEvent();
    private final UiEvent deleteIssueEvent = new UiEvent();

    private final JComboBox<AssigneeOption> assigneeDropBox = new JComboBox<>();
    private final JComboBox<IssueStatus> statusDropBox = new JComboBox<>();
    private final JTextArea commentArea = new JTextArea(3, 40);
    private final JButton assignButton = new JButton("Assign Issue");
    private final JButton recommendButton = new JButton("Recommend Dev");
    private final JButton statusButton = new JButton("Change Status");
    private final JButton deleteIssueButton = new JButton("Del Issue");
    private final JButton addCommentButton = new JButton("Add Comment");
    private final JLabel assigneeLabel = new JLabel("Assignee DEV");
    private final JLabel statusLabel = new JLabel("Target Status");

    private List<IssueView.AssigneeCandidate> assigneeCandidates = new ArrayList<>();

    IssueActionPanel() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Actions"));
        setBackground(UiTheme.CARD_BG);

        JPanel topActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topActions.setOpaque(false);
        topActions.add(assigneeLabel);
        topActions.add(assigneeDropBox);
        topActions.add(assignButton);
        topActions.add(recommendButton);
        topActions.add(deleteIssueButton);

        JPanel statusActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statusActionPanel.setOpaque(false);
        statusActionPanel.add(statusLabel);
        statusActionPanel.add(statusDropBox);
        statusActionPanel.add(statusButton);

        JPanel commentComposer = new JPanel(new BorderLayout(8, 8));
        commentComposer.setOpaque(false);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setBorder(BorderFactory.createLineBorder(new Color(210, 214, 220)));
        commentComposer.add(new JLabel("New Comment"), BorderLayout.NORTH);
        commentComposer.add(new JScrollPane(commentArea), BorderLayout.CENTER);
        commentComposer.add(addCommentButton, BorderLayout.EAST);

        assignButton.addActionListener(e -> assignIssueEvent.emit());
        recommendButton.addActionListener(e -> recommendEvent.emit());
        statusButton.addActionListener(e -> changeStatusEvent.emit());
        deleteIssueButton.addActionListener(e -> deleteIssueEvent.emit());
        addCommentButton.addActionListener(e -> addCommentEvent.emit());

        JPanel body = new JPanel(new BorderLayout(8, 8));
        body.setOpaque(false);
        body.add(statusActionPanel, BorderLayout.NORTH);
        body.add(commentComposer, BorderLayout.CENTER);

        UiTheme.styleCombo(assigneeDropBox);
        UiTheme.styleCombo(statusDropBox);
        UiTheme.stylePrimaryButton(assignButton);
        UiTheme.styleSecondaryButton(recommendButton);
        UiTheme.stylePrimaryButton(statusButton);
        UiTheme.styleDangerButton(deleteIssueButton);
        UiTheme.stylePrimaryButton(addCommentButton);

        add(topActions, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
    }

    public void onAssignIssue(Runnable handler) {
        assignIssueEvent.subscribe(handler);
    }

    public void onChangeIssueStatus(Runnable handler) {
        changeStatusEvent.subscribe(handler);
    }

    public void onAddIssueComment(Runnable handler) {
        addCommentEvent.subscribe(handler);
    }

    public void onRecommendAssignee(Runnable handler) {
        recommendEvent.subscribe(handler);
    }

    public void onDeleteIssue(Runnable handler) {
        deleteIssueEvent.subscribe(handler);
    }

    public Integer getAssigneeUserIdInput() {
        AssigneeOption selected = (AssigneeOption) assigneeDropBox.getSelectedItem();
        return selected == null ? null : selected.userId;
    }

    public String getIssueCommentInput() {
        return commentArea.getText().trim();
    }

    public IssueStatus getTargetIssueStatusInput() {
        return (IssueStatus) statusDropBox.getSelectedItem();
    }

    public void clearCommentInput() {
        commentArea.setText("");
    }

    public void setAssigneeCandidates(List<IssueView.AssigneeCandidate> candidates) {
        this.assigneeCandidates = new ArrayList<>(candidates);
        refreshAssigneeDropBox(null);
    }

    // 담당자 선택 박스 갱신
    public void refreshAssigneeDropBox(Integer assigneeUserId) {
        assigneeDropBox.removeAllItems();
        AssigneeOption selected = null;

        for (IssueView.AssigneeCandidate candidate : assigneeCandidates) {
            AssigneeOption option = new AssigneeOption(
                    candidate.userId(),
                    candidate.loginId() + " (#" + candidate.userId() + ")"
            );
            assigneeDropBox.addItem(option);
            if (assigneeUserId != null && assigneeUserId.equals(candidate.userId())) {
                selected = option;
            }
        }

        if (selected != null) {
            assigneeDropBox.setSelectedItem(selected);
        }
    }

    public String resolveAssigneeDisplay(Integer assigneeUserId) {
        if (assigneeUserId == null) {
            return "-";
        }

        for (IssueView.AssigneeCandidate candidate : assigneeCandidates) {
            if (assigneeUserId.equals(candidate.userId())) {
                return candidate.loginId();
            }
        }
        return "#" + assigneeUserId;
    }

    @Override
    public void setAssignSectionVisible(boolean visible) {
        assigneeLabel.setVisible(visible);
        assigneeDropBox.setVisible(visible);
        assignButton.setVisible(visible);
    }

    @Override
    public void setRecommendButtonVisible(boolean visible) {
        recommendButton.setVisible(visible);
    }

    @Override
    public void setStatusSectionVisible(boolean visible) {
        statusLabel.setVisible(visible);
        statusDropBox.setVisible(visible);
        statusButton.setVisible(visible);
    }

    @Override
    public void setDeleteButtonVisible(boolean visible) {
        deleteIssueButton.setVisible(visible);
    }

    @Override
    public void setCommentSectionVisible(boolean visible) {
        commentArea.setEnabled(visible);
        commentArea.setEditable(visible);
        addCommentButton.setVisible(visible);
    }

    @Override
    public void setStatusOptions(List<IssueStatus> statuses) {
        statusDropBox.removeAllItems();
        for (IssueStatus status : statuses) {
            statusDropBox.addItem(status);
        }
    }
}
