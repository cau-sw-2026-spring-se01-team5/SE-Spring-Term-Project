package main.project;

import app.JavaFxBackend;
import app.JavaFxBackend.ProjectItem;
import app.JavaFxBackend.UserItem;
import app.UiDialog;
import enums.user.v1.UserRole;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import session.UserSession;

/*
 * 프로젝트/계정 관리 본문 패널이다.
 *
 * Swing의 기능별 Panel처럼 메인 화면의 center 영역에 들어가는 실제 UI이다.
 * 프로젝트 목록과 선택 프로젝트 계정 목록을 나란히 두어 소속 관계를 명확히 보여준다.
 */
public class ProjectPanel extends VBox {

    private final JavaFxBackend backend;
    private final UserSession session;
    private final ListView<ProjectItem> projectList = new ListView<>();
    private final ListView<UserItem> userList = new ListView<>();

    public ProjectPanel(JavaFxBackend backend, UserSession session) {
        this.backend = backend;
        this.session = session;
        build();
    }

    private void build() {
        setSpacing(16);
        setPadding(new Insets(34));

        VBox titleBox = new VBox(6);
        Label titleLabel = new Label(session.role() == UserRole.ADMIN ? "프로젝트/계정 관리" : "프로젝트 정보");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        Label descLabel = new Label(description());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");
        titleBox.getChildren().addAll(titleLabel, descLabel);

        HBox createBox = new HBox(10);
        createBox.setAlignment(Pos.CENTER_LEFT);
        if (session.role() == UserRole.ADMIN) {
            Button createProjectButton = primaryButton("프로젝트 추가");
            Button createUserButton = primaryButton("선택 프로젝트에 계정 추가");
            createProjectButton.setOnAction(e -> createProjectDialog());
            createUserButton.setOnAction(e -> createUserDialog());
            createBox.getChildren().addAll(createProjectButton, createUserButton);
        }

        projectList.setPrefHeight(360);
        projectList.setStyle("-fx-font-size: 14px;");
        projectList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> refreshProjectUsers());

        userList.setPrefHeight(360);
        userList.setStyle("-fx-font-size: 14px;");

        HBox lists = new HBox(18);
        lists.getChildren().addAll(listCard("프로젝트 목록", projectList), listCard("선택 프로젝트 계정", userList));

        HBox actionBox = new HBox(10);
        actionBox.getChildren().addAll(roleActions());

        getChildren().add(titleBox);
        if (session.role() == UserRole.ADMIN) {
            getChildren().add(createBox);
        }
        getChildren().addAll(lists, actionBox);
        refreshLists();
    }

    private Button[] roleActions() {
        return switch (session.role()) {
            case ADMIN -> new Button[]{
                    actionButton("프로젝트 상세", this::showProjectDetail),
                    actionButton("프로젝트 삭제", this::deleteProject),
                    actionButton("계정 삭제", this::deleteUser),
                    actionButton("계정 상세", this::showUserDetail)
            };
            case PL -> new Button[]{actionButton("프로젝트 상세", this::showProjectDetail), actionButton("구성원 확인", this::showMembers)};
            case DEV, TESTER -> new Button[]{actionButton("프로젝트 상세", this::showProjectDetail), actionButton("내 역할 확인", this::showMyRole)};
        };
    }

    private void refreshLists() {
        projectList.getItems().setAll(session.role() == UserRole.ADMIN ? backend.projects() : backend.projectsForUser(session.loginId(), session.role()));
        if (!projectList.getItems().isEmpty() && projectList.getSelectionModel().getSelectedItem() == null) {
            projectList.getSelectionModel().selectFirst();
        }
        refreshProjectUsers();
    }

    private void refreshProjectUsers() {
        ProjectItem selected = projectList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            userList.getItems().clear();
            return;
        }
        userList.getItems().setAll(backend.usersForProject(selected.id()));
    }

    private void createProjectDialog() {
        Dialog<Void> dialog = baseDialog("프로젝트 추가");
        GridPane form = formGrid();
        TextField nameField = new TextField();
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(3);
        addRow(form, 0, "프로젝트명", nameField);
        addRow(form, 1, "설명", descriptionArea);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(button -> {
            if (button.getButtonData().isDefaultButton()) {
                if (nameField.getText().isBlank()) {
                    UiDialog.showWarning("프로젝트명을 입력하세요.");
                    return null;
                }
                ProjectItem project = backend.addProject(nameField.getText(), descriptionArea.getText());
                refreshLists();
                projectList.getSelectionModel().select(project);
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void createUserDialog() {
        ProjectItem selectedProject = projectList.getSelectionModel().getSelectedItem();
        if (selectedProject == null) {
            UiDialog.showWarning("먼저 계정을 추가할 프로젝트를 선택하세요.");
            return;
        }
        Dialog<Void> dialog = baseDialog("계정 추가");
        GridPane form = formGrid();
        TextField loginField = new TextField();
        PasswordField passwordField = new PasswordField();
        ComboBox<UserRole> roleBox = new ComboBox<>();
        roleBox.getItems().addAll(UserRole.PL, UserRole.DEV, UserRole.TESTER);
        roleBox.setValue(UserRole.DEV);
        addRow(form, 0, "프로젝트", new Label(selectedProject.name()));
        addRow(form, 1, "계정 ID", loginField);
        addRow(form, 2, "비밀번호", passwordField);
        addRow(form, 3, "역할", roleBox);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(button -> {
            if (button.getButtonData().isDefaultButton()) {
                if (loginField.getText().isBlank() || passwordField.getText().isBlank()) {
                    UiDialog.showWarning("계정 ID와 비밀번호를 입력하세요.");
                    return null;
                }
                if (backend.hasLoginId(loginField.getText())) {
                    UiDialog.showWarning("이미 존재하는 계정 ID입니다.");
                    return null;
                }
                backend.addUser(loginField.getText(), passwordField.getText(), roleBox.getValue(), selectedProject.id());
                refreshProjectUsers();
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void showProjectDetail() {
        ProjectItem selected = projectList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiDialog.showWarning("먼저 프로젝트를 선택하세요.");
            return;
        }
        UiDialog.showInfo("프로젝트 상세", "번호: " + selected.id() + "\n프로젝트명: " + selected.name() + "\n설명: " + selected.description());
    }

    private void deleteProject() {
        ProjectItem selected = projectList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiDialog.showWarning("먼저 프로젝트를 선택하세요.");
            return;
        }
        backend.deleteProject(selected.id());
        refreshLists();
    }

    private void deleteUser() {
        UserItem selected = userList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiDialog.showWarning("먼저 계정을 선택하세요.");
            return;
        }
        backend.deleteUser(selected.loginId());
        refreshProjectUsers();
    }

    private void showUserDetail() {
        UserItem selected = userList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiDialog.showWarning("먼저 계정을 선택하세요.");
            return;
        }
        UiDialog.showInfo("계정 상세", "번호: " + selected.id() + "\n계정 ID: " + selected.loginId() + "\n비밀번호: " + selected.password() + "\n역할: " + roleText(selected.role()));
    }

    private void showMembers() {
        ProjectItem selected = projectList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiDialog.showWarning("먼저 프로젝트를 선택하세요.");
            return;
        }
        UiDialog.showInfo("프로젝트 구성원", selected.name() + "의 구성원은 오른쪽 목록에서 확인할 수 있습니다.");
    }

    private void showMyRole() {
        UiDialog.showInfo("내 역할", session.loginId() + " 계정의 역할은 " + roleText(session.role()) + "입니다.");
    }

    private VBox listCard(String titleText, ListView<?> listView) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setPrefWidth(330);
        card.setStyle(cardStyle());
        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        card.getChildren().addAll(title, listView);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private String description() {
        return switch (session.role()) {
            case ADMIN -> "관리자는 프로젝트를 만들고, 선택한 프로젝트에 PL/개발자/테스터 계정을 생성합니다.";
            case PL -> "PL은 자신이 속한 프로젝트와 구성원을 확인합니다.";
            case DEV -> "개발자는 자신이 속한 프로젝트 정보를 확인합니다.";
            case TESTER -> "테스터는 자신이 속한 프로젝트 정보를 확인합니다.";
        };
    }

    private Button actionButton(String text, Runnable action) {
        Button button = secondaryButton(text);
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
        button.setMinWidth(130);
        button.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
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

    private String roleText(UserRole role) {
        return switch (role) {
            case ADMIN -> "관리자";
            case PL -> "PL";
            case DEV -> "개발자";
            case TESTER -> "테스터";
        };
    }
}
