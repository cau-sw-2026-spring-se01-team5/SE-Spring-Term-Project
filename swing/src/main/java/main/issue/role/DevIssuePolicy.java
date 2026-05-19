package main.issue.role;

import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;

import java.util.List;

/* dev 정책 */
public class DevIssuePolicy extends BaseIssuePolicy {

    @Override
    public boolean supports(UserRole role) {
        return role == UserRole.DEV;
    }

    @Override
    public void apply(IssueActionView view) {
        configure(
                view,
                false,
                false,
                true,
                false,
                List.of(IssueStatus.FIXED)
        );
    }
}
