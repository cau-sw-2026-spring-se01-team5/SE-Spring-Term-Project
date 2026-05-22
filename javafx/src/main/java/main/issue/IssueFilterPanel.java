package main.issue;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.List;

/*
 * 이슈 검색 조건만 담당하는 하위 패널이다.
 *
 * IssuePanel에 검색 입력 UI까지 모두 넣으면 목록, 액션, 다이얼로그 코드가 한 파일에 섞인다.
 * 검색 조건 입력 책임을 이 클래스로 분리해서 IssuePanel의 변경 이유를 줄였다.
 */
class IssueFilterPanel extends HBox {

    private static final String ALL_STATUS = "전체상태";
    private static final String ALL_PRIORITY = "전체우선순위";
    private static final String ALL_ASSIGNEE = "전체담당자";
    private static final String ALL_REPORTER = "전체리포터";

    private final TextField keywordField = new TextField();
    private final ComboBox<String> statusBox = new ComboBox<>();
    private final ComboBox<String> priorityBox = new ComboBox<>();
    private final ComboBox<String> assigneeBox = new ComboBox<>();
    private final ComboBox<String> reporterBox = new ComboBox<>();
    private final Button searchButton = new Button("검색");

    IssueFilterPanel(String defaultStatus, String defaultAssignee, String defaultReporter) {
        setSpacing(10);
        setAlignment(Pos.CENTER_LEFT);

        keywordField.setPromptText("제목 또는 설명");
        keywordField.setPrefWidth(190);
        keywordField.setPrefHeight(38);

        statusBox.getItems().addAll(ALL_STATUS, "NEW", "ASSIGNED", "FIXED", "RESOLVED", "CLOSED", "REOPENED");
        statusBox.setValue(defaultStatus);

        priorityBox.getItems().addAll(ALL_PRIORITY, "BLOCKER", "CRITICAL", "MAJOR", "MINOR", "TRIVIAL");
        priorityBox.setValue(ALL_PRIORITY);

        assigneeBox.getItems().add(ALL_ASSIGNEE);
        assigneeBox.setValue(defaultAssignee);

        reporterBox.getItems().add(ALL_REPORTER);
        reporterBox.setValue(defaultReporter);

        for (ComboBox<String> box : List.of(statusBox, priorityBox, assigneeBox, reporterBox)) {
            box.setPrefHeight(38);
        }

        searchButton.setPrefHeight(38);
        searchButton.setStyle("-fx-background-color: white; -fx-text-fill: #111827; -fx-font-size: 14px; -fx-border-color: #d1d5db; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");

        getChildren().addAll(keywordField, statusBox, priorityBox, assigneeBox, reporterBox, searchButton);
    }

    void setAssignees(List<String> assignees) {
        assigneeBox.getItems().setAll(ALL_ASSIGNEE);
        assigneeBox.getItems().addAll(assignees);
    }

    void setReporters(List<String> reporters) {
        reporterBox.getItems().setAll(ALL_REPORTER);
        reporterBox.getItems().addAll(reporters);
    }

    void onSearch(Runnable handler) {
        searchButton.setOnAction(event -> handler.run());
    }

    boolean matches(backend.JavaFxBackend.IssueItem issue) {
        String keyword = keywordField.getText() == null ? "" : keywordField.getText().trim().toLowerCase();

        return (ALL_STATUS.equals(statusBox.getValue()) || issue.status().equals(statusBox.getValue()))
                && (ALL_PRIORITY.equals(priorityBox.getValue()) || issue.priority().equals(priorityBox.getValue()))
                && (ALL_ASSIGNEE.equals(assigneeBox.getValue()) || issue.assignee().equals(assigneeBox.getValue()))
                && (ALL_REPORTER.equals(reporterBox.getValue()) || issue.reporter().equals(reporterBox.getValue()))
                && (keyword.isEmpty()
                || issue.title().toLowerCase().contains(keyword)
                || issue.description().toLowerCase().contains(keyword));
    }
}
