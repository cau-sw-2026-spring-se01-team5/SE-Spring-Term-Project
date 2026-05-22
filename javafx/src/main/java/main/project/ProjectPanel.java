package main.project;

import backend.JavaFxBackend.ProjectItem;
import backend.JavaFxBackend.UserItem;
import ui.UiDialog;
import enums.user.v1.UserRole;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

/*
 * 프로젝트/계정 관리 화면을 그리는 JavaFX Panel이다.
 *
 * 이 클래스는 화면 배치, 입력 다이얼로그, 사용자 이벤트 전달만 담당한다.
 * 프로젝트 생성/삭제, 계정 생성/삭제 같은 실제 처리 흐름은 ProjectController가 담당한다.
 */
public class ProjectPanel extends VBox implements ProjectView {

    private final UserRole role;
    private final ProjectListPanel projectListPanel = new ProjectListPanel();
    private final ProjectUserListPanel userListPanel = new ProjectUserListPanel();

    private Button createProjectButton;
    private Button createUserButton;
    private Button projectDetailButton;
    private Button deleteProjectButton;
    private Button deleteUserButton;
    private Button userDetailButton;
    private Button showMembersButton;
    private Button showMyRoleButton;

    public ProjectPanel(UserRole role) {
        this.role = role;
        build();
    }

    private void build() {
        /*
         * 프로젝트/계정 화면의 전체 배치를 만든다.
         * 이 클래스는 목록, 버튼, 다이얼로그 같은 화면 요소만 만들고,
         * 실제 생성/삭제 처리는 ProjectController가 담당한다.
         */
        setSpacing(16);
        setPadding(new Insets(34));

        VBox titleBox = new VBox(6);
        Label titleLabel = new Label(role == UserRole.ADMIN ? "프로젝트/계정 관리" : "프로젝트 정보");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        Label descLabel = new Label(description());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");
        titleBox.getChildren().addAll(titleLabel, descLabel);

        HBox createBox = new HBox(10);
        createBox.setAlignment(Pos.CENTER_LEFT);
        if (role == UserRole.ADMIN) {
            createProjectButton = primaryButton("프로젝트 추가");
            createUserButton = primaryButton("선택 프로젝트에 계정 추가");
            createBox.getChildren().addAll(createProjectButton, createUserButton);
        }

        HBox lists = new HBox(18);
        lists.getChildren().addAll(projectListPanel, userListPanel);

        HBox actionBox = new HBox(10);
        actionBox.getChildren().addAll(roleActions());

        getChildren().add(titleBox);
        if (role == UserRole.ADMIN) {
            getChildren().add(createBox);
        }
        getChildren().addAll(lists, actionBox);
    }

    private Button[] roleActions() {
        /*
         * 역할별로 프로젝트 화면에서 가능한 동작 버튼을 다르게 보여준다.
         * admin은 생성/삭제/상세 확인을 수행하고,
         * PL/DEV/TESTER는 자신의 프로젝트 정보 확인 위주로 제한된다.
         */
        return switch (role) {
            case ADMIN -> {
                projectDetailButton = secondaryButton("프로젝트 상세");
                deleteProjectButton = secondaryButton("프로젝트 삭제");
                deleteUserButton = secondaryButton("계정 삭제");
                userDetailButton = secondaryButton("계정 상세");
                yield new Button[]{projectDetailButton, deleteProjectButton, deleteUserButton, userDetailButton};
            }
            case PL -> {
                projectDetailButton = secondaryButton("프로젝트 상세");
                showMembersButton = secondaryButton("구성원 확인");
                yield new Button[]{projectDetailButton, showMembersButton};
            }
            case DEV, TESTER -> {
                projectDetailButton = secondaryButton("프로젝트 상세");
                showMyRoleButton = secondaryButton("내 역할 확인");
                yield new Button[]{projectDetailButton, showMyRoleButton};
            }
        };
    }

    @Override
    public void setProjects(List<ProjectItem> projects) {
        projectListPanel.setProjects(projects);
    }

    @Override
    public void setUsers(List<UserItem> users) {
        userListPanel.setUsers(users);
    }

    @Override
    public void clearUsers() {
        userListPanel.clearUsers();
    }

    @Override
    public ProjectItem selectedProject() {
        return projectListPanel.selectedProject();
    }

    @Override
    public UserItem selectedUser() {
        return userListPanel.selectedUser();
    }

    @Override
    public void selectProject(ProjectItem project) {
        projectListPanel.select(project);
    }

    @Override
    public Optional<CreateProjectForm> showCreateProjectDialog() {
        /*
         * 프로젝트 생성 다이얼로그는 프로젝트명과 설명 입력만 담당한다.
         * 입력 결과는 CreateProjectForm으로 Controller에 전달하고,
         * backend.addProject 호출은 ProjectController가 수행한다.
         */
        Dialog<CreateProjectForm> dialog = baseDialog("프로젝트 추가");
        GridPane form = formGrid();
        TextField nameField = new TextField();
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(3);
        addRow(form, 0, "프로젝트명", nameField);
        addRow(form, 1, "설명", descriptionArea);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(button -> {
            if (!button.getButtonData().isDefaultButton()) {
                return null;
            }
            if (nameField.getText().isBlank()) {
                UiDialog.showWarning("프로젝트명을 입력하세요.");
                return null;
            }
            return new CreateProjectForm(nameField.getText(), descriptionArea.getText());
        });
        return dialog.showAndWait();
    }

    @Override
    public Optional<CreateUserForm> showCreateUserDialog(ProjectItem project) {
        /*
         * 계정 생성 다이얼로그는 선택된 프로젝트에 추가할 계정 정보를 입력받는다.
         * 중복 ID 검사나 실제 생성 요청은 Controller가 처리하므로,
         * View는 입력값을 CreateUserForm으로 묶어 반환하는 데 집중한다.
         */
        Dialog<CreateUserForm> dialog = baseDialog("계정 추가");
        GridPane form = formGrid();
        TextField loginField = new TextField();
        PasswordField passwordField = new PasswordField();
        ComboBox<UserRole> roleBox = new ComboBox<>();
        roleBox.getItems().addAll(UserRole.PL, UserRole.DEV, UserRole.TESTER);
        roleBox.setValue(UserRole.DEV);
        addRow(form, 0, "프로젝트", new Label(project.name()));
        addRow(form, 1, "계정 ID", loginField);
        addRow(form, 2, "비밀번호", passwordField);
        addRow(form, 3, "역할", roleBox);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(button -> {
            if (!button.getButtonData().isDefaultButton()) {
                return null;
            }
            if (loginField.getText().isBlank() || passwordField.getText().isBlank()) {
                UiDialog.showWarning("계정 ID와 비밀번호를 입력하세요.");
                return null;
            }
            return new CreateUserForm(loginField.getText(), passwordField.getText(), roleBox.getValue());
        });
        return dialog.showAndWait();
    }

    @Override
    public void showProjectDetail(ProjectItem project) {
        UiDialog.showInfo("프로젝트 상세", "번호: " + project.id() + "\n프로젝트명: " + project.name() + "\n설명: " + project.description());
    }

    @Override
    public void showUserDetail(UserItem user) {
        UiDialog.showInfo("계정 상세", "번호: " + user.id() + "\n계정 ID: " + user.loginId() + "\n비밀번호: " + user.password() + "\n역할: " + roleText(user.role()));
    }

    @Override
    public void showMembers(ProjectItem project) {
        UiDialog.showInfo("프로젝트 구성원", project.name() + "의 구성원은 오른쪽 목록에서 확인할 수 있습니다.");
    }

    @Override
    public void showMyRole(String loginId, UserRole role) {
        UiDialog.showInfo("내 역할", loginId + " 계정의 역할은 " + roleText(role) + "입니다.");
    }

    @Override
    public void showWarning(String message) {
        UiDialog.showWarning(message);
    }

    @Override
    public void onProjectSelected(Runnable handler) {
        projectListPanel.onSelectionChanged(handler);
    }

    @Override
    public void onCreateProject(Runnable handler) {
        /*
         * 버튼 이벤트 등록 메서드이다.
         * ProjectPanel은 handler를 실행할 뿐이고, handler 내부의 실제 기능 흐름은 Controller가 가진다.
         */
        if (createProjectButton != null) {
            createProjectButton.setOnAction(event -> handler.run());
        }
    }

    @Override
    public void onCreateUser(Runnable handler) {
        if (createUserButton != null) {
            createUserButton.setOnAction(event -> handler.run());
        }
    }

    @Override
    public void onProjectDetail(Runnable handler) {
        projectDetailButton.setOnAction(event -> handler.run());
    }

    @Override
    public void onDeleteProject(Runnable handler) {
        if (deleteProjectButton != null) {
            deleteProjectButton.setOnAction(event -> handler.run());
        }
    }

    @Override
    public void onDeleteUser(Runnable handler) {
        if (deleteUserButton != null) {
            deleteUserButton.setOnAction(event -> handler.run());
        }
    }

    @Override
    public void onUserDetail(Runnable handler) {
        if (userDetailButton != null) {
            userDetailButton.setOnAction(event -> handler.run());
        }
    }

    @Override
    public void onShowMembers(Runnable handler) {
        if (showMembersButton != null) {
            showMembersButton.setOnAction(event -> handler.run());
        }
    }

    @Override
    public void onShowMyRole(Runnable handler) {
        if (showMyRoleButton != null) {
            showMyRoleButton.setOnAction(event -> handler.run());
        }
    }

    private String description() {
        return switch (role) {
            case ADMIN -> "관리자는 프로젝트를 만들고, 선택한 프로젝트에 PL/개발자/테스터 계정을 생성합니다.";
            case PL -> "PL은 자신이 속한 프로젝트와 구성원을 확인합니다.";
            case DEV -> "개발자는 자신이 속한 프로젝트 정보를 확인합니다.";
            case TESTER -> "테스터는 자신이 속한 프로젝트 정보를 확인합니다.";
        };
    }

    private <T> Dialog<T> baseDialog(String title) {
        /*
         * 프로젝트 화면에서 사용하는 다이얼로그 기본 설정을 한 곳에 모았다.
         * 같은 스타일의 확인/취소 버튼을 반복해서 만들지 않기 위한 공통화이다.
         */
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

    private String roleText(UserRole role) {
        return switch (role) {
            case ADMIN -> "관리자";
            case PL -> "PL";
            case DEV -> "개발자";
            case TESTER -> "테스터";
        };
    }
}
