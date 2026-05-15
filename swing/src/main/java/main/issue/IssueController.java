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
import main.header.HeaderController;
import session.UserSession;

public class IssueController {

    private final IssueView view;
    private final Issue issueService;
    private final UserSession session;
    private final HeaderController headerController;

    public IssueController(
            IssueView view,
            Issue issueService,
            UserSession session,
            HeaderController headerController
    ) {
        this.view = view;
        this.issueService = issueService;
        this.session = session;
        this.headerController = headerController;

        bind();
    }

    public void applyRole() {
        view.applyRole(session.role());
    }

    private void bind() {
        view.onSearchIssues(this::searchIssues);
        view.onRegisterIssue(this::registerIssue);
        view.onAssignIssue(this::assignIssue);
        view.onChangeIssueStatus(this::changeIssueStatus);
        view.onAddIssueComment(this::addIssueComment);
        view.onShowIssueDetail(this::showIssueDetail);
        view.onRecommendAssignee(this::recommendAssignee);
        view.onDeleteIssue(this::deleteIssue);
    }

    public void searchIssues() {
        Integer projectId = requireProjectId();

        if (projectId == null) {
            return;
        }

        var output = issueService.getIssueList(
                new GetIssueListInput(
                        session.userId(),
                        projectId,
                        view.getFilterAssigneeUserId(),
                        view.getFilterReporterUserId(),
                        view.getFilterFixerUserId(),
                        view.getFilterStatus(),
                        view.getFilterPriority(),
                        view.getFilterKeyword()
                )
        );

        if (!output.success()) {
            view.showMessage(output.message());
            return;
        }

        view.setIssues(output.issues());
    }

    private void registerIssue() {
        Integer projectId = requireProjectId();

        if (projectId == null) {
            return;
        }

        var output = issueService.registerIssue(
                new RegisterIssueInput(
                        projectId,
                        view.getIssueTitleInput(),
                        view.getIssueDescriptionInput(),
                        view.getIssuePriorityInput(),
                        session.userId()
                )
        );

        view.showMessage(output.message());

        if (output.success()) {
            searchIssues();
        }
    }

    private void assignIssue() {
        Integer issueId = requireIssueId();

        if (issueId == null) {
            return;
        }

        var output = issueService.assignIssue(
                new AssignIssueInput(
                        issueId,
                        session.userId(),
                        view.getAssigneeUserIdInput(),
                        view.getIssueCommentInput()
                )
        );

        view.showMessage(output.message());

        if (output.success()) {
            searchIssues();
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
            searchIssues();
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
            showIssueDetail();
        }
    }

    private void showIssueDetail() {
        Integer issueId = requireIssueId();

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
            searchIssues();
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

    private Integer requireIssueId() {
        Integer issueId = view.getSelectedIssueId();

        if (issueId == null) {
            view.showMessage("이슈를 선택하세요.");
            return null;
        }

        return issueId;
    }
}