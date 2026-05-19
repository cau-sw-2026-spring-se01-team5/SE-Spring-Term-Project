package main.issue.role;

import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;

import java.util.Arrays;

/* admin계정 정책 */
public class AdminIssuePolicy extends BaseIssuePolicy {

    @Override
    public boolean supports(UserRole role) {
        return role == UserRole.ADMIN;
    }

    @Override
    public void apply(IssueActionView view) {
        configure(
                view,
                true,
                true,
                true,
                true,
                Arrays.asList(IssueStatus.values())
        );
    }
}
