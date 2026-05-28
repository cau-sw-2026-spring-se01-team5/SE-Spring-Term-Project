package main.dashboard;

import app.JavaFxServices;
import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import session.UserSession;
import statistics.dto.countByStatus.v1.CountByStatusInput;
import statistics.dto.getDailyIssueCounts.v1.GetDailyIssueCountsInput;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

// 선택된 프로젝트의 통계 요약을 보여주는 패널.
public class DashboardPanel extends VBox {

    private final JavaFxServices services;
    private final UserSession session;
    private final Runnable showIssues;
    private final Runnable showProjects;

    public DashboardPanel(JavaFxServices services, UserSession session, Runnable showIssues, Runnable showProjects) {
        this.services = services;
        this.session = session;
        this.showIssues = showIssues;
        this.showProjects = showProjects;
        build();
    }

    private void build() {
        setSpacing(22);
        setPadding(new Insets(34));
        setAlignment(Pos.TOP_LEFT);

        Label title = new Label("대시보드");
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        String projectTitle = session.selectedProjectTitle() == null ? "프로젝트 미선택" : session.selectedProjectTitle();
        Label user = new Label("현재 로그인: " + session.loginId() + " / " + roleText(session.role()) + " / 프로젝트: " + projectTitle);
        user.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        Map<String, Integer> statusCounts = statusCounts();
        Map<String, Long> dailyCounts = dailyCounts();
        Map<String, Long> monthlyCounts = monthlyCounts(dailyCounts);

        HBox summary = new HBox(14);
        summary.getChildren().addAll(
                metricCard("NEW", String.valueOf(statusCounts.get("NEW")), "PL 배정 대기"),
                metricCard("ASSIGNED", String.valueOf(statusCounts.get("ASSIGNED")), "개발 처리 중"),
                metricCard("FIXED", String.valueOf(statusCounts.get("FIXED")), "테스터 검증 대기"),
                metricCard("RESOLVED", String.valueOf(statusCounts.get("RESOLVED")), "PL 종료 대기"),
                metricCard("CLOSED", String.valueOf(statusCounts.get("CLOSED")), "완료 이력")
        );

        HBox statistics = new HBox(18);
        statistics.getChildren().addAll(
                statisticPanel("상태별 이슈 현황", statusCounts),
                statisticPanel("일별 이슈 발생 추이", dailyCounts),
                statisticPanel("월별 이슈 발생 추이", monthlyCounts)
        );

        Label shortcutTitle = new Label("바로가기");
        shortcutTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        FlowPane shortcuts = new FlowPane();
        shortcuts.setHgap(12);
        shortcuts.setVgap(12);
        shortcuts.getChildren().addAll(shortcutButtons());

        getChildren().addAll(title, user, summary, statistics, shortcutTitle, shortcuts);
    }

    private Map<String, Integer> statusCounts() {
        Integer projectId = session.selectedProjectId();
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("NEW", countByStatus(projectId, "NEW"));
        counts.put("ASSIGNED", countByStatus(projectId, "ASSIGNED"));
        counts.put("FIXED", countByStatus(projectId, "FIXED"));
        counts.put("RESOLVED", countByStatus(projectId, "RESOLVED"));
        counts.put("CLOSED", countByStatus(projectId, "CLOSED"));
        return counts;
    }

    private int countByStatus(Integer projectId, String status) {
        var output = services.statistics().countByStatus(new CountByStatusInput(projectId, IssueStatus.valueOf(status)));
        return output.success() ? (int) output.count() : 0;
    }

    private Map<String, Long> dailyCounts() {
        var output = services.statistics().getDailyIssueCounts(new GetDailyIssueCountsInput(session.selectedProjectId()));
        Map<String, Long> counts = new LinkedHashMap<>();
        if (!output.success()) {
            return counts;
        }
        output.counts().forEach(item -> counts.put(item.date(), item.count()));
        return counts;
    }

    private Map<String, Long> monthlyCounts(Map<String, Long> dailyCounts) {
        return dailyCounts.entrySet().stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getKey().substring(0, 7),
                        LinkedHashMap::new,
                        Collectors.summingLong(Map.Entry::getValue)
                ));
    }

    private VBox statisticPanel(String titleText, Map<String, ? extends Number> data) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(18));
        panel.setPrefWidth(320);
        panel.setStyle(cardStyle());

        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        panel.getChildren().add(title);

        if (data.isEmpty() || data.values().stream().allMatch(value -> value.longValue() == 0L)) {
            Label empty = new Label("데이터 없음");
            empty.setStyle("-fx-font-size: 13px; -fx-text-fill: #9ca3af;");
            panel.getChildren().add(empty);
            return panel;
        }

        double max = data.values().stream()
                .mapToDouble(Number::doubleValue)
                .max()
                .orElse(1);

        data.forEach((label, value) -> panel.getChildren().add(statRow(label, value.longValue(), max)));
        return panel;
    }

    private HBox statRow(String labelText, long value, double max) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(labelText);
        label.setMinWidth(74);
        label.setMaxWidth(74);
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");

        Region bar = new Region();
        double width = max <= 0 ? 0 : Math.max(16, 86 * (value / max));
        bar.setPrefSize(width, 10);
        bar.setStyle("-fx-background-color: #2563eb; -fx-background-radius: 999;");

        Label number = new Label(value + "건");
        number.setMinWidth(44);
        number.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        row.getChildren().addAll(label, bar, number);
        return row;
    }

    private Button[] shortcutButtons() {
        return switch (session.role()) {
            case ADMIN -> new Button[]{
                    shortcutButton("프로젝트 선택 변경", showProjects),
                    shortcutButton("이슈 이력 확인", showIssues)
            };
            case PL -> new Button[]{
                    shortcutButton("NEW 이슈 배정", showIssues),
                    shortcutButton("담당자 추천/통계", showIssues),
                    shortcutButton("프로젝트 선택 변경", showProjects)
            };
            case DEV -> new Button[]{
                    shortcutButton("내 배정 이슈", showIssues),
                    shortcutButton("수정 완료 처리", showIssues),
                    shortcutButton("프로젝트 선택 변경", showProjects)
            };
            case TESTER -> new Button[]{
                    shortcutButton("이슈 등록", showIssues),
                    shortcutButton("FIXED 이슈 검증", showIssues),
                    shortcutButton("프로젝트 선택 변경", showProjects)
            };
        };
    }

    private VBox metricCard(String titleText, String numberText, String descText) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(18));
        card.setPrefSize(145, 116);
        card.setStyle(cardStyle());

        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");

        Label number = new Label(numberText);
        number.setStyle("-fx-font-size: 29px; -fx-font-weight: bold;");

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
        button.setOnAction(event -> action.run());
        HBox.setHgrow(button, Priority.NEVER);
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
