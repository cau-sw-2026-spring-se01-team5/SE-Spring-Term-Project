package main.issue;

import app.JavaFxMapper;
import app.JavaFxServices;
import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;
import issue.dto.addIssueComment.v1.AddIssueCommentInput;
import issue.dto.assignIssue.v1.AssignIssueInput;
import issue.dto.changeIssueStatus.v1.ChangeIssueStatusInput;
import issue.dto.getIssueDetail.v1.GetIssueDetailInput;
import issue.dto.getIssueList.v1.GetIssueListInput;
import issue.dto.recommendAssignee.v1.RecommendAssigneeInput;
import issue.dto.registerIssue.v1.RegisterIssueInput;
import model.JavaFxData.IssueItem;
import model.JavaFxData.ProjectItem;
import model.JavaFxData.RegisterIssueResult;
import model.JavaFxData.UserItem;
import project.dto.getProjectList.v1.GetProjectListInput;
import session.UserSession;
import user.dto.getProjectUserList.v1.GetProjectUserListInput;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IssueController {

    private final IssueView view;
    private final JavaFxServices services;
    private final UserSession session;

    public IssueController(IssueView view, JavaFxServices services, UserSession session) {
        this.view = view;
        this.services = services;
        this.session = session;
        bind();
    }

    public void start() {
        view.setFilterOptions(projectDeveloperLoginIds(), projectTesterLoginIds());
        refreshTable();
    }

    private void bind() {
        view.onSearch(this::refreshTable);
        view.onRegisterIssue(this::registerIssue);
        view.onShowIssueDetail(this::showSelectedIssue);
        view.onAddComment(this::addComment);
        view.onAssignIssue(this::assignIssue);
        view.onRecommendAssignee(this::recommendAssignee);
        view.onCloseIssue(this::closeIssue);
        view.onShowStatistics(this::showStatistics);
        view.onMarkFixed(this::markFixed);
        view.onResolveIssue(this::resolveIssue);
        view.onReopenIssue(this::reopenIssue);
    }

    private void refreshTable() {
        List<IssueItem> filtered = currentProjectIssues().stream()
                .filter(view::matchesFilter)
                .toList();
        view.setIssues(filtered);
    }

    private void registerIssue() {
        List<ProjectItem> availableProjects = currentProjectItems();
        if (availableProjects.isEmpty()) {
            view.showWarning("선택 가능한 프로젝트가 없습니다.");
            return;
        }

        view.showRegisterIssueDialog(availableProjects).ifPresent(form -> {
            var output = services.issue().registerIssue(new RegisterIssueInput(
                    form.project().id(),
                    form.title(),
                    form.description(),
                    IssuePriority.valueOf(form.priority()),
                    session.userId()
            ));
            RegisterIssueResult result = JavaFxMapper.registerIssueResult(output.success(), output.issueId(), output.message());
            if (!result.success()) {
                view.showWarning(result.message());
                return;
            }

            if (result.issueId() != null && form.comment() != null && !form.comment().isBlank()) {
                services.issue().addIssueComment(new AddIssueCommentInput(result.issueId(), session.userId(), form.comment()));
            }

            refreshTable();
        });
    }

    private void assignIssue() {
        IssueItem selected = requireSelectedIssue();
        if (selected == null) {
            return;
        }
        if (!"NEW".equals(selected.status()) && !"REOPENED".equals(selected.status())) {
            view.showWarning("PL은 NEW 또는 REOPENED 상태의 이슈만 배정할 수 있습니다.");
            return;
        }

        view.showAssignIssueDialog(projectDeveloperLoginIds(selected.projectId()), session.loginId())
                .ifPresent(form -> {
                    Integer assigneeUserId = userIdByLoginId(selected.projectId(), form.assignee());
                    if (assigneeUserId == null) {
                        view.showWarning("선택한 개발자 계정을 찾을 수 없습니다.");
                        return;
                    }
                    var output = services.issue().assignIssue(new AssignIssueInput(
                            selected.id(),
                            session.userId(),
                            assigneeUserId,
                            form.comment()
                    ));
                    if (!output.success()) {
                        view.showWarning(output.message());
                        return;
                    }
                    refreshTable();
                });
    }

    private void markFixed() {
        IssueItem selected = requireSelectedIssue();
        if (selected == null) {
            return;
        }
        if (!session.loginId().equals(selected.assignee())) {
            view.showWarning("자신에게 배정된 이슈만 수정 완료 처리할 수 있습니다.");
            return;
        }

        view.showCommentDialog("수정 완료 처리", "수정이 완료되어 테스트 검증을 요청합니다.")
                .ifPresent(comment -> {
                    var output = services.issue().changeIssueStatus(new ChangeIssueStatusInput(
                            selected.id(),
                            session.userId(),
                            IssueStatus.FIXED
                    ));
                    if (!output.success()) {
                        view.showWarning(output.message());
                        return;
                    }
                    if (!comment.isBlank()) {
                        services.issue().addIssueComment(new AddIssueCommentInput(selected.id(), session.userId(), comment));
                    }
                    refreshTable();
                });
    }

    private void resolveIssue() {
        IssueItem selected = requireSelectedIssue();
        if (selected == null) {
            return;
        }
        if (!"FIXED".equals(selected.status())) {
            view.showWarning("FIXED 상태의 이슈만 해결 확인할 수 있습니다.");
            return;
        }

        var output = services.issue().changeIssueStatus(new ChangeIssueStatusInput(
                selected.id(),
                session.userId(),
                IssueStatus.RESOLVED
        ));
        if (!output.success()) {
            view.showWarning(output.message());
            return;
        }
        refreshTable();
    }

    private void reopenIssue() {
        IssueItem selected = requireSelectedIssue();
        if (selected == null) {
            return;
        }
        if (session.role() != UserRole.ADMIN) {
            view.showWarning("관리자만 재오픈할 수 있습니다.");
            return;
        }
        if (!"CLOSED".equals(selected.status())) {
            view.showWarning("CLOSED 상태의 이슈만 재오픈할 수 있습니다.");
            return;
        }

        var output = services.issue().changeIssueStatus(new ChangeIssueStatusInput(
                selected.id(),
                session.userId(),
                IssueStatus.REOPENED
        ));
        if (!output.success()) {
            view.showWarning(output.message());
            return;
        }
        refreshTable();
    }

    private void closeIssue() {
        IssueItem selected = requireSelectedIssue();
        if (selected == null) {
            return;
        }
        if (!"RESOLVED".equals(selected.status())) {
            view.showWarning("RESOLVED 상태의 이슈만 종료할 수 있습니다.");
            return;
        }

        var output = services.issue().changeIssueStatus(new ChangeIssueStatusInput(
                selected.id(),
                session.userId(),
                IssueStatus.CLOSED
        ));
        if (!output.success()) {
            view.showWarning(output.message());
            return;
        }
        refreshTable();
    }

    private void addComment() {
        IssueItem selected = requireSelectedIssue();
        if (selected == null) {
            return;
        }

        view.showCommentDialog("코멘트 추가", "").ifPresent(comment -> {
            var output = services.issue().addIssueComment(new AddIssueCommentInput(selected.id(), session.userId(), comment));
            if (!output.success()) {
                view.showWarning(output.message());
                return;
            }
            refreshTable();
        });
    }

    private void showSelectedIssue() {
        IssueItem selected = requireSelectedIssue();
        if (selected == null) {
            return;
        }

        var output = services.issue().getIssueDetail(new GetIssueDetailInput(selected.id()));
        if (!output.success()) {
            view.showWarning(output.message());
            return;
        }

        String assigneeLoginId = output.assigneeUserId() == null ? "" : services.roleResolver().resolveLoginId(output.assigneeUserId());
        view.showIssueDetail(JavaFxMapper.issueItem(output, assigneeLoginId));
    }

    private void recommendAssignee() {
        IssueItem selected = requireSelectedIssue();
        if (selected == null) {
            return;
        }
        if (!"NEW".equals(selected.status()) && !"REOPENED".equals(selected.status())) {
            view.showWarning("담당자 추천은 NEW 또는 REOPENED 상태의 이슈에서만 사용할 수 있습니다.");
            return;
        }

        var output = services.issue().recommendAssignees(new RecommendAssigneeInput(selected.id(), selected.projectId()));
        if (!output.success()) {
            view.showWarning(output.message());
            return;
        }

        List<String> recommendations = output.candidates().stream()
                .map(candidate -> candidate.userId())
                .toList();

        view.showRecommendationSelectDialog(recommendations).ifPresent(candidate -> {
            List<String> developers = projectDeveloperLoginIds(selected.projectId());
            view.showAssignIssueDialog(developers, session.loginId(), candidate).ifPresent(form -> {
                Integer assigneeUserId = userIdByLoginId(selected.projectId(), form.assignee());
                if (assigneeUserId == null) {
                    view.showWarning("선택한 개발자 계정을 찾을 수 없습니다.");
                    return;
                }
                var assignOutput = services.issue().assignIssue(new AssignIssueInput(
                        selected.id(),
                        session.userId(),
                        assigneeUserId,
                        form.comment()
                ));
                if (!assignOutput.success()) {
                    view.showWarning(assignOutput.message());
                    return;
                }
                refreshTable();
            });
        });
    }

    private void showStatistics() {
        List<IssueItem> visibleIssues = view.visibleIssues();
        String daily = dailyCounts(visibleIssues).entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\n"));

        if (daily.isBlank()) {
            daily = "데이터 없음";
        }

        view.showStatistics(
                "상태별 요약\n" +
                        "NEW: " + countByStatus(visibleIssues, "NEW") + "\n" +
                        "ASSIGNED: " + countByStatus(visibleIssues, "ASSIGNED") + "\n" +
                        "FIXED: " + countByStatus(visibleIssues, "FIXED") + "\n" +
                        "RESOLVED: " + countByStatus(visibleIssues, "RESOLVED") + "\n" +
                        "CLOSED: " + countByStatus(visibleIssues, "CLOSED") + "\n" +
                        "REOPENED: " + countByStatus(visibleIssues, "REOPENED") + "\n\n" +
                        "일별 발생 건수\n" + daily
        );
    }

    private IssueItem requireSelectedIssue() {
        IssueItem selected = view.selectedIssue();
        if (selected == null) {
            view.showWarning("먼저 이슈를 선택하세요.");
        }
        return selected;
    }

    private List<IssueItem> currentProjectIssues() {
        Integer selectedProjectId = session.selectedProjectId();
        if (selectedProjectId == null) {
            return List.of();
        }

        var output = services.issue().getIssueList(new GetIssueListInput(
                selectedProjectId,
                session.userId(),
                null,
                null,
                null,
                null,
                null,
                null
        ));
        if (!output.success() || output.issues() == null) {
            return List.of();
        }

        return output.issues().stream()
                .map(JavaFxMapper::issueItem)
                .toList();
    }

    private List<ProjectItem> currentProjectItems() {
        var output = services.project().getProjectList(new GetProjectListInput(session.userId()));
        if (!output.success() || output.projectList() == null) {
            return List.of();
        }

        List<ProjectItem> projects = output.projectList().stream()
                .map(JavaFxMapper::projectItem)
                .toList();
        Integer selectedProjectId = session.selectedProjectId();
        if (selectedProjectId == null) {
            return projects;
        }

        return projects.stream()
                .filter(project -> project.id().equals(selectedProjectId))
                .toList();
    }

    private List<String> projectDeveloperLoginIds() {
        Integer selectedProjectId = session.selectedProjectId();
        if (selectedProjectId == null) {
            return List.of();
        }
        return projectDeveloperLoginIds(selectedProjectId);
    }

    private List<String> projectDeveloperLoginIds(Integer projectId) {
        return usersForProject(projectId).stream()
                .filter(user -> user.role() == UserRole.DEV)
                .map(UserItem::loginId)
                .toList();
    }

    private List<String> projectTesterLoginIds() {
        Integer selectedProjectId = session.selectedProjectId();
        if (selectedProjectId == null) {
            return List.of();
        }

        return usersForProject(selectedProjectId).stream()
                .filter(user -> user.role() == UserRole.TESTER)
                .map(UserItem::loginId)
                .toList();
    }

    private List<UserItem> usersForProject(Integer projectId) {
        var output = services.user().getProjectUserList(new GetProjectUserListInput(projectId));
        if (!output.success() || output.userList() == null) {
            return List.of();
        }
        return output.userList().stream().map(JavaFxMapper::userItem).toList();
    }

    private Integer userIdByLoginId(Integer projectId, String loginId) {
        return usersForProject(projectId).stream()
                .filter(user -> user.loginId().equals(loginId))
                .map(UserItem::id)
                .findFirst()
                .orElse(null);
    }

    private long countByStatus(List<IssueItem> issues, String status) {
        return issues.stream()
                .filter(issue -> status.equals(issue.status()))
                .count();
    }

    private Map<String, Long> dailyCounts(List<IssueItem> issues) {
        return issues.stream()
                .collect(Collectors.groupingBy(
                        issue -> issue.reportedDate() == null || issue.reportedDate().length() < 10
                                ? "unknown"
                                : issue.reportedDate().substring(0, 10),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
    }
}
