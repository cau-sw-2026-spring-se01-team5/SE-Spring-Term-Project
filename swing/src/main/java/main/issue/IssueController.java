package main.issue;

import issue.dto.addIssueComment.v1.AddIssueCommentInput;
import issue.dto.assignIssue.v1.AssignIssueInput;
import issue.dto.changeIssueStatus.v1.ChangeIssueStatusInput;
import issue.dto.deleteIssue.v1.DeleteIssueInput;
import issue.dto.getIssueDetail.v1.GetIssueDetailInput;
import issue.dto.getIssueList.v1.GetIssueListInput;
import issue.dto.recommendAssignee.v1.RecommendAssigneeInput;
import issue.dto.registerIssue.v1.RegisterIssueInput;
import issue.v1.Issue;
import enums.user.v1.UserRole;
import session.UserSession;
import user.dto.getProjectUserList.v1.GetProjectUserListInput;
import user.v1.User;

import java.util.List;

public class IssueController {

    private final IssueView view;
    private final Issue issueService;
    private final User userService;
    private final UserSession session;

    public IssueController(
            IssueView view,
            Issue issueService,
            User userService,
            UserSession session
    ) {
        this.view = view;
        this.issueService = issueService;
        this.userService = userService;
        this.session = session;

        bind();
    }

    public void applyRole() {
        view.applyRole(session.role());
        loadAssignableDevelopers();
    }

    private void bind() {
        view.onSearchIssues(this::searchIssues);
        view.onRegisterIssue(this::registerIssue);
        view.onAssignIssue(this::assignIssue);
        view.onChangeIssueStatus(this::changeIssueStatus);
        view.onAddIssueComment(this::addIssueComment);
        view.onShowIssueDetail(this::showSelectedIssueDetail);
        view.onRecommendAssignee(this::recommendAssignee);
        view.onDeleteIssue(this::deleteIssue);
    }

    public void searchIssues() {
        Integer projectId = requireProjectId();

        if (projectId == null) {
            return;
        }

        IssueView.SearchCondition condition;
        try {
            condition = view.showSearchDialog();
        } catch (IllegalArgumentException e) {
            view.showMessage(e.getMessage());
            return;
        }

        if (condition == null) {
            return;
        }

        var output = issueService.getIssueList(
                new GetIssueListInput(
                        projectId,
                        session.userId(),
                        condition.assigneeUserId(),
                        condition.reporterUserId(),
                        condition.fixerUserId(),
                        condition.status(),
                        condition.priority(),
                        condition.keyword()
                )
        );

        if (!output.success()) {
            view.showMessage(output.message());
            return;
        }

        Integer selectedIssueId = view.showSearchResultAndSelectIssue(output.issues());
        if (selectedIssueId != null) {
            showIssueDetail(selectedIssueId);
        }
    }

    public void loadAllIssues() {
        Integer projectId = requireProjectId();

        if (projectId == null) {
            return;
        }

        var output = issueService.getIssueList(
                new GetIssueListInput(
                        projectId,
                        session.userId(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        );

        if (!output.success()) {
            view.showMessage(output.message());
            return;
        }

        view.setIssues(output.issues());
        loadAssignableDevelopers();
    }

    private void registerIssue() {
        Integer projectId = requireProjectId();

        if (projectId == null) {
            return;
        }

        IssueView.CreateIssueForm form = view.showCreateIssueDialog();
        if (form == null) {
            return;
        }

        var output = issueService.registerIssue(
                new RegisterIssueInput(
                        projectId,
                        form.title(),
                        form.description(),
                        form.priority(),
                        session.userId()
                )
        );

        view.showMessage(output.message());

        if (output.success()) {
            loadAllIssues();
        }
    }

    private void assignIssue() {
        Integer issueId = requireIssueId();

        if (issueId == null) {
            return;
        }

        Integer assigneeUserId = view.getAssigneeUserIdInput();

        if (assigneeUserId == null) {
            view.showMessage("배정할 DEV를 선택하세요.");
            return;
        }

        var output = issueService.assignIssue(
                new AssignIssueInput(
                        issueId,
                        session.userId(),
                        assigneeUserId,
                        null
                )
        );

        view.showMessage(output.message());

        if (output.success()) {
            loadAllIssues();
            showIssueDetail(issueId);
        }
    }

    private void changeIssueStatus() {
        Integer issueId = requireIssueId();

        if (issueId == null) {
            return;
        }

        var output = issueService.changeIssueStatus(
                new ChangeIssueStatusInput(
                        issueId,
                        session.userId(),
                        view.getTargetIssueStatusInput()
                )
        );

        view.showMessage(output.message());

        if (output.success()) {
            loadAllIssues();
            showIssueDetail(issueId);
        }
    }

    private void addIssueComment() {
        Integer issueId = requireIssueId();

        if (issueId == null) {
            return;
        }

        var output = issueService.addIssueComment(
                new AddIssueCommentInput(
                        issueId,
                        session.userId(),
                        view.getIssueCommentInput()
                )
        );

        view.showMessage(output.message());

        if (output.success()) {
            showIssueDetail(issueId);
        }
    }

    private void showSelectedIssueDetail() {
        Integer issueId = view.getSelectedIssueId();

        if (issueId == null) {
            view.showMessage("이슈를 선택하세요.");
            return;
        }

        showIssueDetail(issueId);
    }

    private void showIssueDetail(Integer issueId) {
        if (issueId == null) {
            return;
        }

        var output = issueService.getIssueDetail(
                new GetIssueDetailInput(issueId)
        );

        view.showIssueDetail(output);
    }

    private void recommendAssignee() {
        Integer projectId = requireProjectId();
        Integer issueId = requireIssueId();

        if (projectId == null || issueId == null) {
            return;
        }

        var output = issueService.recommendAssignees(
                new RecommendAssigneeInput(
                        issueId,
                        projectId
                )
        );

        view.showRecommendations(output);
    }

    private void deleteIssue() {
        Integer issueId = requireIssueId();

        if (issueId == null) {
            return;
        }

        var output = issueService.deleteIssue(
                new DeleteIssueInput(
                        session.userId(),
                        issueId
                )
        );

        view.showMessage(output.message());

        if (output.success()) {
            loadAllIssues();
            showIssueDetail(issueId);
        }
    }

    private Integer requireProjectId() {
        Integer projectId = session.selectedProjectId();

        if (projectId == null) {
            view.showMessage("프로젝트를 선택하세요.");
            return null;
        }

        return projectId;
    }

    private Integer requireIssueId() {
        Integer issueId = view.getActiveDetailIssueId();

        if (issueId != null) {
            return issueId;
        }

        issueId = view.getSelectedIssueId();

        if (issueId == null) {
            view.showMessage("이슈를 선택하세요.");
            return null;
        }

        return issueId;
    }

    private void loadAssignableDevelopers() {
        Integer projectId = session.selectedProjectId();
        if (projectId == null) {
            return;
        }

        var output = userService.getProjectUserList(new GetProjectUserListInput(projectId));
        if (!output.success()) {
            return;
        }

        List<IssueView.AssigneeCandidate> candidates = output.userList()
                .stream()
                .filter(user -> user.role() == UserRole.DEV)
                .map(user -> new IssueView.AssigneeCandidate(user.userId(), user.loginId()))
                .toList();

        List<IssueView.ProjectUserOption> projectUsers = output.userList()
                .stream()
                .map(user -> new IssueView.ProjectUserOption(
                        user.userId(),
                        user.loginId(),
                        user.role()
                ))
                .toList();

        view.setAssigneeCandidates(candidates);
        view.setProjectUsers(projectUsers);
    }
}
