package view.project;

import app.BackendProvider;
import app.JavaFxBackend;
import app.JavaFxBackend.ProjectItem;
import app.JavaFxBackend.UserItem;
import app.SceneManager;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/*
 * 프로젝트와 프로젝트별 계정을 관리하는 화면이다.
 *
 * 화면은 JavaFxBackend 인터페이스에만 의존한다.
 * 관리자용 추가 버튼은 제목 줄과 분리해서 창 폭이 좁아도 잘리지 않도록 배치한다.
 */
public class ProjectView extends BorderPane {

    private final String loginId;
    private final UserRole role;
    private final JavaFxBackend backend = BackendProvider.backend();

    private final ListView<ProjectItem> projectList = new ListView<>();
    private final ListView<UserItem> userList = new ListView<>();

    public ProjectView(String loginId, UserRole role) {
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

        Label subtitle = new Label(role == UserRole.ADMIN ? "프로젝트/계정 관리" : "프로젝트 정보");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #9ca3af;");

        Button dashboardButton = menuButton("대시보드");
        Button issueButton = menuButton("이슈 관리");
        Button logoutButton = menuButton("로그아웃");
        dashboardButton.setOnAction(e -> SceneManager.showDashboardView());
        issueButton.setOnAction(e -> SceneManager.showIssueListView());
        logoutButton.setOnAction(e -> SceneManager.logout());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().addAll(logo, subtitle, dashboardButton, issueButton, spacer, logoutButton);
        return sidebar;
    }

    private VBox createContent() {
        /*
         * 관리자 화면의 핵심 작업은 "프로젝트 선택 -> 해당 프로젝트 계정 관리"이다.
         * 그래서 프로젝트 목록과 선택 프로젝트의 계정 목록을 나란히 배치했다.
         * 사용자가 지금 어느 프로젝트에 계정을 추가/삭제하는지 바로 확인할 수 있게 하기 위한 구성이다.
         */
        VBox content = new VBox(16);
        content.setPadding(new Insets(34));

        VBox titleBox = new VBox(6);
        Label titleLabel = new Label(role == UserRole.ADMIN ? "프로젝트/계정 관리" : "프로젝트 정보");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        Label descLabel = new Label(description());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");
        titleBox.getChildren().addAll(titleLabel, descLabel);

        HBox adminCreateBox = new HBox(10);
        adminCreateBox.setAlignment(Pos.CENTER_LEFT);
        if (role == UserRole.ADMIN) {
            /*
             * 처음에는 생성 버튼을 제목 오른쪽에 두었지만, 긴 버튼 문구가 창 폭에 따라 잘리는 문제가 있었다.
             * 그래서 생성 버튼은 제목 아래 별도 줄로 분리했다.
             * 이는 단순히 글자를 줄이는 임시방편보다 레이아웃 자체를 안정적으로 만드는 선택이다.
             */
            Button createProjectButton = primaryButton("프로젝트 추가");
            Button createUserButton = primaryButton("선택 프로젝트에 계정 추가");
            createProjectButton.setOnAction(e -> createProjectDialog());
            createUserButton.setOnAction(e -> createUserDialog());
            adminCreateBox.getChildren().addAll(createProjectButton, createUserButton);
        }

        projectList.setPrefHeight(360);
        projectList.setStyle("-fx-font-size: 14px;");
        projectList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> refreshProjectUsers());

        userList.setPrefHeight(360);
        userList.setStyle("-fx-font-size: 14px;");

        HBox lists = new HBox(18);
        lists.getChildren().addAll(listCard("프로젝트 목록", projectList), listCard("선택 프로젝트 계정", userList));

        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        actionBox.getChildren().addAll(roleActions());

        content.getChildren().add(titleBox);
        if (role == UserRole.ADMIN) {
            content.getChildren().add(adminCreateBox);
        }
        content.getChildren().addAll(lists, actionBox);
        refreshLists();
        return content;
    }

    private Button[] roleActions() {
        /*
         * 역할별로 필요한 작업만 보여준다.
         * UI 수준에서 사용하지 않는 기능을 숨기면 사용자의 실수를 줄이고, 각 역할의 책임이 화면에서도 분명해진다.
         */
        return switch (role) {
            case ADMIN -> new Button[]{
                    actionButton("프로젝트 상세", this::showProjectDetail),
                    actionButton("프로젝트 삭제", this::deleteProject),
                    actionButton("계정 삭제", this::deleteUser),
                    actionButton("계정 상세", this::showUserDetail)
            };
            case PL -> new Button[]{
                    actionButton("프로젝트 상세", this::showProjectDetail),
                    actionButton("구성원 확인", this::showMembers)
            };
            case DEV, TESTER -> new Button[]{
                    actionButton("프로젝트 상세", this::showProjectDetail),
                    actionButton("내 역할 확인", () -> showInfo("내 역할", loginId + " 계정의 역할은 " + roleText(role) + "입니다."))
            };
        };
    }

    private void refreshLists() {
        /*
         * 관리자는 전체 프로젝트를 보고, 일반 사용자는 자신이 속한 프로젝트만 본다.
         * 목록 갱신 로직을 한 메서드에 모아 프로젝트 추가/삭제 후에도 같은 방식으로 화면을 새로 그린다.
         */
        projectList.getItems().setAll(role == UserRole.ADMIN ? backend.projects() : backend.projectsForUser(loginId, role));
        if (!projectList.getItems().isEmpty() && projectList.getSelectionModel().getSelectedItem() == null) {
            projectList.getSelectionModel().selectFirst();
        }
        refreshProjectUsers();
    }

    private void refreshProjectUsers() {
        /*
         * 왼쪽 프로젝트 선택이 바뀌면 오른쪽 계정 목록도 같이 바뀐다.
         * 프로젝트와 계정의 소속 관계를 UI에서 바로 확인할 수 있도록 한 구성이다.
         */
        ProjectItem selected = projectList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            userList.getItems().clear();
            return;
        }
        userList.getItems().setAll(backend.usersForProject(selected.id()));
    }

    private void createProjectDialog() {
        /*
         * 프로젝트 생성은 관리자 유스케이스이므로 role을 한 번 더 확인한다.
         * 버튼은 관리자에게만 보이지만, 이벤트 처리 단계에서도 방어적으로 검사한다.
         */
        if (role != UserRole.ADMIN) {
            showWarning("관리자만 프로젝트를 추가할 수 있습니다.");
            return;
        }
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
                    showWarning("프로젝트명을 입력하세요.");
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
        /*
         * 계정은 반드시 선택된 프로젝트에 소속되어야 한다.
         * 그래서 계정 생성 전에 프로젝트 선택 여부를 먼저 확인한다.
         */
        if (role != UserRole.ADMIN) {
            showWarning("관리자만 계정을 추가할 수 있습니다.");
            return;
        }
        ProjectItem selectedProject = projectList.getSelectionModel().getSelectedItem();
        if (selectedProject == null) {
            showWarning("먼저 계정을 추가할 프로젝트를 선택하세요.");
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
                    showWarning("계정 ID와 비밀번호를 입력하세요.");
                    return null;
                }
                if (backend.hasLoginId(loginField.getText())) {
                    showWarning("이미 존재하는 계정 ID입니다.");
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
            showWarning("먼저 프로젝트를 선택하세요.");
            return;
        }
        showInfo("프로젝트 상세", "번호: " + selected.id() + "\n프로젝트명: " + selected.name() + "\n설명: " + selected.description());
    }

    private void deleteProject() {
        ProjectItem selected = projectList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("먼저 프로젝트를 선택하세요.");
            return;
        }
        backend.deleteProject(selected.id());
        refreshLists();
    }

    private void deleteUser() {
        UserItem selected = userList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("먼저 계정을 선택하세요.");
            return;
        }
        backend.deleteUser(selected.loginId());
        refreshProjectUsers();
    }

    private void showUserDetail() {
        UserItem selected = userList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("먼저 계정을 선택하세요.");
            return;
        }
        showInfo("계정 상세", "번호: " + selected.id() + "\n계정 ID: " + selected.loginId() + "\n비밀번호: " + selected.password() + "\n역할: " + roleText(selected.role()));
    }

    private void showMembers() {
        ProjectItem selected = projectList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("먼저 프로젝트를 선택하세요.");
            return;
        }
        showInfo("프로젝트 구성원", selected.name() + "의 구성원은 오른쪽 목록에서 확인할 수 있습니다.");
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
        return switch (role) {
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

    private Button menuButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(42);
        button.setStyle("-fx-background-color: #1f2937; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-cursor: hand;");
        return button;
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

    private void showInfo(String title, String content) {
        UiDialog.showInfo(title, content);
    }

    private void showWarning(String content) {
        UiDialog.showWarning(content);
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
