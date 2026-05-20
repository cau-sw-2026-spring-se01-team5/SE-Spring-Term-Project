package auth;

import app.JavaFxBackend;

import java.util.function.Consumer;

/*
 * 로그인 UI와 LoginController 사이의 인터페이스이다.
 *
 * Swing의 LoginView처럼 컨트롤러가 구체 JavaFX 노드를 직접 알지 않고,
 * 필요한 입력값과 이벤트 연결 메서드만 사용하게 하기 위해 둔다.
 */
public interface LoginView {

    String getLoginId();

    String getPassword();

    void showMessage(String message);

    void clearPassword();

    void onLogin(Runnable handler);

    void onLoginSuccess(Consumer<JavaFxBackend.LoginUser> handler);
}
