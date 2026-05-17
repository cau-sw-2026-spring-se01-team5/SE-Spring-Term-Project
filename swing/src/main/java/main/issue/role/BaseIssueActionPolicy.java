package main.issue.role;

import enums.issue.v1.IssueStatus;

import java.util.List;

abstract class BaseIssueActionPolicy implements IssueActionPolicy {

    protected void configure(
            IssueActionView view,
            boolean assignVisible,
            boolean recommendVisible,
            boolean statusVisible,
            boolean deleteVisible,
            List<IssueStatus> statusOptions
    ) {
        view.setAssignSectionVisible(assignVisible);
        view.setRecommendButtonVisible(recommendVisible);
        view.setStatusSectionVisible(statusVisible);
        view.setDeleteButtonVisible(deleteVisible);
        view.setCommentSectionVisible(true);
        view.setStatusOptions(statusOptions);
    }
}
