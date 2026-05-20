package main.dashboard;

import app.JavaFxBackend;
import enums.user.v1.UserRole;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import session.UserSession;

/*
 * 대시보드 본문 패널이다.
 *
 * 전체 메인 틀과 분리하여 대시보드 변경이 MainPanel에 영향을 주지 않도록 했다.
 */
public class DashboardPanel extends VBox {

    private final JavaFxBackend backend;
    private final UserSession session;
    private final Runnable showIssues;
    private final Runnable showProjects;

    public DashboardPanel(JavaFxBackend backend, UserSession session, Runnable showIssues, Runnable showProjects) {
        this.backend = backend;
        this.session = session;
        this.showIssues = showIssues;
        this.showProjects = showProjects;
        build();
    }

    private void build() {
        setSpacing(22);
        setPadding(new Insets(40));
        setAlignment(Pos.TOP_LEFT);

        Label title = new Label("대시보드");
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        Label user = new Label("현재 로그인: " + session.loginId() + " / " + roleText(session.role()));
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

        getChildren().addAll(title, user, summary, shortcutTitle, shortcuts);
    }

    private Button[] shortcutButtons() {
        return switch (session.role()) {
            case ADMIN -> new Button[]{shortcutButton("프로젝트/계정 관리", showProjects), shortcutButton("이슈 이력 확인", showIssues)};
            case PL -> new Button[]{shortcutButton("NEW 이슈 배정", showIssues), shortcutButton("담당자 추천/통계", showIssues), shortcutButton("프로젝트 구성원 확인", showProjects)};
            case DEV -> new Button[]{shortcutButton("내 배정 이슈", showIssues), shortcutButton("수정 완료 처리", showIssues), shortcutButton("프로젝트 정보", showProjects)};
            case TESTER -> new Button[]{shortcutButton("이슈 등록", showIssues), shortcutButton("FIXED 이슈 검증", showIssues), shortcutButton("프로젝트 정보", showProjects)};
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
        button.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        button.setOnAction(e -> action.run());
        return button;
    }

    private String cardStyle() {
        return "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: #e5e7eb;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.09), 10, 0, 0, 3);";
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
