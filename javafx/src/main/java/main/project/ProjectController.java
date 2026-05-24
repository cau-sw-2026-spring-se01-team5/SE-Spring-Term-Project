package main.project;

import backend.JavaFxBackend;
import backend.JavaFxBackend.ProjectItem;
import backend.JavaFxBackend.UserItem;
import enums.user.v1.UserRole;
import session.UserSession;

/*
 * 프로젝트/계정 기능의 사용자 이벤트를 처리하는 Controller이다.
 *
 * ProjectPanel은 화면과 입력만 담당하고, 프로젝트 생성/삭제와 계정 생성/삭제는 이 Controller가
 * JavaFxBackend에 요청한다. Swing의 UserController/ProjectSelectController와 같은 분리 의도이다.
 */
public class ProjectController {

    private final ProjectView view;
    private final JavaFxBackend backend;
    private final UserSession session;
    private final Runnable enterProjectCallback;

    public ProjectController(ProjectView view, JavaFxBackend backend, UserSession session, Runnable enterProjectCallback) {
        this.view = view;
        this.backend = backend;
        this.session = session;
        this.enterProjectCallback = enterProjectCallback;
        bind();
    }

    public void start() {
        /*
         * 프로젝트 화면이 열리면 먼저 현재 사용자가 볼 수 있는 프로젝트 목록을 불러온다.
         * admin은 전체 프로젝트를 보고, 일반 사용자는 자신이 속한 프로젝트만 본다.
         */
        refreshProjects();
    }

    private void bind() {
        /*
         * View의 버튼/선택 이벤트를 Controller 메서드에 연결한다.
         * ProjectPanel은 이벤트 발생만 알려주고, 실제 처리 순서는 Controller가 담당한다.
         */
        view.onProjectSelected(this::refreshProjectUsers);
        view.onEnterProject(this::enterProject);
        view.onCreateProject(this::createProject);
        view.onCreateUser(this::createUser);
        view.onProjectDetail(this::showProjectDetail);
        view.onDeleteProject(this::deleteProject);
        view.onDeleteUser(this::deleteUser);
        view.onUserDetail(this::showUserDetail);
        view.onShowMembers(this::showMembers);
        view.onShowMyRole(this::showMyRole);
    }

    private void refreshProjects() {
        /*
         * 역할에 따라 프로젝트 조회 범위를 다르게 한다.
         * 이 조건을 View에 두지 않아 화면 클래스가 권한 판단 책임을 갖지 않게 했다.
         */
        view.setProjects(session.role() == UserRole.ADMIN
                ? backend.projects()
                : backend.projectsForUser(session.loginId(), session.role()));
        refreshProjectUsers();
    }

    private void refreshProjectUsers() {
        /*
         * 왼쪽 프로젝트 목록에서 선택된 프로젝트가 바뀌면 오른쪽 계정 목록도 갱신한다.
         * 프로젝트와 계정의 소속 관계를 화면에 항상 맞춰 보여주기 위한 처리이다.
         */
        ProjectItem selected = view.selectedProject();
        if (selected == null) {
            view.clearUsers();
            return;
        }
        view.setUsers(backend.usersForProject(selected.id()));
    }

    private void createProject() {
        /*
         * 프로젝트 생성 다이얼로그는 View가 띄우고,
         * 생성 요청과 목록 갱신은 Controller가 처리한다.
         */
        view.showCreateProjectDialog().ifPresent(form -> {
            ProjectItem project = backend.addProject(form.name(), form.description());
            refreshProjects();
            view.selectProject(project);
        });
    }

    private void createUser() {
        /*
         * 계정은 반드시 선택된 프로젝트에 소속되어 생성된다.
         * 따라서 먼저 프로젝트 선택 여부를 확인하고, 중복 ID도 Controller에서 검사한다.
         */
        ProjectItem selectedProject = view.selectedProject();
        if (selectedProject == null) {
            view.showWarning("먼저 계정을 추가할 프로젝트를 선택하세요.");
            return;
        }

        view.showCreateUserDialog(selectedProject).ifPresent(form -> {
            if (backend.hasLoginId(form.loginId())) {
                view.showWarning("이미 존재하는 계정 ID입니다.");
                return;
            }
            backend.addUser(form.loginId(), form.password(), form.role(), selectedProject.id());
            refreshProjectUsers();
        });
    }

    private void enterProject() {
        ProjectItem selected = view.selectedProject();
        if (selected == null) {
            view.showWarning("먼저 프로젝트를 선택하세요.");
            return;
        }

        session.selectProject(selected.id(), selected.name());
        enterProjectCallback.run();
    }

    private void showProjectDetail() {
        ProjectItem selected = view.selectedProject();
        if (selected == null) {
            view.showWarning("먼저 프로젝트를 선택하세요.");
            return;
        }
        view.showProjectDetail(selected);
    }

    private void deleteProject() {
        /*
         * 프로젝트 삭제 후에는 프로젝트 목록과 계정 목록을 다시 불러온다.
         * 삭제된 프로젝트의 계정 목록이 화면에 남지 않게 하기 위한 처리이다.
         */
        ProjectItem selected = view.selectedProject();
        if (selected == null) {
            view.showWarning("먼저 프로젝트를 선택하세요.");
            return;
        }
        backend.deleteProject(selected.id());
        refreshProjects();
    }

    private void deleteUser() {
        /*
         * 계정 삭제 후에는 선택된 프로젝트의 계정 목록만 다시 갱신한다.
         * 전체 프로젝트 목록을 다시 가져올 필요가 없으므로 갱신 범위를 좁혔다.
         */
        UserItem selected = view.selectedUser();
        if (selected == null) {
            view.showWarning("먼저 계정을 선택하세요.");
            return;
        }
        backend.deleteUser(selected.loginId());
        refreshProjectUsers();
    }

    private void showUserDetail() {
        UserItem selected = view.selectedUser();
        if (selected == null) {
            view.showWarning("먼저 계정을 선택하세요.");
            return;
        }
        view.showUserDetail(selected);
    }

    private void showMembers() {
        ProjectItem selected = view.selectedProject();
        if (selected == null) {
            view.showWarning("먼저 프로젝트를 선택하세요.");
            return;
        }
        view.showMembers(selected);
    }

    private void showMyRole() {
        view.showMyRole(session.loginId(), session.role());
    }
}
