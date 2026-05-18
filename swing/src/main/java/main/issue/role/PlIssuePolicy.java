package main.issue.role;

import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;

import java.util.List;

public class PlIssuePolicy extends BaseIssuePolicy {

    @Override
    public boolean supports(UserRole role) {
        return role == UserRole.PL;
    }

    @Override
    public void apply(IssueActionView view) {
        configure(
                view,
                true,
                true,
                true,
                true,
                List.of(IssueStatus.CLOSED, IssueStatus.REOPENED)
        );
    }
}
