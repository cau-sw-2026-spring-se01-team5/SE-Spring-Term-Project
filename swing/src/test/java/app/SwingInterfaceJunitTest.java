package app;

import auth.dto.login.v1.LoginInput;
import auth.dto.login.v1.LoginOutput;
import auth.v1.Auth;
import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;
import issue.dto.getIssueDetail.v1.GetIssueDetailInput;
import issue.dto.getIssueList.v1.GetIssueListInput;
import issue.dto.registerIssue.v1.RegisterIssueInput;
import issue.v1.Issue;
import org.junit.jupiter.api.Test;
import project.dto.getProjectList.v1.GetProjectListInput;
import project.v1.Project;
import user.dto.createUser.v1.CreateUserInput;
import user.dto.getUserInfo.v1.GetUserInfoInput;
import user.v1.RoleResolver;
import user.v1.User;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Swing과 연결되는 공용 mock 서비스 계약을 검증한다.
class SwingInterfaceJunitTest {

    @Test
    void loginAndRoleResolverTest() throws Exception {
        // mock 로그인과 권한 조회가 정상 동작하는지 확인한다.
        AppServices services = new MockAppWiring().wire();

        Auth auth = services.auth();
        RoleResolver roleResolver = services.roleResolver();

        LoginOutput output = auth.login(new LoginInput("admin", "1234"));

        assertTrue(output.success());
        assertNotNull(output.userId());
        assertEquals(UserRole.ADMIN, roleResolver.resolveRole(output.userId()));
        assertEquals("admin", roleResolver.resolveLoginId(output.userId()));
    }

    @Test
    void projectAndUserTest() throws Exception {
        // mock 프로젝트 목록과 사용자 정보 조회를 검증한다.
        AppServices services = new MockAppWiring().wire();

        Auth auth = services.auth();
        Project project = services.project();
        User user = services.user();

        LoginOutput login = auth.login(new LoginInput("admin", "1234"));
        assertTrue(login.success());

        var projectList = project.getProjectList(new GetProjectListInput(login.userId()));
        assertTrue(projectList.success());
        assertNotNull(projectList.projectList());
        assertTrue(projectList.projectList().stream().anyMatch(p -> p.projectId() == 1));

        var userInfo = user.getUserInfo(new GetUserInfoInput(login.userId(), 1));
        assertTrue(userInfo.success());
        assertEquals("admin", userInfo.loginId());
        assertEquals(UserRole.ADMIN, userInfo.role());
    }

    @Test
    void issueTest() throws Exception {
        // mock 이슈 등록, 상세 조회, 목록 조회 흐름을 검증한다.
        AppServices services = new MockAppWiring().wire();

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
                "swing issue test",
                "mock issue registration check",
                IssuePriority.MAJOR,
                testerCreate.createdUserId()
        ));

        assertTrue(register.success());
        assertNotNull(register.issueId());

        var detail = issue.getIssueDetail(new GetIssueDetailInput(register.issueId()));
        assertTrue(detail.success());
        assertEquals("swing issue test", detail.issueTitle());

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
        assertTrue(list.issues().stream().anyMatch(i -> i.issueId().equals(register.issueId())));
    }

    @Test
    void statisticsTest() throws Exception {
        // Swing 통계는 현재 보이는 이슈 목록 집계 방식이므로 mock 목록 기준으로 검증한다.
        AppServices services = new MockAppWiring().wire();

        Auth auth = services.auth();
        User user = services.user();
        Issue issue = services.issue();

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

        Map<String, Long> daily = list.issues().stream()
                .collect(Collectors.groupingBy(
                        summary -> summary.reportedDate().toLocalDate().toString(),
                        Collectors.counting()
                ));
        assertFalse(daily.isEmpty());
    }
}
