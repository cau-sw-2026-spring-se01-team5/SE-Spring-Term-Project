package main.user;

import enums.user.v1.UserRole;
import main.header.HeaderController;
import session.UserSession;
import user.dto.createUser.v1.CreateUserInput;
import user.dto.deleteUser.v1.DeleteUserInput;
import user.dto.getProjectUserList.v1.GetProjectUserListInput;
import user.v1.User;

public class UserController {

    private final UserView view;
    private final User userService;
    private final UserSession session;
    private final HeaderController headerController;

    public UserController(
            UserView view,
            User userService,
            UserSession session,
            HeaderController headerController
    ) {
        this.view = view;
        this.userService = userService;
        this.session = session;
        this.headerController = headerController;

        bind();
    }

    public void applyRole() {
        boolean admin = session.role() == UserRole.ADMIN;
        view.applyAdminPermission(admin);
    }

    private void bind() {
        view.onRefreshUsers(this::refreshUsers);
        view.onCreateUser(this::createUser);
        view.onDeleteUser(this::deleteUser);
    }

    public void refreshUsers() {
        Integer projectId = requireProjectId();

        if (projectId == null) {
            return;
        }

        var output = userService.getProjectUserList(
                new GetProjectUserListInput(projectId)
        );

        if (!output.success()) {
            view.showMessage(output.message());
            return;
        }

        view.setUsers(output.userList());
    }

    private void createUser() {
        Integer projectId = requireProjectId();

        if (projectId == null) {
            return;
        }

        var output = userService.createUser(
                new CreateUserInput(
                        session.userId(),
                        view.getNewLoginIdInput(),
                        view.getNewPasswordInput(),
                        view.getNewUserRoleInput(),
                        projectId
                )
        );

        view.showMessage(output.message());

        if (output.success()) {
            refreshUsers();
        }
    }

    private void deleteUser() {
        Integer projectId = requireProjectId();

        if (projectId == null) {
            return;
        }

        Integer targetUserId = view.getSelectedTargetUserId();

        if (targetUserId == null) {
            view.showMessage("삭제할 유저를 선택하세요.");
            return;
        }

        var output = userService.deleteUser(
                new DeleteUserInput(
                        session.userId(),
                        targetUserId,
                        projectId
                )
        );

        view.showMessage(output.message());

        if (output.success()) {
            refreshUsers();
        }
    }

    private Integer requireProjectId() {
        Integer projectId = headerController.getSelectedProjectId();

        if (projectId == null) {
            view.showMessage("프로젝트를 선택하세요.");
            return null;
        }

        return projectId;
    }
}