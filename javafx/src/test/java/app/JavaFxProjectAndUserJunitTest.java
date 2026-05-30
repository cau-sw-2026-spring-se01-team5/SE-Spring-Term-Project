package app;

import auth.dto.login.v1.LoginInput;
import auth.dto.login.v1.LoginOutput;
import auth.v1.Auth;
import enums.user.v1.UserRole;
import org.junit.jupiter.api.Test;
import project.dto.getProjectList.v1.GetProjectListInput;
import project.v1.Project;
import user.dto.getUserInfo.v1.GetUserInfoInput;
import user.v1.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// 프로젝트 목록과 사용자 정보 조회 계약을 검증한다.
class JavaFxProjectAndUserJunitTest {

    @Test
    void projectAndUserTest() throws Exception {
        JavaFxServices services = JavaFxTestSupport.mockServices();
        Auth auth = services.auth();
        Project project = services.project();
        User user = services.user();

        LoginOutput login = auth.login(new LoginInput("admin", "1234"));
        assertTrue(login.success());

        var projectList = project.getProjectList(new GetProjectListInput(login.userId()));
        assertTrue(projectList.success());
        assertNotNull(projectList.projectList());
        assertTrue(projectList.projectList().stream().anyMatch(projectInfo -> projectInfo.projectId() == 1));

        var userInfo = user.getUserInfo(new GetUserInfoInput(login.userId(), 1));
        assertTrue(userInfo.success());
        assertEquals("admin", userInfo.loginId());
        assertEquals(UserRole.ADMIN, userInfo.role());
    }
}
