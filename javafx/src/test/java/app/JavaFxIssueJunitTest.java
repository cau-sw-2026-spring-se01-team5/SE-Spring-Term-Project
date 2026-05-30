package app;

import auth.dto.login.v1.LoginInput;
import auth.dto.login.v1.LoginOutput;
import auth.v1.Auth;
import enums.issue.v1.IssuePriority;
import enums.user.v1.UserRole;
import issue.dto.getIssueDetail.v1.GetIssueDetailInput;
import issue.dto.getIssueList.v1.GetIssueListInput;
import issue.dto.registerIssue.v1.RegisterIssueInput;
import issue.v1.Issue;
import org.junit.jupiter.api.Test;
import user.dto.createUser.v1.CreateUserInput;
import user.v1.User;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

// 이슈 등록과 목록/상세 조회 계약을 검증한다.
class JavaFxIssueJunitTest {

    @Test
    void issueTest() throws Exception {
        JavaFxServices services = JavaFxTestSupport.mockServices();
        Auth auth = services.auth();
        User user = services.user();
        Issue issue = services.issue();

        LoginOutput adminLogin = auth.login(new LoginInput("admin", "1234"));
        assertTrue(adminLogin.success());

        var testerCreate = user.createUser(new CreateUserInput(
                adminLogin.userId(),
                "tester-it",
                "1234",
                UserRole.TESTER,
                1
        ));

        assertTrue(testerCreate.success());
        assertNotNull(testerCreate.createdUserId());

        var register = issue.registerIssue(new RegisterIssueInput(
                1,
                "javafx issue test",
                "mock issue registration check",
                IssuePriority.MAJOR,
                testerCreate.createdUserId()
        ));

        assertTrue(register.success());
        assertNotNull(register.issueId());

        var detail = issue.getIssueDetail(new GetIssueDetailInput(register.issueId()));
        assertTrue(detail.success());
        assertEquals("javafx issue test", detail.issueTitle());

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
        assertTrue(list.issues().stream().anyMatch(summary -> summary.issueId().equals(register.issueId())));
    }
}
