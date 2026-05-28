package app;

import auth.v1.Auth;
import issue.v1.Issue;
import mock.MockAuth;
import mock.MockDatabase;
import mock.MockIssue;
import mock.MockProject;
import mock.MockRoleResolver;
import mock.MockStatistics;
import mock.MockUser;
import project.v1.Project;
import statistics.v1.Statistics;
import user.v1.RoleResolver;
import user.v1.User;

// Swing과 같은 공용 mock 구현체를 연결하는 JavaFX용 wiring.
public class MockJavaFxWiring implements JavaFxWiring {

    @Override
    public JavaFxServices wire() {
        // 공용 mock-support 모듈의 mock 세트를 그대로 조립한다.
        MockDatabase database = new MockDatabase();

        Auth auth = new MockAuth(database);
        Project project = new MockProject(database);
        User user = new MockUser(database);
        RoleResolver roleResolver = new MockRoleResolver(database);
        Issue issue = new MockIssue(database);
        Statistics statistics = new MockStatistics(database);

        return new JavaFxServices(auth, project, user, roleResolver, issue, statistics);
    }
}
