package app;

import auth.v1.Auth;
import issue.v1.Issue;
import mock.MockAuth;
import mock.MockDatabase;
import mock.MockIssue;
import mock.MockProject;
import mock.MockRoleResolver;
import mock.MockUser;
import project.v1.Project;
import user.v1.RoleResolver;
import user.v1.User;

public class MockAppWiring implements AppWiring {

    @Override
    public AppServices wire() {
        MockDatabase database = new MockDatabase();

        Auth auth = new MockAuth(database);
        Project project = new MockProject(database);
        User user = new MockUser(database);
        RoleResolver roleResolver = new MockRoleResolver(database);
        Issue issue = new MockIssue(database);

        return new AppServices(auth, project, user, roleResolver, issue);
    }
}
