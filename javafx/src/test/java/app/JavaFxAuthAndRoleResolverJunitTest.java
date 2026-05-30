package app;

import auth.dto.login.v1.LoginInput;
import auth.dto.login.v1.LoginOutput;
import auth.v1.Auth;
import enums.user.v1.UserRole;
import org.junit.jupiter.api.Test;
import user.v1.RoleResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// 로그인과 권한 조회 계약을 따로 검증한다.
class JavaFxAuthAndRoleResolverJunitTest {

    @Test
    void loginAndRoleResolverTest() throws Exception {
        JavaFxServices services = JavaFxTestSupport.mockServices();
        Auth auth = services.auth();
        RoleResolver roleResolver = services.roleResolver();

        LoginOutput output = auth.login(new LoginInput("admin", "1234"));

        assertTrue(output.success());
        assertNotNull(output.userId());
        assertEquals(UserRole.ADMIN, roleResolver.resolveRole(output.userId()));
        assertEquals("admin", roleResolver.resolveLoginId(output.userId()));
    }
}
