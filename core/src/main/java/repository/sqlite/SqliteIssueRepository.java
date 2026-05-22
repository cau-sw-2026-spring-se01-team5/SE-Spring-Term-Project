package repository.sqlite;

import domain.Issue;
import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import repository.IssueFilter;
import repository.IssueRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SqliteIssueRepository implements IssueRepository {
    @NonNull
    private Connection connection;

    @Override
    public Integer save(Issue issue) throws Exception {
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );

        statement.setInt(1, issue.getProjectId());
        statement.setString(2, issue.getTitle());
        statement.setString(3, issue.getDescription());
        statement.setString(4, issue.getPriority().name());
        statement.setString(5, issue.getStatus().name());
        statement.setInt(6, issue.getReporterId());

        if (issue.getAssigneeId() != null) {
            statement.setInt(7, issue.getAssigneeId());
        } else {
            statement.setNull(7, Types.INTEGER);
        }

        if (issue.getFixerId() != null) {
            statement.setInt(8, issue.getFixerId());
        } else {
            statement.setNull(8, Types.INTEGER);
        }

        statement.setString(9, issue.getReportedDate().toString());
        statement.executeUpdate();

        ResultSet keys = statement.getGeneratedKeys();
        if (keys.next()) {
            return keys.getInt(1);
        }

        return null;
    }

    @Override
    public Issue load(Integer id) throws Exception {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date FROM issues WHERE id = ?"
        );

        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();

        if (rs.next()) {
            Issue issue = new Issue(
                    rs.getInt("project_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    IssuePriority.valueOf(rs.getString("priority")),
                    IssueStatus.valueOf(rs.getString("status"))
            );
            issue.setId(id);
            issue.setReporterId(rs.getInt("reporter_id"));

            int assigneeId = rs.getInt("assignee_id");
            issue.setAssigneeId(rs.wasNull() ? null : assigneeId);

            int fixerId = rs.getInt("fixer_id");
            issue.setFixerId(rs.wasNull() ? null : fixerId);

            issue.setReportedDate(LocalDateTime.parse(rs.getString("reported_date")));

            return issue;
        }

        return null;
    }

    @Override
    public void delete(Integer id) throws Exception {
        PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM issues WHERE id = ?"
        );
        statement.setInt(1, id);
        statement.executeUpdate();
    }

    @Override
    public void update(Issue issue) throws Exception {
        PreparedStatement statement = connection.prepareStatement(
                "UPDATE issues SET title = ?, description = ?, priority = ?, status = ?, assignee_id = ?, fixer_id = ? WHERE id = ?"
        );

        statement.setString(1, issue.getTitle());
        statement.setString(2, issue.getDescription());
        statement.setString(3, issue.getPriority().name());
        statement.setString(4, issue.getStatus().name());

        if (issue.getAssigneeId() != null) {
            statement.setInt(5, issue.getAssigneeId());
        } else {
            statement.setNull(5, Types.INTEGER);
        }

        if (issue.getFixerId() != null) {
            statement.setInt(6, issue.getFixerId());
        } else {
            statement.setNull(6, Types.INTEGER);
        }

        statement.setInt(7, issue.getId());
        statement.executeUpdate();
    }

    @Override
    public List<Issue> search(IssueFilter filter) throws Exception {
        StringBuilder sql = new StringBuilder(
                "SELECT id, project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date FROM issues WHERE 1=1"
        );

        if (filter.projectId() != null)
            sql.append(" AND project_id = ?");
        if (filter.assigneeId() != null)
            sql.append(" AND assignee_id = ?");
        if (filter.reporterId() != null)
            sql.append(" AND reporter_id = ?");
        if (filter.fixerId() != null)
            sql.append(" AND fixer_id = ?");
        if (filter.status() != null)
            sql.append(" AND status = ?");
        if (filter.priority() != null)
            sql.append(" AND priority = ?");
        if (filter.keyword() != null)
            sql.append(" AND (title LIKE ? OR description LIKE ?)");

        PreparedStatement statement = connection.prepareStatement(sql.toString());

        int idx = 1;
        if (filter.projectId() != null)
            statement.setInt(idx++, filter.projectId());
        if (filter.assigneeId() != null)
            statement.setInt(idx++, filter.assigneeId());
        if (filter.reporterId() != null)
            statement.setInt(idx++, filter.reporterId());
        if (filter.fixerId() != null)
            statement.setInt(idx++, filter.fixerId());
        if (filter.status() != null)
            statement.setString(idx++, filter.status().name());
        if (filter.priority() != null)
            statement.setString(idx++, filter.priority().name());
        if (filter.keyword() != null) {
            String like = "%" + filter.keyword() + "%";
            statement.setString(idx++, like);
            statement.setString(idx++, like);
        }

        ResultSet rs = statement.executeQuery();

        List<Issue> issues = new ArrayList<>();
        while (rs.next()) {
            Issue issue = new Issue(
                    rs.getInt("project_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    IssuePriority.valueOf(rs.getString("priority")),
                    IssueStatus.valueOf(rs.getString("status"))
            );
            issue.setId(rs.getInt("id"));
            issue.setReporterId(rs.getInt("reporter_id"));

            int assigneeId = rs.getInt("assignee_id");
            issue.setAssigneeId(rs.wasNull() ? null : assigneeId);

            int fixerId = rs.getInt("fixer_id");
            issue.setFixerId(rs.wasNull() ? null : fixerId);

            issue.setReportedDate(LocalDateTime.parse(rs.getString("reported_date")));

            issues.add(issue);
        }

        return issues;
    }
}
