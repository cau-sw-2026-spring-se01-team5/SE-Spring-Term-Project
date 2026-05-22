package main.header;

import enums.user.v1.UserRole;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/*
 * 메인 화면 왼쪽 메뉴를 담당하는 JavaFX Panel이다.
 *
 * 이 클래스는 버튼과 라벨 같은 화면 구성만 가진다.
 * 어느 버튼을 눌렀을 때 어떤 화면으로 이동할지는 HeaderController가 결정한다.
 */
public class HeaderPanel extends VBox implements HeaderView {

    private final Label subtitleLabel = new Label();
    private final Button dashboardButton = menuButton("대시보드");
    private final Button issueButton = menuButton("이슈 관리");
    private final Button projectButton = menuButton("프로젝트/계정");
    private final Button logoutButton = menuButton("로그아웃");

    public HeaderPanel() {
        setSpacing(16);
        setPadding(new Insets(30, 20, 30, 20));
        setPrefWidth(240);
        setStyle("-fx-background-color: #111827;");

        Label logo = new Label("ITS");
        logo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #9ca3af;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(logo, subtitleLabel, dashboardButton, issueButton, projectButton, spacer, logoutButton);
    }

    @Override
    public void setUserInfo(String loginId, UserRole role) {
        subtitleLabel.setText(loginId + " / " + roleText(role));
        projectButton.setText(role == UserRole.ADMIN ? "프로젝트/계정 관리" : "프로젝트 정보");
    }

    @Override
    public void onDashboard(Runnable handler) {
        dashboardButton.setOnAction(event -> handler.run());
    }

    @Override
    public void onIssue(Runnable handler) {
        issueButton.setOnAction(event -> handler.run());
    }

    @Override
    public void onProject(Runnable handler) {
        projectButton.setOnAction(event -> handler.run());
    }

    @Override
    public void onLogout(Runnable handler) {
        logoutButton.setOnAction(event -> handler.run());
    }

    private static Button menuButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(42);
        button.setStyle("-fx-background-color: #1f2937; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-cursor: hand;");
        return button;
    }

    private String roleText(UserRole role) {
        return switch (role) {
            case ADMIN -> "관리자";
            case PL -> "PL";
            case DEV -> "개발자";
            case TESTER -> "테스터";
        };
    }
}
