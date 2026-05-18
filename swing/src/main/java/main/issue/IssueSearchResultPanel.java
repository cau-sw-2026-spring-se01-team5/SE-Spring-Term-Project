package main.issue;

import issue.dto.getIssueList.v1.IssueSummaryOutput;
import ui.UiTheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// 검색 결과 목록을 팝업 테이블로 보여줌
class IssueSearchResultPanel {

    private final Component owner;

    IssueSearchResultPanel(Component owner) {
        this.owner = owner;
    }

    Integer show(List<IssueSummaryOutput> issues) {
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
                owner,
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
            return -1;
        }

        int modelRow = table.convertRowIndexToModel(row);
        IssueSummaryOutput selected = popupTableModel.getIssueAt(modelRow);
        return selected == null ? null : selected.issueId();
    }
}
