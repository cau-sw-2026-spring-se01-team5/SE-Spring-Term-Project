package main.issue;

import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;
import issue.dto.getIssueDetail.v1.GetIssueDetailOutput;
import issue.dto.recommendAssignee.v1.RecommendAssigneeOutput;
import main.issue.role.FindRightIssuePolicy;
import ui.UiTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

// 이슈 상세 보기 관련 UI
public class IssueDetailPanel extends JPanel {

    // 접속한 유저에 맞는 권한 찾아주기
    private final FindRightIssuePolicy findRightIssuePolicy = new FindRightIssuePolicy();

    private UserRole currentRole; // 현재 접속한 사용자 권한
    private JDialog detailPopup; // 이슈 상세 팝업 창
    private JLabel headerLabel; // 최상단 헤더
    private IssueDetailMetaPanel metaPanel; // 이슈 기본 정보 카드
    private IssueCommentsPanel commentsPanel; // 댓글 목록 카드
    private IssueActionPanel actionPanel; // 이슈 관련 액션 카드
    private Integer activeDetailIssueId; // 지금 상세 보기 팝업에서 보고 있는 이슈 ID

    // 지금 팝업에서 보고 있는 이슈ID를 반환
    public Integer getActiveDetailIssueId() {
        return activeDetailIssueId;
    }

    // 지금 팝업에서 선택된 담당자 userId 가져오는 메서드
    public Integer getAssigneeUserIdInput() {
        return actionPanel == null ? null : actionPanel.getAssigneeUserIdInput();
    }

    // 댓글 입력창의 내용 가져오기
    public String getIssueCommentInput() {
        return actionPanel == null ? "" : actionPanel.getIssueCommentInput();
    }

    // 상태 변경 드롭박스에서 선택된 값 가져오기
    public IssueStatus getTargetIssueStatusInput() {
        return actionPanel == null ? null : actionPanel.getTargetIssueStatusInput();
    }

    // IssueController에서 배정 가능한 dev 목록 가져와서 UI 반영 시 호출
    public void setAssigneeCandidates(java.util.List<IssueView.AssigneeCandidate> candidates) {
        makeDetailPopup();
        actionPanel.setAssigneeCandidates(candidates);
    }

    // 이슈 상세 조회 결과 받아서 상세 팝업 띄우는 메서드
    public void showIssueDetail(GetIssueDetailOutput output) {
        if (!output.success()) {
            JOptionPane.showMessageDialog(this, output.message());
            return;
        }

        makeDetailPopup(); // 상세 보기 팝업 생성
        refreshDetailIssuePopup(output); // 팝업 내용을 받아온 결과로 갱신
        detailPopup.setVisible(true); // 팝업 띄우기
        detailPopup.toFront(); // 띄운 팝업을 제일 앞으로
        detailPopup.requestFocus(); // 띄운 팝업에 포커싱
        SwingUtilities.invokeLater(() -> actionPanel.requestFocusInWindow()); // swing 이벤트 처리 후 포커싱
    }

    // 담당자 추천 결과 보여주는 메서드
    public void showRecommendations(RecommendAssigneeOutput output) {
        if (!output.success()) {
            JOptionPane.showMessageDialog(this, output.message());
            return;
        }

        showRecommendationPopup(output.candidates());
    }

    // 현재 사용자 권한 저장 -> UI에 적용
    public void applyRole(UserRole role) {
        this.currentRole = role;
        applyRoleToActions();
    }

    // 이벤트 등록 메서드 -> controller가 메서드 등록할 수 있도록 함
    public void onAssignIssue(Runnable handler) {
        makeDetailPopup();
        actionPanel.onAssignIssue(handler);
    }

    public void onChangeIssueStatus(Runnable handler) {
        makeDetailPopup();
        actionPanel.onChangeIssueStatus(handler);
    }

    public void onAddIssueComment(Runnable handler) {
        makeDetailPopup();
        actionPanel.onAddIssueComment(handler);
    }

    public void onRecommendAssignee(Runnable handler) {
        makeDetailPopup();
        actionPanel.onRecommendAssignee(handler);
    }

    public void onDeleteIssue(Runnable handler) {
        makeDetailPopup();
        actionPanel.onDeleteIssue(handler);
    }

    // 팝업 열려 있으면 이거 기준으로 메세지 띄움
    public void showMessage(String message) {
        Component parent = (detailPopup != null && detailPopup.isShowing()) ? detailPopup : this;
        JOptionPane.showMessageDialog(parent, message);
    }

    // 상세 팝업 생성 + 배치
    private void makeDetailPopup() {
        if (detailPopup != null) {
            return;
        }

        Window owner = SwingUtilities.getWindowAncestor(this);
        detailPopup = new JDialog(owner, "Issue Detail", Dialog.ModalityType.MODELESS);
        detailPopup.setSize(900, 680);
        detailPopup.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(UiTheme.BG);
        root.setBorder(new EmptyBorder(14, 14, 14, 14));

        headerLabel = new JLabel();
        headerLabel.setForeground(Color.BLACK);

        metaPanel = new IssueDetailMetaPanel();
        commentsPanel = new IssueCommentsPanel();
        actionPanel = new IssueActionPanel();

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.add(metaPanel, BorderLayout.NORTH);
        center.add(commentsPanel, BorderLayout.CENTER);
        center.add(actionPanel, BorderLayout.SOUTH);

        root.add(headerLabel, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        detailPopup.setContentPane(root);
    }

    // 상세 화면 데이터를 최신 데이터로 갱신
    private void refreshDetailIssuePopup(GetIssueDetailOutput output) {
        activeDetailIssueId = output.issueId();
        headerLabel.setText("Issue #" + output.issueId() + "  " + output.issueTitle());

        String assigneeDisplay = actionPanel.resolveAssigneeDisplay(output.assigneeUserId());
        metaPanel.update(output, assigneeDisplay);
        commentsPanel.renderComments(output.comments());
        actionPanel.refreshAssigneeDropBox(output.assigneeUserId());
        actionPanel.clearCommentInput();

        applyRoleToActions();
    }

    // 지금 역할에 맞는 actions 버튼 갱신
    private void applyRoleToActions() {
        if (currentRole == null || detailPopup == null || actionPanel == null) {
            return;
        }
        findRightIssuePolicy.find(currentRole).apply(actionPanel);
    }

    private void showRecommendationPopup(List<issue.dto.recommendAssignee.v1.RecommendedAssigneeOutput> candidates) {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UiTheme.BG);
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("추천 담당자 목록");
        title.setForeground(Color.BLACK);
        title.setFont(new Font("Dialog", Font.BOLD, 18));
        root.add(title, BorderLayout.NORTH);

        String[] columns = {"순위", "추천자 ID"};
        Object[][] data;
        if (candidates == null || candidates.isEmpty()) {
            data = new Object[][]{{"-", "추천 결과 없음"}};
        } else {
            data = new Object[candidates.size()][2];
            for (int i = 0; i < candidates.size(); i++) {
                var candidate = candidates.get(i);
                data[i][0] = candidate.rank() + "위";
                data[i][1] = candidate.userId();
            }
        }

        JTable table = new JTable(data, columns);
        table.setEnabled(false);
        table.setRowHeight(30);
        UiTheme.styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(UiTheme.cardBorder(6));
        scroll.setPreferredSize(new Dimension(420, 180));
        root.add(scroll, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(
                this,
                root,
                "추천 결과",
                JOptionPane.PLAIN_MESSAGE
        );
    }
}
