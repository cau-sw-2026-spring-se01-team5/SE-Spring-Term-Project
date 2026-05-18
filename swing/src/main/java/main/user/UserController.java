package main.user;

import enums.user.v1.UserRole;
import main.support.ProjectContextGuard;
import session.UserSession;
import user.dto.createUser.v1.CreateUserInput;
import user.dto.deleteUser.v1.DeleteUserInput;
import user.dto.getProjectUserList.v1.GetProjectUserListInput;
import user.v1.User;

/* 유저 관련 화면 이벤트를 처리 */
/* 유저 목록 조회, 유저 생성, 삭제, 권한에 따른 UI 제어 담당 */
public class UserController {

    private final UserView view; // 유저 화면 UI 인터페이스
    private final User userService; // 백엔드 api
    private final UserSession session; // 현재 접속한 유저 확인
    private final ProjectContextGuard projectContextGuard; // 현재 프로젝트 파악

    public UserController(
            UserView view,
            User userService,
            UserSession session
    ) {
        this.view = view;
        this.userService = userService;
        this.session = session;
        this.projectContextGuard = new ProjectContextGuard(session);

        bind();
    }

    // 현재 로그인한 사용자의 권한을 화면에 적용
    public void applyRole() {
        boolean admin = session.role() == UserRole.ADMIN;
        view.applyAdminPermission(admin);
    }

    // 이벤트 연결 메서드
    private void bind() {
        view.onRefreshUsers(this::refreshUsers);
        view.onCreateUser(this::createUser);
        view.onDeleteUser(this::deleteUser);
    }

    // 현재 프로젝트의 유저 목록 조회
    public void refreshUsers() {
        Integer projectId = getProjectId();

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

    // 유저 생성
    private void createUser() {
        Integer projectId = getProjectId();

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

    // 유저 삭제
    private void deleteUser() {
        Integer projectId = getProjectId();

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

    // 지금 프로젝트 id 가져오기
    private Integer getProjectId() {
        return projectContextGuard.requireProjectId(view::showMessage);
    }
}
