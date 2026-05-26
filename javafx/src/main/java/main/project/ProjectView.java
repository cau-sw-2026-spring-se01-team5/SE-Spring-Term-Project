package main.project;

import enums.user.v1.UserRole;
import model.JavaFxData.ProjectItem;
import model.JavaFxData.UserItem;

import java.util.List;
import java.util.Optional;

/*
 * 프로젝트/계정 화면이 외부에 제공하는 View 인터페이스이다.
 *
 * Controller는 이 인터페이스만 사용하므로 JavaFX 구체 위젯에 직접 의존하지 않는다.
 * 화면 입력은 record로 묶어 전달하고, 실제 프로젝트/계정 생성은 Controller가 backend에 요청한다.
 */
public interface ProjectView {

    record CreateProjectForm(String name, String description) {
    }

    record CreateUserForm(String loginId, String password, UserRole role) {
    }

    void setProjects(List<ProjectItem> projects);

    void setUsers(List<UserItem> users);

    void clearUsers();

    ProjectItem selectedProject();

    UserItem selectedUser();

    void selectProject(ProjectItem project);

    Optional<CreateProjectForm> showCreateProjectDialog();

    Optional<CreateUserForm> showCreateUserDialog(ProjectItem project);

    void showProjectDetail(ProjectItem project);

    void showUserDetail(UserItem user);

    void showMembers(ProjectItem project);

    void showMyRole(String loginId, UserRole role);

    void showWarning(String message);

    void onProjectSelected(Runnable handler);

    void onEnterProject(Runnable handler);

    void onCreateProject(Runnable handler);

    void onCreateUser(Runnable handler);

    void onProjectDetail(Runnable handler);

    void onDeleteProject(Runnable handler);

    void onDeleteUser(Runnable handler);

    void onUserDetail(Runnable handler);

    void onShowMembers(Runnable handler);

    void onShowMyRole(Runnable handler);
}
