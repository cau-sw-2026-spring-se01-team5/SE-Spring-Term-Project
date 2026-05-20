package main.issue;

import app.JavaFxBackend;
import app.JavaFxBackend.CommentItem;
import app.JavaFxBackend.IssueItem;
import app.UiDialog;
import enums.user.v1.UserRole;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import session.UserSession;

import java.util.List;
import java.util.stream.Collectors;

/*
 * 이슈 관리 본문 패널이다.
 *
 * Swing의 IssuePanel처럼 메인 화면 내부에 배치되는 기능 패널이다.
 * 역할별 버튼과 검색/목록/상세 흐름을 이 패널 안에 모았다.
 */
public class IssuePanel extends VBox {

    private final JavaFxBackend backend;
    private final UserSession session;

    private final TableView<IssueItem> tableView = new TableView<>();
    private final TextField keywordField = new TextField();
    private final ComboBox<String> statusBox = new ComboBox<>();
    private final ComboBox<String> priorityBox = new ComboBox<>();
    private final ComboBox<String> assigneeBox = new ComboBox<>();
    private final ComboBox<String> reporterBox = new ComboBox<>();

    public IssuePanel(JavaFxBackend backend, UserSession session) {
        this.backend = backend;
        this.session = session;
        build();
    }

    private void build() {
        setSpacing(18);
        setPadding(new Insets(34));

        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(6);
        Label titleLabel = new Label("이슈 목록");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        Label descLabel = new Label(roleDescription());
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");
        titleBox.getChildren().addAll(titleLabel, descLabel);

        HBox actionBox = new HBox(8);
        actionBox.getChildren().addAll(roleButtons());
        header.getChildren().addAll(titleBox, spacer(), actionBox);

        setupFilters();
        setupTable();

        HBox filterBox = new HBox(10, keywordField, statusBox, priorityBox, assigneeBox, reporterBox, searchButton());
        filterBox.setAlignment(Pos.CENTER_LEFT);

        VBox tableCard = new VBox(tableView);
        tableCard.setPadding(new Insets(18));
        tableCard.setStyle(cardStyle());
        VBox.setVgrow(tableCard, Priority.ALWAYS);
        tableView.setPrefHeight(485);

        getChildren().addAll(header, filterBox, tableCard);
        refreshTable();
    }

    private Node spacer() {
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private void setupFilters() {
        keywordField.setPromptText("제목 또는 설명");
        keywordField.setPrefWidth(190);
        keywordField.setPrefHeight(38);

        statusBox.getItems().addAll("전체상태", "NEW", "ASSIGNED", "FIXED", "RESOLVED", "CLOSED", "REOPENED");
        statusBox.setValue(defaultStatus());
        statusBox.setPrefHeight(38);

        priorityBox.getItems().addAll("전체우선순위", "BLOCKER", "CRITICAL", "MAJOR", "MINOR", "TRIVIAL");
        priorityBox.setValue("전체우선순위");
        priorityBox.setPrefHeight(38);

        assigneeBox.getItems().add("전체담당자");
        assigneeBox.getItems().addAll(backend.developerLoginIds());
        assigneeBox.setValue(session.role() == UserRole.DEV ? session.loginId() : "전체담당자");
        assigneeBox.setPrefHeight(38);

        reporterBox.getItems().add("전체리포터");
        reporterBox.getItems().addAll(backend.testerLoginIds());
        reporterBox.setValue(session.role() == UserRole.TESTER ? session.loginId() : "전체리포터");
        reporterBox.setPrefHeight(38);
    }

    private Button searchButton() {
        Button button = secondaryButton("검색");
        button.setOnAction(e -> refreshTable());
        return button;
    }

    private Button[] roleButtons() {
        return switch (session.role()) {
            case ADMIN -> new Button[]{actionButton("상세", this::showSelectedIssue), actionButton("코멘트", this::addCommentDialog)};
            case PL -> new Button[]{actionButton("상세", this::showSelectedIssue), actionButton("배정", this::assignDialog), actionButton("추천", this::recommendDialog), actionButton("종료", this::closeSelectedIssue), actionButton("통계", this::statisticsDialog)};
            case DEV -> new Button[]{actionButton("상세", this::showSelectedIssue), actionButton("코멘트", this::addCommentDialog), actionButton("수정완료", this::markFixedDialog)};
            case TESTER -> new Button[]{actionButton("등록", this::registerIssueDialog), actionButton("상세", this::showSelectedIssue), actionButton("코멘트", this::addCommentDialog), actionButton("해결확인", this::resolveSelectedIssue), actionButton("재오픈", this::reopenSelectedIssue)};
        };
    }

    private void setupTable() {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setStyle("-fx-background-color: transparent; -fx-font-size: 13px;");
        tableView.getColumns().addAll(
                column("번호", issue -> String.valueOf(issue.id())),
                column("제목", IssueItem::title),
                column("상태", IssueItem::status),
                column("우선순위", IssueItem::priority),
                column("리포터", IssueItem::reporter),
                column("담당자", IssueItem::assignee),
                column("수정자", IssueItem::fixer),
                column("등록일시", IssueItem::reportedDate)
        );
    }

    private void refreshTable() {
        String keyword = keywordField.getText() == null ? "" : keywordField.getText().trim().toLowerCase();
        List<IssueItem> filtered = backend.issuesForRole(session.loginId(), session.role()).stream()
                .filter(issue -> "전체상태".equals(statusBox.getValue()) || issue.status().equals(statusBox.getValue()))
                .filter(issue -> "전체우선순위".equals(priorityBox.getValue()) || issue.priority().equals(priorityBox.getValue()))
                .filter(issue -> "전체담당자".equals(assigneeBox.getValue()) || issue.assignee().equals(assigneeBox.getValue()))
                .filter(issue -> "전체리포터".equals(reporterBox.getValue()) || issue.reporter().equals(reporterBox.getValue()))
                .filter(issue -> keyword.isEmpty() || issue.title().toLowerCase().contains(keyword) || issue.description().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
        tableView.getItems().setAll(filtered);
    }

    private void registerIssueDialog() {
        if (session.role() != UserRole.TESTER) {
            UiDialog.showWarning("테스터만 이슈를 등록할 수 있습니다.");
            return;
        }
        Dialog<Void> dialog = baseDialog("이슈 등록");
        GridPane form = formGrid();
        ComboBox<JavaFxBackend.ProjectItem> projectBox = new ComboBox<>();
        projectBox.getItems().addAll(backend.projectsForUser(session.loginId(), session.role()));
        if (projectBox.getItems().isEmpty()) {
            UiDialog.showWarning("소속된 프로젝트가 없습니다.");
            return;
        }
        projectBox.setValue(projectBox.getItems().get(0));
        TextField titleField = new TextField();
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(4);
        ComboBox<String> priority = new ComboBox<>();
        priority.getItems().addAll("BLOCKER", "CRITICAL", "MAJOR", "MINOR", "TRIVIAL");
        priority.setValue("MAJOR");
        addRow(form, 0, "프로젝트", projectBox);
        addRow(form, 1, "제목", titleField);
        addRow(form, 2, "설명", descriptionArea);
        addRow(form, 3, "우선순위", priority);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(button -> {
            if (button.getButtonData().isDefaultButton()) {
                if (titleField.getText().isBlank() || descriptionArea.getText().isBlank()) {
                    UiDialog.showWarning("제목과 설명은 반드시 입력해야 합니다.");
                    return null;
                }
                backend.registerIssue(projectBox.getValue().id(), titleField.getText(), descriptionArea.getText(), session.loginId(), priority.getValue());
                refreshTable();
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void assignDialog() {
        IssueItem selected = selectedIssue();
        if (selected == null) return;
        if (!"NEW".equals(selected.status()) && !"REOPENED".equals(selected.status())) {
            UiDialog.showWarning("PL은 NEW 또는 REOPENED 상태의 이슈만 배정할 수 있습니다.");
            return;
        }
        Dialog<Void> dialog = baseDialog("이슈 배정");
        GridPane form = formGrid();
        ComboBox<String> developerBox = new ComboBox<>();
        developerBox.getItems().addAll(backend.developerLoginIdsForProject(selected.projectId()));
        if (developerBox.getItems().isEmpty()) {
            UiDialog.showWarning("선택한 프로젝트에 개발자 계정이 없습니다.");
            return;
        }
        developerBox.setValue(developerBox.getItems().get(0));
        TextArea commentArea = new TextArea(session.loginId() + "가 이슈를 배정함");
        commentArea.setPrefRowCount(3);
        addRow(form, 0, "개발자", developerBox);
        addRow(form, 1, "코멘트", commentArea);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(button -> {
            if (button.getButtonData().isDefaultButton()) {
                backend.assignIssue(selected.id(), developerBox.getValue(), session.loginId(), commentArea.getText());
                refreshTable();
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void markFixedDialog() {
        IssueItem selected = selectedIssue();
        if (selected == null) return;
        if (!session.loginId().equals(selected.assignee())) {
            UiDialog.showWarning("자신에게 배정된 이슈만 수정 완료 처리할 수 있습니다.");
            return;
        }
        Dialog<Void> dialog = baseDialog("수정 완료 처리");
        TextArea commentArea = new TextArea("수정이 완료되었으며 테스트 검증을 요청합니다.");
        commentArea.setPrefRowCount(4);
        dialog.getDialogPane().setContent(commentArea);
        dialog.setResultConverter(button -> {
            if (button.getButtonData().isDefaultButton()) {
                backend.markFixed(selected.id(), session.loginId(), commentArea.getText());
                refreshTable();
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void resolveSelectedIssue() {
        IssueItem selected = selectedIssue();
        if (selected == null) return;
        if (!"FIXED".equals(selected.status())) {
            UiDialog.showWarning("FIXED 상태의 이슈만 해결 확인할 수 있습니다.");
            return;
        }
        backend.resolveIssue(selected.id(), session.loginId(), "테스터가 수정 내용을 확인함");
        refreshTable();
    }

    private void reopenSelectedIssue() {
        IssueItem selected = selectedIssue();
        if (selected == null) return;
        if (!"FIXED".equals(selected.status())) {
            UiDialog.showWarning("FIXED 상태의 이슈만 재오픈할 수 있습니다.");
            return;
        }
        backend.reopenIssue(selected.id(), session.loginId(), "테스터가 수정 부족으로 재오픈함");
        refreshTable();
    }

    private void closeSelectedIssue() {
        IssueItem selected = selectedIssue();
        if (selected == null) return;
        if (!"RESOLVED".equals(selected.status())) {
            UiDialog.showWarning("RESOLVED 상태의 이슈만 종료할 수 있습니다.");
            return;
        }
        backend.closeIssue(selected.id(), session.loginId(), "PL이 해결된 이슈를 종료 처리함");
        refreshTable();
    }

    private void addCommentDialog() {
        IssueItem selected = selectedIssue();
        if (selected == null) return;
        Dialog<Void> dialog = baseDialog("코멘트 추가");
        TextArea commentArea = new TextArea();
        commentArea.setPromptText("코멘트를 입력하세요.");
        commentArea.setPrefRowCount(4);
        dialog.getDialogPane().setContent(commentArea);
        dialog.setResultConverter(button -> {
            if (button.getButtonData().isDefaultButton() && !commentArea.getText().isBlank()) {
                backend.addComment(selected.id(), session.loginId(), commentArea.getText());
                refreshTable();
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void showSelectedIssue() {
        IssueItem selected = selectedIssue();
        if (selected == null) return;
        String comments = selected.comments().stream().map(CommentItem::toString).collect(Collectors.joining("\n"));
        UiDialog.showInfo("이슈 상세 정보",
                "번호: " + selected.id() + "\n" +
                        "제목: " + selected.title() + "\n" +
                        "설명: " + selected.description() + "\n" +
                        "상태: " + selected.status() + "\n" +
                        "우선순위: " + selected.priority() + "\n" +
                        "리포터: " + selected.reporter() + "\n" +
                        "등록일시: " + selected.reportedDate() + "\n" +
                        "담당자: " + selected.assignee() + "\n" +
                        "수정자: " + selected.fixer() + "\n\n" +
                        "코멘트 이력\n" + comments);
    }

    private void recommendDialog() {
        IssueItem selected = selectedIssue();
        if (selected == null) return;
        UiDialog.showInfo("담당자 자동 추천", "추천 후보: " + String.join(", ", backend.recommendAssignees(selected)));
    }

    private void statisticsDialog() {
        String daily = backend.dailyIssueCounts().entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\n"));
        UiDialog.showInfo("이슈 통계 분석",
                "상태별 요약\n" +
                        "NEW: " + backend.countByStatus("NEW") + "\n" +
                        "ASSIGNED: " + backend.countByStatus("ASSIGNED") + "\n" +
                        "FIXED: " + backend.countByStatus("FIXED") + "\n" +
                        "RESOLVED: " + backend.countByStatus("RESOLVED") + "\n" +
                        "CLOSED: " + backend.countByStatus("CLOSED") + "\n\n" +
                        "일별 발생 횟수\n" + daily);
    }

    private IssueItem selectedIssue() {
        IssueItem selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiDialog.showWarning("먼저 이슈를 선택하세요.");
            return null;
        }
        return selected;
    }

    private String defaultStatus() {
        return switch (session.role()) {
            case PL -> "NEW";
            case DEV -> "ASSIGNED";
            case TESTER -> "FIXED";
            case ADMIN -> "전체상태";
        };
    }

    private String roleDescription() {
        return switch (session.role()) {
            case ADMIN -> "관리자는 이슈와 코멘트 이력을 확인하고, 계정 관리는 프로젝트/계정 관리 화면에서 수행합니다.";
            case PL -> "PL은 이슈 검색, 배정, 담당자 추천, 통계 확인, 종료 처리를 수행합니다.";
            case DEV -> "개발자는 자신에게 배정된 이슈를 확인하고 코멘트 작성 및 수정 완료 처리를 수행합니다.";
            case TESTER -> "테스터는 이슈 등록, 코멘트 작성, FIXED 이슈 검증을 수행합니다.";
        };
    }

    private TableColumn<IssueItem, String> column(String title, ValueProvider provider) {
        TableColumn<IssueItem, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new SimpleStringProperty(provider.value(data.getValue())));
        return column;
    }

    private Button actionButton(String text, Runnable action) {
        Button button = primaryButton(text);
        button.setOnAction(e -> action.run());
        return button;
    }

    private Dialog<Void> baseDialog(String title) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.getDialogPane().getButtonTypes().addAll(UiDialog.okButtonType(), UiDialog.cancelButtonType());
        UiDialog.styleDialog(dialog);
        return dialog;
    }

    private GridPane formGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(16));
        grid.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #e5e7eb;");
        return grid;
    }

    private void addRow(GridPane grid, int row, String label, Node field) {
        grid.add(new Label(label), 0, row);
        grid.add(field, 1, row);
    }

    private Button primaryButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(38);
        button.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        return button;
    }

    private Button secondaryButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(38);
        button.setStyle("-fx-background-color: white; -fx-text-fill: #111827; -fx-font-size: 14px; -fx-border-color: #d1d5db; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
        return button;
    }

    private String cardStyle() {
        return "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: #e5e7eb;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.09), 10, 0, 0, 3);";
    }

    private interface ValueProvider {
        String value(IssueItem issue);
    }
}
