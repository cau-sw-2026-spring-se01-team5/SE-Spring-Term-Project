package auth;

import model.JavaFxData.LoginUser;

import java.util.function.Consumer;

public interface LoginView {

    String getLoginId();

    String getPassword();

    void showMessage(String message);

    void clearPassword();

    void onLogin(Runnable handler);

    void onLoginSuccess(Consumer<LoginUser> handler);
}
