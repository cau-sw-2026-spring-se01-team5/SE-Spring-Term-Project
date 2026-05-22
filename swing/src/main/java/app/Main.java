package app;

import session.UserSession;

import javax.swing.*;

/* App 진입점. */
/* 필요한 객체들 생성해서 의존성 조립 역할 */
public class Main {

    public static void main(String[] args) {
        // Swing UI 관련 작업을 Event Dispatch Thread에서 실행하도록 함
        // Swing 로직은 전용 스레드에서 처리하는 것이 안전.
        // 따라서 SwingUtilities.invokeLater안에서 UI를 생성함
        SwingUtilities.invokeLater(() -> {
            try {
                // 실행 시 갈아끼울 wiring 선택 -> mock or core
                AppWiring wiring = new CoreAppWiring();
                // AppWiring wiring = new MockAppWiring();

                AppServices services = wiring.wire();

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
                        services.roleResolver(),
                        services.auth(),
                        services.project(),
                        services.user(),
                        services.issue(),
                        services.statistics()
                );

                controller.start();

                // 창 띄우기
                frame.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "앱 시작 실패: " + e.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
            }
        });
    }
}
