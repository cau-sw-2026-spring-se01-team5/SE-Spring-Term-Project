package main.issue.role;

import enums.issue.v1.IssueStatus;

import java.util.List;

public interface IssueActionView {

    void setAssignSectionVisible(boolean visible);

    void setRecommendButtonVisible(boolean visible);

    void setStatusSectionVisible(boolean visible);

    void setDeleteButtonVisible(boolean visible);

    void setCommentSectionVisible(boolean visible);

    void setStatusOptions(List<IssueStatus> statuses);
}
