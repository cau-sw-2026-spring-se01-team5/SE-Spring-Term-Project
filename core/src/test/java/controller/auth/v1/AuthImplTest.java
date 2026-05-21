package controller.auth.v1;

import auth.dto.login.v1.LoginInput;
import auth.dto.login.v1.LoginOutput;
import domain.User;
import enums.user.v1.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthImplTest {
    AuthImpl authImpl;
    User user;

    @BeforeEach
    void setUp() throws Exception {
        user = new User("tester01", "pass1234", UserRole.TESTER);
        user.setId(1);

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.byLoginId(user.getLoginId())).thenReturn(user);
        when(userRepository.byLoginId("unknown")).thenReturn(null);

        authImpl = new AuthImpl(userRepository);
    }

    @Test
    void login() {
        LoginOutput output = authImpl.login(new LoginInput("tester01", "pass1234"));

        assertEquals(true, output.success());
        assertEquals(user.getId(), output.userId());
    }

    @Test
    void loginWithWrongPassword() {
        LoginOutput output = authImpl.login(new LoginInput("tester01", "wrongpw"));

        assertEquals(false, output.success());
        assertNull(output.userId());
    }

    @Test
    void loginWithUnknownId() {
        LoginOutput output = authImpl.login(new LoginInput("unknown", "pass1234"));

        assertEquals(false, output.success());
        assertNull(output.userId());
    }

    @Test
    void loginWithBlankId() {
        LoginOutput output = authImpl.login(new LoginInput("", "pass1234"));

        assertEquals(false, output.success());
    }

    @Test
    void loginWithBlankPassword() {
        LoginOutput output = authImpl.login(new LoginInput("tester01", ""));

        assertEquals(false, output.success());
    }
}
