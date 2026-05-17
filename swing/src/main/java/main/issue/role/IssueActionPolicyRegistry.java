package main.issue.role;

import enums.user.v1.UserRole;

import java.util.List;

public class IssueActionPolicyRegistry {

    private final List<IssueActionPolicy> policies;
    private final IssueActionPolicy fallbackPolicy;

    public IssueActionPolicyRegistry() {
        this.policies = List.of(
                new AdminIssueActionPolicy(),
                new PlIssueActionPolicy(),
                new DevIssueActionPolicy(),
                new TesterIssueActionPolicy()
        );
        this.fallbackPolicy = new DefaultIssueActionPolicy();
    }

    public IssueActionPolicy resolve(UserRole role) {
        for (IssueActionPolicy policy : policies) {
            if (policy.supports(role)) {
                return policy;
            }
        }
        return fallbackPolicy;
    }
}
