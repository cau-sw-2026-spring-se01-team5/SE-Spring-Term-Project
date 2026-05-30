package app;

import auth.dto.login.v1.LoginInput;
import auth.dto.login.v1.LoginOutput;
import auth.v1.Auth;
import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;
import issue.dto.getIssueList.v1.GetIssueListInput;
import issue.dto.registerIssue.v1.RegisterIssueInput;
import issue.v1.Issue;
import org.junit.jupiter.api.Test;
import statistics.dto.countByStatus.v1.CountByStatusInput;
import statistics.dto.getDailyIssueCounts.v1.GetDailyIssueCountsInput;
import statistics.v1.Statistics;
import user.dto.createUser.v1.CreateUserInput;
import user.v1.User;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// 통계 조회 계약을 별도 파일에서 검증한다.
class JavaFxStatisticsJunitTest {

    @Test
    void statisticsTest() throws Exception {
        JavaFxServices services = JavaFxTestSupport.mockServices();
        Auth auth = services.auth();
        User user = services.user();
        Issue issue = services.issue();
        Statistics statistics = services.statistics();

        LoginOutput adminLogin = auth.login(new LoginInput("admin", "1234"));
        assertTrue(adminLogin.success());

        var testerCreate = user.createUser(new CreateUserInput(
                adminLogin.userId(),
                "tester-stat",
                "1234",
                UserRole.TESTER,
                1
        ));
        assertTrue(testerCreate.success());

        var register = issue.registerIssue(new RegisterIssueInput(
                1,
                "statistics issue",
                "mock statistics verification",
                IssuePriority.MAJOR,
                testerCreate.createdUserId()
        ));
        assertTrue(register.success());

        var list = issue.getIssueList(new GetIssueListInput(
                1,
                adminLogin.userId(),
                null,
                null,
                null,
                null,
                null,
                null
        ));
        assertTrue(list.success());
        assertNotNull(list.issues());
        assertFalse(list.issues().isEmpty());

        long newCount = list.issues().stream()
                .filter(summary -> summary.status() == IssueStatus.NEW)
                .count();
        assertTrue(newCount >= 1);

        var countByStatus = statistics.countByStatus(new CountByStatusInput(1, IssueStatus.NEW));
        assertTrue(countByStatus.success());
        assertTrue(countByStatus.count() >= 1);

        var dailyCounts = statistics.getDailyIssueCounts(new GetDailyIssueCountsInput(1));
        assertTrue(dailyCounts.success());
        assertFalse(dailyCounts.counts().isEmpty());

        Map<String, Long> daily = list.issues().stream()
                .collect(Collectors.groupingBy(
                        summary -> summary.reportedDate().toLocalDate().toString(),
                        Collectors.counting()
                ));
        assertFalse(daily.isEmpty());
    }
}
