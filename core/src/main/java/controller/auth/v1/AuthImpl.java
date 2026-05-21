package controller.auth.v1;

import auth.dto.login.v1.LoginInput;
import auth.dto.login.v1.LoginOutput;
import auth.v1.Auth;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import repository.UserRepository;

@RequiredArgsConstructor
public class AuthImpl implements Auth {
    @NonNull
    private UserRepository userRepository;

    @Override
    public LoginOutput login(LoginInput input) {
        if (input.loginId() == null || input.loginId().isBlank()) {
            return new LoginOutput(false, null, "아이디를 입력해야 합니다.");
        }

        if (input.password() == null || input.password().isBlank()) {
            return new LoginOutput(false, null, "비밀번호를 입력해야 합니다.");
        }

        try {
            domain.User user = userRepository.byLoginId(input.loginId());
            if (user == null || !user.getPassword().equals(input.password())) {
                return new LoginOutput(false, null, "아이디 또는 비밀번호가 올바르지 않습니다.");
            }
            return new LoginOutput(true, user.getId(), "로그인 성공");
        } catch (Exception e) {
            return new LoginOutput(false, null, e.getMessage());
        }
    }

    @Override
    public void logout() {
    }
}
