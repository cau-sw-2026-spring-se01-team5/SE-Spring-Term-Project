package app;

import auth.v1.Auth;
import issue.v1.Issue;
import project.v1.Project;
import statistics.v1.Statistics;
import user.v1.RoleResolver;
import user.v1.User;

// JavaFX가 직접 사용하는 service 묶음.
public record JavaFxServices(
        Auth auth,
        Project project,
        User user,
        RoleResolver roleResolver,
        Issue issue,
        Statistics statistics
) {
}
