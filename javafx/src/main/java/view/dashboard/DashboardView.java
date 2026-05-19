package view.dashboard;

import app.BackendProvider;
import app.JavaFxBackend;
import app.SceneManager;
import enums.user.v1.UserRole;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/*
 * 로그인 후 표시되는 역할별 대시보드 화면이다.
 *
 * 이 화면은 요약 정보와 주요 기능 바로가기만 제공하고, 실제 데이터 조회는 JavaFxBackend에 위임한다.
 */
public class DashboardView extends BorderPane {

    private final String loginId;
    private final UserRole role;
    private final JavaFxBackend backend = BackendProvider.backend();

    public DashboardView(String loginId, UserRole role) {
        this.loginId = loginId;
        this.role = role;

        setStyle("-fx-background-color: #f4f6f8;");
        setLeft(createSidebar());
        setCenter(createContent());
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(16);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: #111827;");

        Label logo = new Label("ITS");
        logo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitle = new Label(roleText() + " 대시보드");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #9ca3af;");

        Button issueButton = menuButton("이슈 관리");
        issueButton.setOnAction(e -> SceneManager.showIssueListView());

        Button projectButton = menuButton(role == UserRole.ADMIN ? "프로젝트/계정 관리" : "프로젝트 정보");
        projectButton.setOnAction(e -> SceneManager.showProjectView());

        Button logoutButton = menuButton("로그아웃");
        logoutButton.setOnAction(e -> SceneManager.logout());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(logo, subtitle, issueButton, projectButton, spacer, logoutButton);
        return sidebar;
    }

    private VBox createContent() {
        VBox content = new VBox(22);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("대시보드");
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        Label user = new Label("현재 로그인: " + loginId + " / " + roleText());
        user.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        HBox summary = new HBox(18);
        summary.getChildren().addAll(
                metricCard("NEW", String.valueOf(backend.countByStatus("NEW")), "PL 배정 대기"),
                metricCard("ASSIGNED", String.valueOf(backend.countByStatus("ASSIGNED")), "개발자 처리 중"),
                metricCard("FIXED", String.valueOf(backend.countByStatus("FIXED")), "테스터 검증 대기"),
                metricCard("RESOLVED", String.valueOf(backend.countByStatus("RESOLVED")), "PL 종료 대기"),
                metricCard("CLOSED", String.valueOf(backend.countByStatus("CLOSED")), "완료 이력")
        );

        Label shortcutTitle = new Label("바로가기");
        shortcutTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        FlowPane shortcuts = new FlowPane();
        shortcuts.setHgap(12);
        shortcuts.setVgap(12);
        shortcuts.getChildren().addAll(shortcutButtons());

        content.getChildren().addAll(title, user, summary, shortcutTitle, shortcuts);
        return content;
    }

    private Button[] shortcutButtons() {
        return switch (role) {
            case ADMIN -> new Button[]{
                    shortcutButton("프로젝트/계정 관리", SceneManager::showProjectView),
                    shortcutButton("이슈 이력 확인", SceneManager::showIssueListView)
            };
            case PL -> new Button[]{
                    shortcutButton("NEW 이슈 배정", SceneManager::showIssueListView),
                    shortcutButton("담당자 추천/통계", SceneManager::showIssueListView),
                    shortcutButton("프로젝트 구성원 확인", SceneManager::showProjectView)
            };
            case DEV -> new Button[]{
                    shortcutButton("내 배정 이슈", SceneManager::showIssueListView),
                    shortcutButton("수정 완료 처리", SceneManager::showIssueListView),
                    shortcutButton("프로젝트 정보", SceneManager::showProjectView)
            };
            case TESTER -> new Button[]{
                    shortcutButton("이슈 등록", SceneManager::showIssueListView),
                    shortcutButton("FIXED 이슈 검증", SceneManager::showIssueListView),
                    shortcutButton("프로젝트 정보", SceneManager::showProjectView)
            };
        };
    }

    private VBox metricCard(String titleText, String numberText, String descText) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setPrefSize(155, 125);
        card.setStyle(cardStyle());

        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        Label number = new Label(numberText);
        number.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        Label desc = new Label(descText);
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 12px; -fx-text-fill: #9ca3af;");

        card.getChildren().addAll(title, number, desc);
        return card;
    }

    private Button shortcutButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefHeight(42);
        button.setMinWidth(170);
        button.setStyle(
                "-fx-background-color: #2563eb;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        button.setOnAction(e -> action.run());
        return button;
    }

    private Button menuButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(42);
        button.setStyle(
                "-fx-background-color: #1f2937;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        return button;
    }

    private String cardStyle() {
        return "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: #e5e7eb;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.09), 10, 0, 0, 3);";
    }

    private String roleText() {
        return switch (role) {
            case ADMIN -> "관리자";
            case PL -> "PL";
            case DEV -> "개발자";
            case TESTER -> "테스터";
        };
    }
}
