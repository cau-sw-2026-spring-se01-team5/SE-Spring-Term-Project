package app;

import auth.v1.Auth;
import controller.auth.v1.AuthImpl;
import controller.issue.v1.IssueImpl;
import controller.project.v1.ProjectImpl;
import controller.user.v1.RoleResolverImpl;
import controller.user.v1.UserImpl;
import domain.Comment;
import issue.v1.Issue;
import mock.*;
import project.v1.Project;
import repository.CommentRepository;
import repository.IssueRepository;
import repository.ProjectRepository;
import repository.UserRepository;
import repository.sqlite.SqliteCommentRepository;
import repository.sqlite.SqliteIssueRepository;
import repository.sqlite.SqliteProjectRepository;
import repository.sqlite.SqliteUserRepository;
import session.UserSession;
import user.v1.RoleResolver;
import user.v1.User;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/* App 진입점. */
/* 필요한 객체들 생성해서 의존성 조립 역할 */
public class MainWithCore {

    public static void main(String[] args) {
        // Swing UI 관련 작업을 Event Dispatch Thread에서 실행하도록 함
        // Swing 로직은 전용 스레드에서 처리하는 것이 안전.
        // 따라서 SwingUtilities.invokeLater안에서 UI를 생성함
        SwingUtilities.invokeLater(() -> {
            Connection connection;
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:test.db?foreign_keys=true");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            UserRepository userRepository = new SqliteUserRepository(connection);
            ProjectRepository projectRepository = new SqliteProjectRepository(connection);
            IssueRepository issueRepository = new SqliteIssueRepository(connection);
            CommentRepository commentRepository = new SqliteCommentRepository(connection);

            Auth auth = new AuthImpl(userRepository);
            Project project = new ProjectImpl(userRepository, projectRepository);
            User user = new UserImpl(userRepository);
            Issue issue = new IssueImpl(userRepository, issueRepository, commentRepository);

            // 로그인 후 userId 기준으로 사용자의 loginId와 role 찾아주는 객체
            RoleResolver roleResolver = new RoleResolverImpl(userRepository);

            // 현재 로그인한 사용자 상태를 저장하는 세션 객체
            // 로그인 이후 화면과 controller에게 현재 사용자 정보를 공유하기 위해 필요
            UserSession session = new UserSession();

            // 전체 Swing 창 생성
            AppFrame frame = new AppFrame();

            // App 전체 화면 전환 조율
            // 로그인 화면 -> 성공 시 프로젝트 선택 화면 -> 선택 후 메인 화면 -> 로그아웃 시 로그인 화면 등등
            // Main이 직접 화면 전환 처리하지 않고 AppController에게 역할 위임
            AppController controller = new AppController(
                    frame,
                    session,
                    roleResolver,
                    auth,
                    project,
                    user,
                    issue
            );

            controller.start();

            // 창 띄우기
            frame.setVisible(true);
        });
    }
}
