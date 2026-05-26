package main.project;

import app.JavaFxMapper;
import app.JavaFxServices;
import enums.user.v1.UserRole;
import model.JavaFxData.ProjectItem;
import model.JavaFxData.UserItem;
import project.dto.createProject.v1.CreateProjectInput;
import project.dto.deleteProject.v1.DeleteProjectInput;
import project.dto.getProjectList.v1.GetProjectListInput;
import session.UserSession;
import user.dto.createUser.v1.CreateUserInput;
import user.dto.deleteUser.v1.DeleteUserInput;
import user.dto.getProjectUserList.v1.GetProjectUserListInput;

import java.util.ArrayList;
import java.util.List;

public class ProjectController {

    private final ProjectView view;
    private final JavaFxServices services;
    private final UserSession session;
    private final Runnable enterProjectCallback;

    public ProjectController(ProjectView view, JavaFxServices services, UserSession session, Runnable enterProjectCallback) {
        this.view = view;
        this.services = services;
        this.session = session;
        this.enterProjectCallback = enterProjectCallback;
        bind();
    }

    public void start() {
        refreshProjects();
    }

    private void bind() {
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
        var output = services.project().getProjectList(new GetProjectListInput(session.userId()));
        if (!output.success()) {
            view.showWarning(output.message());
            view.setProjects(List.of());
            view.clearUsers();
            return;
        }

        List<ProjectItem> projects = output.projectList().stream()
                .map(JavaFxMapper::projectItem)
                .toList();
        view.setProjects(projects);
        refreshProjectUsers();
    }

    private void refreshProjectUsers() {
        ProjectItem selected = view.selectedProject();
        if (selected == null) {
            view.clearUsers();
            return;
        }

        var output = services.user().getProjectUserList(new GetProjectUserListInput(selected.id()));
        if (!output.success()) {
            view.showWarning(output.message());
            view.clearUsers();
            return;
        }

        view.setUsers(output.userList().stream().map(JavaFxMapper::userItem).toList());
    }

    private void createProject() {
        view.showCreateProjectDialog().ifPresent(form -> {
            var output = services.project().createProject(new CreateProjectInput(form.name()));
            if (!output.success()) {
                view.showWarning(output.message());
                return;
            }

            refreshProjects();
            projects().stream()
                    .filter(project -> project.id().equals(output.projectId()))
                    .findFirst()
                    .ifPresent(view::selectProject);
        });
    }

    private void createUser() {
        ProjectItem selectedProject = view.selectedProject();
        if (selectedProject == null) {
            view.showWarning("먼저 계정을 추가할 프로젝트를 선택하세요.");
            return;
        }

        view.showCreateUserDialog(selectedProject).ifPresent(form -> {
            if (hasLoginId(form.loginId())) {
                view.showWarning("이미 존재하는 계정 ID입니다.");
                return;
            }

            var output = services.user().createUser(new CreateUserInput(
                    session.userId(),
                    form.loginId(),
                    form.password(),
                    form.role(),
                    selectedProject.id()
            ));
            if (!output.success()) {
                view.showWarning(output.message());
                return;
            }
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
        ProjectItem selected = view.selectedProject();
        if (selected == null) {
            view.showWarning("먼저 프로젝트를 선택하세요.");
            return;
        }

        var output = services.project().deleteProject(new DeleteProjectInput(session.userId(), selected.id()));
        if (!output.success()) {
            view.showWarning(output.message());
            return;
        }
        refreshProjects();
    }

    private void deleteUser() {
        UserItem selected = view.selectedUser();
        if (selected == null) {
            view.showWarning("먼저 계정을 선택하세요.");
            return;
        }

        var output = services.user().deleteUser(new DeleteUserInput(session.userId(), selected.id(), selected.projectId()));
        if (!output.success()) {
            view.showWarning(output.message());
            return;
        }
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

    private List<ProjectItem> projects() {
        var output = services.project().getProjectList(new GetProjectListInput(session.userId()));
        if (!output.success()) {
            return List.of();
        }
        return output.projectList().stream().map(JavaFxMapper::projectItem).toList();
    }

    private boolean hasLoginId(String loginId) {
        List<UserItem> users = new ArrayList<>();
        for (ProjectItem project : projects()) {
            var output = services.user().getProjectUserList(new GetProjectUserListInput(project.id()));
            if (output.success()) {
                users.addAll(output.userList().stream().map(JavaFxMapper::userItem).toList());
            }
        }
        return users.stream().anyMatch(user -> user.loginId().equals(loginId));
    }
}
