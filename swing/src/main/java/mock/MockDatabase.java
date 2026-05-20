package mock;

import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;
import mock.model.MockIssueData;
import mock.model.MockProjectData;
import mock.model.MockUserData;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class MockDatabase {

    private int nextProjectId = 2;
    private int nextUserId = 10;
    private int nextIssueId = 2;
    private int nextCommentId = 1000;

    private final Map<Integer, MockUserData> users = new LinkedHashMap<>();
    private final Map<Integer, MockProjectData> projects = new LinkedHashMap<>();
    private final Map<Integer, MockIssueData> issues = new LinkedHashMap<>();

    public MockDatabase() {
        projects.put(1, new MockProjectData(1, "project1"));

        users.put(1, new MockUserData(1, "admin", "1234", UserRole.ADMIN, 1));
        users.put(2, new MockUserData(2, "pl1", "1234", UserRole.PL, 1));
        users.put(3, new MockUserData(3, "dev1", "1234", UserRole.DEV, 1));
        users.put(4, new MockUserData(4, "tester1", "1234", UserRole.TESTER, 1));
        users.put(5, new MockUserData(5, "dev2", "1234", UserRole.DEV, 1));
        users.put(6, new MockUserData(6, "tester2", "1234", UserRole.TESTER, 1));

        MockIssueData issue = new MockIssueData(
                1,
                1,
                "로그인 버튼 클릭 시 오류",
                "로그인 버튼 클릭 시 NullPointerException 발생",
                4,
                LocalDateTime.now().minusDays(1),
                null,
                null,
                IssuePriority.MAJOR,
                IssueStatus.NEW
        );

        issues.put(issue.issueId(), issue);
    }

    public Map<Integer, MockUserData> users() {
        return users;
    }

    public Map<Integer, MockProjectData> projects() {
        return projects;
    }

    public Map<Integer, MockIssueData> issues() {
        return issues;
    }

    public int nextProjectId() {
        return nextProjectId++;
    }

    public int nextUserId() {
        return nextUserId++;
    }

    public int nextIssueId() {
        return nextIssueId++;
    }

    public int nextCommentId() {
        return nextCommentId++;
    }
}
