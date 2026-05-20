package main;

import enums.user.v1.UserRole;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/*
 * 로그인 이후 공통 메인 화면 틀이다.
 *
 * Swing의 MainPanel처럼 사이드바와 본문 영역을 가진다.
 * 각 기능 화면은 center 영역에 교체되어 들어간다.
 */
public class MainPanel extends BorderPane {

    private final Label subtitleLabel = new Label();
    private final Button dashboardButton = menuButton("대시보드");
    private final Button issueButton = menuButton("이슈 관리");
    private final Button projectButton = menuButton("프로젝트/계정");
    private final Button logoutButton = menuButton("로그아웃");

    public MainPanel() {
        setStyle("-fx-background-color: #f4f6f8;");
        setLeft(createSidebar());
    }

    public void setUserInfo(String loginId, UserRole role) {
        subtitleLabel.setText(loginId + " / " + roleText(role));
        projectButton.setText(role == UserRole.ADMIN ? "프로젝트/계정 관리" : "프로젝트 정보");
    }

    public void setContent(Node node) {
        setCenter(node);
    }

    public void onDashboard(Runnable handler) {
        dashboardButton.setOnAction(e -> handler.run());
    }

    public void onIssue(Runnable handler) {
        issueButton.setOnAction(e -> handler.run());
    }

    public void onProject(Runnable handler) {
        projectButton.setOnAction(e -> handler.run());
    }

    public void onLogout(Runnable handler) {
        logoutButton.setOnAction(e -> handler.run());
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(16);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: #111827;");

        Label logo = new Label("ITS");
        logo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #9ca3af;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(logo, subtitleLabel, dashboardButton, issueButton, projectButton, spacer, logoutButton);
        return sidebar;
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
