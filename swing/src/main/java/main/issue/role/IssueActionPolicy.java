package main.issue.role;

import enums.user.v1.UserRole;

public interface IssueActionPolicy {

    boolean supports(UserRole role);

    void apply(IssueActionView view);
}
