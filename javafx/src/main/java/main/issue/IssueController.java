package main.issue;

import backend.JavaFxBackend;
import backend.JavaFxBackend.IssueItem;
import backend.JavaFxBackend.ProjectItem;
import backend.JavaFxBackend.UserItem;
import enums.user.v1.UserRole;
import session.UserSession;

import java.util.List;
import java.util.stream.Collectors;

public class IssueController {

    private final IssueView view;
    private final JavaFxBackend backend;
    private final UserSession session;

    public IssueController(IssueView view, JavaFxBackend backend, UserSession session) {
        this.view = view;
        this.backend = backend;
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
            view.showWarning("선택된 프로젝트가 없습니다.");
            return;
        }

        view.showRegisterIssueDialog(availableProjects).ifPresent(form -> {
            String errorMessage = backend.registerIssue(
                    form.project().id(),
                    form.title(),
                    form.description(),
                    session.loginId(),
                    form.priority()
            );
            if (errorMessage != null) {
                view.showWarning(errorMessage);
                return;
            }
            IssueItem createdIssue = currentProjectIssues().stream()
                    .filter(issue -> issue.projectId() == form.project().id())
                    .filter(issue -> issue.title().equals(form.title()))
                    .filter(issue -> issue.description().equals(form.description()))
                    .filter(issue -> issue.reporter().equals(session.loginId()))
                    .reduce((first, second) -> second)
                    .orElse(null);

            if (createdIssue != null) {
                backend.addComment(createdIssue.id(), session.loginId(), form.comment());
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
        view.showAssignIssueDialog(backend.developerLoginIdsForProject(selected.projectId()), session.loginId()).ifPresent(form -> {
            backend.assignIssue(selected.id(), form.assignee(), session.loginId(), form.comment());
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
        view.showCommentDialog("수정 완료 처리", "수정을 완료했고 테스트 검증을 요청합니다.").ifPresent(comment -> {
            backend.markFixed(selected.id(), session.loginId(), comment);
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
        backend.resolveIssue(selected.id(), session.loginId(), "테스터가 수정 내용을 확인함");
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

        String errorMessage = backend.reopenIssue(selected.id(), session.loginId(), "관리자가 종료된 이슈를 재오픈함");
        if (errorMessage != null) {
            view.showWarning(errorMessage);
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
        backend.closeIssue(selected.id(), session.loginId(), "PL이 해결된 이슈를 종료 처리함");
        refreshTable();
    }

    private void addComment() {
        IssueItem selected = requireSelectedIssue();
        if (selected == null) {
            return;
        }
        view.showCommentDialog("코멘트 추가", "").ifPresent(comment -> {
            backend.addComment(selected.id(), session.loginId(), comment);
            refreshTable();
        });
    }

    private void showSelectedIssue() {
        IssueItem selected = requireSelectedIssue();
        if (selected != null) {
            view.showIssueDetail(selected);
        }
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

        List<String> recommendations = backend.recommendAssignees(selected);
        view.showRecommendationSelectDialog(recommendations).ifPresent(candidate -> {
            List<String> developers = backend.developerLoginIdsForProject(selected.projectId());
            view.showAssignIssueDialog(developers, session.loginId(), candidate).ifPresent(form -> {
                backend.assignIssue(selected.id(), form.assignee(), session.loginId(), form.comment());
                refreshTable();
            });
        });
    }

    private void showStatistics() {
        Integer projectId = session.selectedProjectId();
        String daily = backend.dailyIssueCounts(projectId).entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\n"));

        if (daily.isBlank()) {
            daily = "데이터 없음";
        }

        view.showStatistics(
                "상태별 요약\n" +
                        "NEW: " + backend.countByStatus(projectId, "NEW") + "\n" +
                        "ASSIGNED: " + backend.countByStatus(projectId, "ASSIGNED") + "\n" +
                        "FIXED: " + backend.countByStatus(projectId, "FIXED") + "\n" +
                        "RESOLVED: " + backend.countByStatus(projectId, "RESOLVED") + "\n" +
                        "CLOSED: " + backend.countByStatus(projectId, "CLOSED") + "\n\n" +
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
        return backend.issuesForRole(session.loginId(), session.role()).stream()
                .filter(issue -> selectedProjectId == null || issue.projectId() == selectedProjectId)
                .toList();
    }

    private List<ProjectItem> currentProjectItems() {
        List<ProjectItem> projects = backend.projectsForUser(session.loginId(), session.role());
        Integer selectedProjectId = session.selectedProjectId();
        if (selectedProjectId == null) {
            return projects;
        }

        return projects.stream()
                .filter(project -> project.id() == selectedProjectId)
                .toList();
    }

    private List<String> projectDeveloperLoginIds() {
        Integer selectedProjectId = session.selectedProjectId();
        if (selectedProjectId == null) {
            return backend.developerLoginIds();
        }

        return backend.usersForProject(selectedProjectId).stream()
                .filter(user -> user.role() == UserRole.DEV)
                .map(UserItem::loginId)
                .toList();
    }

    private List<String> projectTesterLoginIds() {
        Integer selectedProjectId = session.selectedProjectId();
        if (selectedProjectId == null) {
            return backend.testerLoginIds();
        }

        return backend.usersForProject(selectedProjectId).stream()
                .filter(user -> user.role() == UserRole.TESTER)
                .map(UserItem::loginId)
                .toList();
    }
}
