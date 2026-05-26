package auth;

import app.JavaFxServices;
import auth.dto.login.v1.LoginInput;
import auth.dto.login.v1.LoginOutput;
import model.JavaFxData.LoginUser;

import java.util.function.Consumer;

public class LoginController {

    private final LoginView view;
    private final JavaFxServices services;

    public LoginController(LoginView view, JavaFxServices services, Consumer<LoginUser> successHandler) {
        this.view = view;
        this.services = services;
        this.view.onLoginSuccess(successHandler);
        bind();
    }

    private void bind() {
        view.onLogin(this::login);
    }

    private void login() {
        String loginId = view.getLoginId();
        String password = view.getPassword();

        if (loginId.isBlank() || password.isBlank()) {
            view.showMessage("아이디와 비밀번호를 입력하세요.");
            return;
        }

        LoginOutput output = services.auth().login(new LoginInput(loginId, password));
        if (!output.success() || output.userId() == null) {
            view.showMessage(output.message() == null || output.message().isBlank()
                    ? "아이디 또는 비밀번호가 올바르지 않습니다."
                    : output.message());
            view.clearPassword();
            return;
        }

        LoginUser user = new LoginUser(
                output.userId(),
                services.roleResolver().resolveLoginId(output.userId()),
                services.roleResolver().resolveRole(output.userId())
        );

        if (view instanceof LoginPanel panel) {
            panel.moveToMain(user);
        }
    }
}
