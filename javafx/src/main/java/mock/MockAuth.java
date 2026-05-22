package mock;

import auth.dto.login.v1.LoginInput;
import auth.dto.login.v1.LoginOutput;
import auth.v1.Auth;
import mock.model.MockUserData;

public class MockAuth implements Auth {

    private final MockDatabase database;

    public MockAuth(MockDatabase database) {
        this.database = database;
    }

    @Override
    public LoginOutput login(LoginInput input) {
        if (input.loginId() == null || input.loginId().isBlank()) {
            return new LoginOutput(false, null, "아이디를 입력하세요.");
        }

        if (input.password() == null || input.password().isBlank()) {
            return new LoginOutput(false, null, "비밀번호를 입력하세요.");
        }

        for (MockUserData user : database.users().values()) {
            if (user.loginId().equals(input.loginId())
                    && user.password().equals(input.password())) {
                return new LoginOutput(true, user.userId(), "로그인 성공");
            }
        }

        return new LoginOutput(false, null, "아이디 또는 비밀번호가 올바르지 않습니다.");
    }

    @Override
    public void logout() {
    }
}