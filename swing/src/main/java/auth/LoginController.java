package auth;

import auth.dto.login.v1.LoginInput;
import auth.dto.login.v1.LoginOutput;
import auth.v1.Auth;

/* 사용자 입력에 대해 로그인 요청, 결과 처리, 화면 전환 담당 */
public class LoginController {
    // 로그인 화면 인터페이스 등록
    // 구체적인 UI 구현 코드인 LoginPanel을 직접 등록하지 않음
    // 커플링 낮추는 부분
    private final LoginView view;

    // core 로직과 연결할 인터페이스 등록
    private final Auth auth;

    public LoginController(LoginView view, Auth auth) {
        this.view = view;
        this.auth = auth;
        bind();
    }
    // 버튼 이벤트와 Controller 메서드 연결
    private void bind() {
        view.onLogin(this::login);
    }

    // 실제 로그인 처리 로직
    private void login() {
        String loginId = view.getLoginId();
        String password = view.getPassword();
        LoginOutput output = auth.login(new LoginInput(loginId, password));

        if (output.success()) {
            view.moveToMainPage(output.userId());
            return;
        }
        view.showMessage(output.message());
        view.clearPassword();
    }
}