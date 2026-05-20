package main.issue;

import issue.dto.getIssueList.v1.IssueSummaryOutput;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

// 이슈 보여줄 테이블
public class IssueTableModel extends AbstractTableModel {

    private final String[] columns = {
            "ID", "Title", "Reporter", "Assignee", "Fixer", "Priority", "Status", "Reported Date"
    };

    private List<IssueSummaryOutput> issues = new ArrayList<>();

    public void setIssues(List<IssueSummaryOutput> issues) {
        this.issues = new ArrayList<>(issues);
        fireTableDataChanged();
    }

    public IssueSummaryOutput getIssueAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= issues.size()) {
            return null;
        }
        return issues.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return issues.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        IssueSummaryOutput issue = issues.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> issue.issueId();
            case 1 -> issue.issueTitle();
            case 2 -> issue.reporterUserId();
            case 3 -> issue.assigneeUserId();
            case 4 -> issue.fixerUserId();
            case 5 -> issue.priority();
            case 6 -> issue.status();
            case 7 -> issue.reportedDate();
            default -> "";
        };
    }
}