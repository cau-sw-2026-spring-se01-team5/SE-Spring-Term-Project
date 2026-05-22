package main.project;

import backend.JavaFxBackend.ProjectItem;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

/*
 * 프로젝트 목록 UI만 담당하는 하위 패널이다.
 *
 * 프로젝트 목록의 표시, 선택, 선택 변경 이벤트를 ProjectPanel에서 분리했다.
 * 이렇게 두면 계정 목록이나 생성 다이얼로그가 바뀌어도 목록 UI 코드는 영향을 덜 받는다.
 */
class ProjectListPanel extends VBox {

    private final ListView<ProjectItem> listView = new ListView<>();

    ProjectListPanel() {
        setPadding(new Insets(18));
        setPrefWidth(330);
        setStyle(cardStyle());

        Label title = new Label("프로젝트 목록");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        listView.setPrefHeight(360);
        listView.setStyle("-fx-font-size: 14px;");

        getChildren().addAll(title, listView);
        setSpacing(12);
        HBox.setHgrow(this, Priority.ALWAYS);
    }

    void setProjects(List<ProjectItem> projects) {
        listView.getItems().setAll(projects);
        if (!listView.getItems().isEmpty() && selectedProject() == null) {
            listView.getSelectionModel().selectFirst();
        }
    }

    ProjectItem selectedProject() {
        return listView.getSelectionModel().getSelectedItem();
    }

    void select(ProjectItem project) {
        listView.getSelectionModel().select(project);
    }

    void onSelectionChanged(Runnable handler) {
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> handler.run());
    }

    private String cardStyle() {
        return "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: #e5e7eb;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.09), 10, 0, 0, 3);";
    }
}
