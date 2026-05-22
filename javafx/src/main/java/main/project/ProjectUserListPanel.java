package main.project;

import backend.JavaFxBackend.UserItem;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

/*
 * 선택된 프로젝트에 속한 계정 목록만 담당하는 하위 패널이다.
 *
 * 프로젝트와 계정 소속 관계를 화면에서 명확히 보여주는 역할만 가진다.
 * 계정 생성, 삭제, 상세 처리 로직은 상위 ProjectPanel에 남겨 흐름을 한 곳에서 제어한다.
 */
class ProjectUserListPanel extends VBox {

    private final ListView<UserItem> listView = new ListView<>();

    ProjectUserListPanel() {
        setPadding(new Insets(18));
        setPrefWidth(330);
        setStyle(cardStyle());

        Label title = new Label("선택 프로젝트 계정");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        listView.setPrefHeight(360);
        listView.setStyle("-fx-font-size: 14px;");

        getChildren().addAll(title, listView);
        setSpacing(12);
        HBox.setHgrow(this, Priority.ALWAYS);
    }

    void setUsers(List<UserItem> users) {
        listView.getItems().setAll(users);
    }

    void clearUsers() {
        listView.getItems().clear();
    }

    UserItem selectedUser() {
        return listView.getSelectionModel().getSelectedItem();
    }

    private String cardStyle() {
        return "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: #e5e7eb;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.09), 10, 0, 0, 3);";
    }
}
