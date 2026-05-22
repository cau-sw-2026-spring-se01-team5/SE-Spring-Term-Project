package auth;

import backend.JavaFxBackend;

import java.util.function.Consumer;

/*
 * 로그인 화면의 이벤트 처리를 담당하는 컨트롤러이다.
 *
 * Panel은 입력값을 제공하고 메시지를 표시하는 역할만 맡는다.
 * 실제 로그인 요청과 성공/실패 판단은 이 컨트롤러가 JavaFxBackend를 통해 처리한다.
 */
public class LoginController {

    private final LoginView view;
    private final JavaFxBackend backend;

    public LoginController(LoginView view, JavaFxBackend backend, Consumer<JavaFxBackend.LoginUser> successHandler) {
        this.view = view;
        this.backend = backend;
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

        backend.login(loginId, password).ifPresentOrElse(
                user -> {
                    if (view instanceof LoginPanel panel) {
                        panel.moveToMain(user);
                    }
                },
                () -> {
                    view.showMessage("아이디 또는 비밀번호가 올바르지 않습니다.");
                    view.clearPassword();
                }
        );
    }
}
