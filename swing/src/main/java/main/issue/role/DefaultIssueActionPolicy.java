package main.issue.role;

import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;

import java.util.List;

public class DefaultIssueActionPolicy extends BaseIssueActionPolicy {

    @Override
    public boolean supports(UserRole role) {
        return role == null;
    }

    @Override
    public void apply(IssueActionView view) {
        configure(
                view,
                false,
                false,
                false,
                false,
                List.<IssueStatus>of()
        );
    }
}
