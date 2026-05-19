package app;

import auth.v1.Auth;
import issue.v1.Issue;
import mock.*;
import project.v1.Project;
import session.UserSession;
import user.v1.User;

import javax.swing.*;

/* App 진입점. */
/* 필요한 객체들 생성해서 의존성 조립 역할 */
public class Main {

    public static void main(String[] args) {
        // Swing UI 관련 작업을 Event Dispatch Thread에서 실행하도록 함
        // Swing 로직은 전용 스레드에서 처리하는 것이 안전.
        // 따라서 SwingUtilities.invokeLater안에서 UI를 생성함
        SwingUtilities.invokeLater(() -> {
            // 테스트용 DB
            MockDatabase database = new MockDatabase();

            // 테스트용 Mock 구현체를 활용.
            // 추상화된 인터페이스에 의존하도록 함.
            Auth auth = new MockAuth(database);
            Project project = new MockProject(database);
            User user = new MockUser(database);
            Issue issue = new MockIssue(database);

            // 로그인 후 userId 기준으로 사용자의 loginId와 role 찾아주는 객체
            MockRoleResolver roleResolver = new MockRoleResolver(database);

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
