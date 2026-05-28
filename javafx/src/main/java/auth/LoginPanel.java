package auth;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.JavaFxData.LoginUser;

import java.util.function.Consumer;

// 로그인 화면 UI와 입력 이벤트만 담당한다.
public class LoginPanel extends BorderPane implements LoginView {

    private final TextField idField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Label messageLabel = new Label();
    private Consumer<LoginUser> loginSuccessHandler;
    private Runnable loginHandler;

    public LoginPanel() {
        setStyle("-fx-background-color: #f4f6f8;");

        VBox card = new VBox(18);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setMaxWidth(420);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: #d0d7de;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.14), 16, 0, 0, 4);"
        );

        Label title = new Label("이슈 관리 시스템");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label subtitle = new Label("기본 관리자 계정: admin / 1234");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");

        idField.setPromptText("아이디");
        idField.setPrefHeight(40);

        passwordField.setPromptText("비밀번호");
        passwordField.setPrefHeight(40);

        messageLabel.setStyle("-fx-text-fill: #dc2626;");

        Button loginButton = new Button("로그인");
        loginButton.setPrefHeight(42);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle(
                "-fx-background-color: #2563eb;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;"
        );
        loginButton.setOnAction(event -> {
            if (loginHandler != null) {
                loginHandler.run();
            }
        });

        card.getChildren().addAll(title, subtitle, idField, passwordField, loginButton, messageLabel);

        StackPane center = new StackPane(card);
        center.setPadding(new Insets(40));
        setCenter(center);
    }

    @Override
    public String getLoginId() {
        return idField.getText().trim();
    }

    @Override
    public String getPassword() {
        return passwordField.getText();
    }

    @Override
    public void showMessage(String message) {
        messageLabel.setText(message);
    }

    @Override
    public void clearPassword() {
        passwordField.clear();
    }

    @Override
    public void onLogin(Runnable handler) {
        this.loginHandler = handler;
    }

    @Override
    public void onLoginSuccess(Consumer<LoginUser> handler) {
        this.loginSuccessHandler = handler;
    }

    public void moveToMain(LoginUser user) {
        if (loginSuccessHandler != null) {
            loginSuccessHandler.accept(user);
        }
    }
}
