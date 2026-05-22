package app;

import auth.v1.Auth;
import issue.v1.Issue;
import project.v1.Project;
import statistics.v1.Statistics;
import user.v1.RoleResolver;
import user.v1.User;

// 필요한 인터페이스를 record로 정의
public record AppServices(
        Auth auth,
        Project project,
        User user,
        RoleResolver roleResolver,
        Issue issue,
        Statistics statistics
) {
}
