package main.issue;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.JavaFxData.IssueItem;

import java.util.List;

// 현재 화면에 보이는 이슈 목록과 선택 상태를 관리한다.
class IssueTablePanel extends VBox {

    private final TableView<IssueItem> tableView = new TableView<>();

    IssueTablePanel() {
        setPadding(new Insets(18));
        setStyle(cardStyle());
        VBox.setVgrow(this, Priority.ALWAYS);

        tableView.setPrefHeight(485);
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

        getChildren().add(tableView);
    }

    void setIssues(List<IssueItem> issues) {
        tableView.getItems().setAll(issues);
    }

    List<IssueItem> visibleIssues() {
        return List.copyOf(tableView.getItems());
    }

    IssueItem selectedIssue() {
        return tableView.getSelectionModel().getSelectedItem();
    }

    private TableColumn<IssueItem, String> column(String title, ValueProvider provider) {
        TableColumn<IssueItem, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new SimpleStringProperty(provider.value(data.getValue())));
        return column;
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
