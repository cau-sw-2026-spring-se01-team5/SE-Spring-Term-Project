package view.login;

import app.BackendProvider;
import app.JavaFxBackend;
import app.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/*
 * 로그인 화면이다.
 *
 * 사용자는 아이디와 비밀번호만 입력한다.
 * 실제 사용자 조회는 JavaFxBackend 인터페이스를 통해 처리한다.
 */
public class LoginView extends BorderPane {

    private final JavaFxBackend backend = BackendProvider.backend();

    public LoginView() {
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

        Label subtitle = new Label("초기 관리자 계정: admin / admin");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");

        TextField idField = new TextField();
        idField.setPromptText("아이디");
        idField.setPrefHeight(40);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("비밀번호");
        passwordField.setPrefHeight(40);

        Label messageLabel = new Label();
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

        loginButton.setOnAction(e -> {
            String loginId = idField.getText().trim();
            String password = passwordField.getText();

            if (loginId.isBlank() || password.isBlank()) {
                messageLabel.setText("아이디와 비밀번호를 입력하세요.");
                return;
            }

            backend.login(loginId, password).ifPresentOrElse(
                    user -> SceneManager.login(user.loginId(), user.role()),
                    () -> messageLabel.setText("아이디 또는 비밀번호가 올바르지 않습니다.")
            );
        });

        card.getChildren().addAll(title, subtitle, idField, passwordField, loginButton, messageLabel);

        StackPane center = new StackPane(card);
        center.setPadding(new Insets(40));
        setCenter(center);
    }
}
