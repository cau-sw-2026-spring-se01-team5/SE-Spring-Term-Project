package auth;

import model.JavaFxData.LoginUser;

import java.util.function.Consumer;

// 로그인 화면이 컨트롤러에 제공해야 할 최소 인터페이스.
public interface LoginView {

    String getLoginId();

    String getPassword();

    void showMessage(String message);

    void clearPassword();

    void onLogin(Runnable handler);

    void onLoginSuccess(Consumer<LoginUser> handler);
}
