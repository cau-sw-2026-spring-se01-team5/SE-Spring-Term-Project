package repository.sqlite;

import domain.Issue;
import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import repository.IssueFilter;
import repository.IssueRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class SqliteIssueRepositoryTest {

    @Test
    void save() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:test.db");
        IssueRepository repository = new SqliteIssueRepository(connection);

        Issue issue = new Issue(1, "NullPointerException 발생", "로그인 시 NPE", IssuePriority.MAJOR, IssueStatus.NEW);
        issue.setReporterId(1);
        issue.setReportedDate(LocalDateTime.now());

        Integer id = repository.save(issue);
        assertNotNull(id);
    }

    @Test
    void load() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:test.db");
        IssueRepository repository = new SqliteIssueRepository(connection);

        Issue issue = repository.load(1);
        assertNotNull(issue);
        System.out.println(issue.getTitle() + " / " + issue.getStatus());
    }

    @Test
    void search() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:test.db");
        IssueRepository repository = new SqliteIssueRepository(connection);

        List<Issue> issues = repository.search(new IssueFilter(1, null, null, null, null, null, null));
        assertFalse(issues.isEmpty());
        System.out.println("검색 결과: " + issues.size() + "건");
    }

    @Test
    void searchByKeyword() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:test.db");
        IssueRepository repository = new SqliteIssueRepository(connection);

        List<Issue> issues = repository.search(new IssueFilter(null, null, null, null, null, null, "NPE"));
        System.out.println("키워드 검색 결과: " + issues.size() + "건");
    }
}
