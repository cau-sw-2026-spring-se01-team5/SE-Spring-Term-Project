package main.issue;

import backend.JavaFxBackend.CommentItem;
import backend.JavaFxBackend.IssueItem;
import backend.JavaFxBackend.ProjectItem;
import ui.UiDialog;
import enums.user.v1.UserRole;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 * 이슈 관리 화면을 그리는 JavaFX Panel이다.
 *
 * 이 클래스는 버튼, 검색 조건, 테이블, 입력 다이얼로그만 담당한다.
 * 이슈 등록/배정/상태 변경 같은 실제 흐름은 IssueController가 담당한다.
 */
public class IssuePanel extends VBox implements IssueView {

    private final UserRole role;
    private final String loginId;
    private final IssueFilterPanel filterPanel;
    private final IssueTablePanel tablePanel = new IssueTablePanel();

    private Button registerButton;
    private Button detailButton;
    private Button commentButton;
    private Button assignButton;
    private Button recommendButton;
    private Button closeButton;
    private Button statisticsButton;
    private Button fixedButton;
    private Button resolveButton;
    private Button reopenButton;

    public IssuePanel(UserRole role, String loginId) {
        this.role = role;
        this.loginId = loginId;
        this.filterPanel = new IssueFilterPanel(
                defaultStatus(),
                role == UserRole.DEV ? loginId : "전체담당자",
                role == UserRole.TESTER ? loginId : "전체리포터"
        );
        build();
    }

    private void build() {
        /*
         * 이 메서드는 화면 배치만 담당한다.
         * 버튼을 눌렀을 때 어떤 backend 기능을 호출할지는 IssueController에서 연결한다.
         */
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

        getChildren().addAll(header, filterPanel, tablePanel);
    }

    @Override
    public void setFilterOptions(List<String> assignees, List<String> reporters) {
        filterPanel.setAssignees(assignees);
        filterPanel.setReporters(reporters);
    }

    @Override
    public void setIssues(List<IssueItem> issues) {
        tablePanel.setIssues(issues);
    }

    @Override
    public boolean matchesFilter(IssueItem issue) {
        return filterPanel.matches(issue);
    }

    @Override
    public IssueItem selectedIssue() {
        return tablePanel.selectedIssue();
    }

    @Override
    public Optional<CreateIssueForm> showRegisterIssueDialog(List<ProjectItem> projects) {
        /*
         * 이슈 등록 다이얼로그는 View 책임이다.
         * 사용자가 입력한 값을 CreateIssueForm으로 묶어 Controller에 돌려주고,
         * 실제 이슈 등록은 Controller가 backend에 요청한다.
         */
        if (projects.isEmpty()) {
            UiDialog.showWarning("소속된 프로젝트가 없습니다.");
            return Optional.empty();
        }

        Dialog<CreateIssueForm> dialog = baseDialog("이슈 등록");
        GridPane form = formGrid();
        ComboBox<ProjectItem> projectBox = new ComboBox<>();
        projectBox.getItems().addAll(projects);
        projectBox.setValue(projects.get(0));
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
            if (!button.getButtonData().isDefaultButton()) {
                return null;
            }
            if (titleField.getText().isBlank() || descriptionArea.getText().isBlank()) {
                UiDialog.showWarning("제목과 설명은 반드시 입력해야 합니다.");
                return null;
            }
            return new CreateIssueForm(projectBox.getValue(), titleField.getText(), descriptionArea.getText(), priority.getValue());
        });
        return dialog.showAndWait();
    }

    @Override
    public Optional<AssignIssueForm> showAssignIssueDialog(List<String> developers, String writer) {
        return showAssignIssueDialog(developers, writer, null);
    }

    @Override
    public Optional<AssignIssueForm> showAssignIssueDialog(List<String> developers, String writer, String defaultDeveloper) {
        /*
         * 여기 수정: 추천 후보를 클릭한 뒤 배정으로 이어질 수 있도록 기본 개발자 값을 받을 수 있게 했다.
         * View는 개발자 선택과 코멘트 입력만 담당하고, 실제 배정 처리는 IssueController가 backend에 요청한다.
         */
        if (developers.isEmpty()) {
            UiDialog.showWarning("선택한 프로젝트에 개발자 계정이 없습니다.");
            return Optional.empty();
        }

        Dialog<AssignIssueForm> dialog = baseDialog("이슈 배정");
        GridPane form = formGrid();
        ComboBox<String> developerBox = new ComboBox<>();
        developerBox.getItems().addAll(developers);
        if (defaultDeveloper != null && developers.contains(defaultDeveloper)) {
            developerBox.setValue(defaultDeveloper);
        } else {
            developerBox.setValue(developers.get(0));
        }

        TextArea commentArea = new TextArea(writer + "가 이슈를 " + developerBox.getValue() + "에게 배정합니다.");
        commentArea.setPrefRowCount(3);
        developerBox.setOnAction(event -> commentArea.setText(writer + "가 이슈를 " + developerBox.getValue() + "에게 배정합니다."));

        addRow(form, 0, "개발자", developerBox);
        addRow(form, 1, "코멘트", commentArea);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(button -> {
            if (!button.getButtonData().isDefaultButton()) {
                return null;
            }
            return new AssignIssueForm(developerBox.getValue(), commentArea.getText());
        });
        return dialog.showAndWait();
    }
    @Override
    public Optional<String> showCommentDialog(String title, String defaultComment) {
        /*
         * 코멘트 입력은 여러 기능에서 공통으로 사용된다.
         * 수정 완료, 일반 코멘트 추가처럼 기능은 달라도 입력 방식은 같으므로 하나의 메서드로 묶었다.
         */
        Dialog<String> dialog = baseDialog(title);
        TextArea commentArea = new TextArea(defaultComment);
        commentArea.setPromptText("코멘트를 입력하세요.");
        commentArea.setPrefRowCount(4);
        dialog.getDialogPane().setContent(commentArea);
        dialog.setResultConverter(button -> {
            if (button.getButtonData().isDefaultButton() && !commentArea.getText().isBlank()) {
                return commentArea.getText();
            }
            return null;
        });
        return dialog.showAndWait();
    }

    @Override
    public void showIssueDetail(IssueItem issue) {
        /*
         * 상세 정보 출력은 View 책임이다.
         * 다만 상세 정보에 들어갈 데이터는 Controller/backend가 준비한 IssueItem을 그대로 사용한다.
         */
        String comments = issue.comments().stream().map(CommentItem::toString).collect(Collectors.joining("\n"));
        UiDialog.showInfo("이슈 상세 정보",
                "번호: " + issue.id() + "\n" +
                        "제목: " + issue.title() + "\n" +
                        "설명: " + issue.description() + "\n" +
                        "상태: " + issue.status() + "\n" +
                        "우선순위: " + issue.priority() + "\n" +
                        "리포터: " + issue.reporter() + "\n" +
                        "등록일시: " + issue.reportedDate() + "\n" +
                        "담당자: " + issue.assignee() + "\n" +
                        "수정자: " + issue.fixer() + "\n\n" +
                        "코멘트 이력\n" + comments);
    }
    @Override
    public void showRecommendations(List<String> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            UiDialog.showInfo(
                    "담당자 자동 추천",
                    "추천 후보가 없습니다.\n\n" +
                            "과거에 해결된 이슈의 fixer 기록이 있어야 추천 결과가 나옵니다."
            );
            return;
        }

        StringBuilder message = new StringBuilder();
        for (int i = 0; i < candidates.size(); i++) {
            message.append(i + 1)
                    .append("순위: ")
                    .append(candidates.get(i))
                    .append("\n");
        }
        UiDialog.showInfo("담당자 자동 추천", message.toString());
    }

    @Override
    public Optional<String> showRecommendationSelectDialog(List<String> candidates) {
        // 여기 수정: 추천 이유 표시는 제거하고, 후보 선택만 하도록 되돌렸다.
        if (candidates == null || candidates.isEmpty()) {
            UiDialog.showInfo(
                    "담당자 자동 추천",
                    "추천 후보가 없습니다.\n\n" +
                            "과거에 해결된 이슈의 fixer 기록이 있어야 추천 결과가 나옵니다."
            );
            return Optional.empty();
        }

        Dialog<String> dialog = baseDialog("추천 담당자 선택");
        GridPane form = formGrid();
        ComboBox<String> candidateBox = new ComboBox<>();
        candidateBox.getItems().addAll(candidates);
        candidateBox.setValue(candidates.get(0));
        addRow(form, 0, "추천 후보", candidateBox);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(button -> {
            if (!button.getButtonData().isDefaultButton()) {
                return null;
            }
            return candidateBox.getValue();
        });
        return dialog.showAndWait();
    }
    @Override
    public void showStatistics(String message) {
        UiDialog.showInfo("이슈 통계 분석", message);
    }

    @Override
    public void showWarning(String message) {
        UiDialog.showWarning(message);
    }

    @Override
    public void onSearch(Runnable handler) {
        filterPanel.onSearch(handler);
    }

    @Override
    public void onRegisterIssue(Runnable handler) {
        if (registerButton != null) registerButton.setOnAction(event -> handler.run());
    }

    @Override
    public void onShowIssueDetail(Runnable handler) {
        detailButton.setOnAction(event -> handler.run());
    }

    @Override
    public void onAddComment(Runnable handler) {
        if (commentButton != null) commentButton.setOnAction(event -> handler.run());
    }

    @Override
    public void onAssignIssue(Runnable handler) {
        if (assignButton != null) assignButton.setOnAction(event -> handler.run());
    }

    @Override
    public void onRecommendAssignee(Runnable handler) {
        if (recommendButton != null) recommendButton.setOnAction(event -> handler.run());
    }

    @Override
    public void onCloseIssue(Runnable handler) {
        if (closeButton != null) closeButton.setOnAction(event -> handler.run());
    }

    @Override
    public void onShowStatistics(Runnable handler) {
        if (statisticsButton != null) statisticsButton.setOnAction(event -> handler.run());
    }

    @Override
    public void onMarkFixed(Runnable handler) {
        if (fixedButton != null) fixedButton.setOnAction(event -> handler.run());
    }

    @Override
    public void onResolveIssue(Runnable handler) {
        if (resolveButton != null) resolveButton.setOnAction(event -> handler.run());
    }

    @Override
    public void onReopenIssue(Runnable handler) {
        if (reopenButton != null) reopenButton.setOnAction(event -> handler.run());
    }

    private Button[] roleButtons() {
        /*
         * 역할별로 노출되는 버튼을 다르게 구성한다.
         * 예를 들어 PL은 배정/추천/통계 버튼을 보고, DEV는 수정완료 버튼을 본다.
         * 버튼 표시 자체는 View 책임이고, 버튼 클릭 후 처리 흐름은 IssueController가 담당한다.
         */
        return switch (role) {
            case ADMIN -> {
                detailButton = actionButton("상세");
                commentButton = actionButton("코멘트");
                yield new Button[]{detailButton, commentButton};
            }
            case PL -> {
                detailButton = actionButton("상세");
                assignButton = actionButton("배정");
                recommendButton = actionButton("추천");
                closeButton = actionButton("종료");
                statisticsButton = actionButton("통계");
                yield new Button[]{detailButton, assignButton, recommendButton, closeButton, statisticsButton};
            }
            case DEV -> {
                detailButton = actionButton("상세");
                commentButton = actionButton("코멘트");
                fixedButton = actionButton("수정완료");
                yield new Button[]{detailButton, commentButton, fixedButton};
            }
            case TESTER -> {
                registerButton = actionButton("등록");
                detailButton = actionButton("상세");
                commentButton = actionButton("코멘트");
                resolveButton = actionButton("해결확인");
                reopenButton = actionButton("재오픈");
                yield new Button[]{registerButton, detailButton, commentButton, resolveButton, reopenButton};
            }
        };
    }

    private Node spacer() {
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private String defaultStatus() {
        /*
         * 역할별 기본 조회 상태를 다르게 둔다.
         * PL은 NEW, DEV는 ASSIGNED, TESTER는 FIXED 이슈를 먼저 보게 하여 데모 시나리오 흐름을 빠르게 확인할 수 있다.
         */
        return switch (role) {
            case PL -> "NEW";
            case DEV -> "ASSIGNED";
            case TESTER -> "전체상태";
            case ADMIN -> "전체상태";
        };
    }

    private String roleDescription() {
        return switch (role) {
            case ADMIN -> "관리자는 이슈와 코멘트 이력을 확인하고, 계정 관리는 프로젝트/계정 관리 화면에서 수행합니다.";
            case PL -> "PL은 이슈 검색, 배정, 담당자 추천, 통계 확인, 종료 처리를 수행합니다.";
            case DEV -> "개발자는 자신에게 배정된 이슈를 확인하고 코멘트 작성 및 수정 완료 처리를 수행합니다.";
            case TESTER -> "테스터는 이슈 등록, 코멘트 작성, FIXED 이슈 검증을 수행합니다.";
        };
    }

    private Button actionButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(38);
        button.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        return button;
    }

    private <T> Dialog<T> baseDialog(String title) {
        Dialog<T> dialog = new Dialog<>();
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
}
