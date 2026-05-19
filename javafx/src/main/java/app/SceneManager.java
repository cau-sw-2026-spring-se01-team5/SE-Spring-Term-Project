package app;

import enums.user.v1.UserRole;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.dashboard.DashboardView;
import view.issue.IssueListView;
import view.login.LoginView;
import view.project.ProjectView;

/*
 * JavaFX 화면 전환을 중앙에서 관리하는 클래스이다.
 *
 * 설계 의도:
 * - 각 View가 Stage를 직접 제어하면 화면 전환 방식이 여러 파일에 흩어진다.
 * - 창 크기, 중앙 배치, 로그인 상태 유지 같은 공통 정책은 한 곳에서 처리하는 것이 유지보수에 유리하다.
 * - 그래서 화면 클래스는 자신의 UI 구성에 집중하고, 화면 이동 책임은 SceneManager가 맡도록 분리했다.
 */
public final class SceneManager {

    /*
     * 모든 페이지에 같은 창 크기를 적용한다.
     * 화면마다 크기가 바뀌면 사용자가 페이지 이동 때마다 어색함을 느끼므로 UI 일관성을 위해 고정했다.
     */
    private static final int APP_WIDTH = 1100;
    private static final int APP_HEIGHT = 720;

    private static Stage stage;
    private static UserRole currentRole;
    private static String currentLoginId;

    private SceneManager() {
    }

    public static void init(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("이슈 관리 시스템");
        stage.setMinWidth(APP_WIDTH);
        stage.setMinHeight(APP_HEIGHT);
    }

    /*
     * 로그인 성공 시 현재 사용자 정보를 보관한다.
     * 이후 각 화면은 이 값을 받아 역할별 버튼과 목록 범위를 다르게 구성한다.
     */
    public static void login(String loginId, UserRole role) {
        currentLoginId = loginId;
        currentRole = role;
        showDashboardView();
    }

    public static void logout() {
        currentLoginId = null;
        currentRole = null;
        showLoginView();
    }

    public static void showLoginView() {
        showScene(new LoginView());
    }

    public static void showDashboardView() {
        requireLogin();
        showScene(new DashboardView(currentLoginId, currentRole));
    }

    public static void showIssueListView() {
        requireLogin();
        showScene(new IssueListView(currentLoginId, currentRole));
    }

    public static void showProjectView() {
        requireLogin();
        showScene(new ProjectView(currentLoginId, currentRole));
    }

    /*
     * 모든 화면 전환에서 공통으로 사용하는 처리이다.
     * 새 Scene을 만든 뒤 같은 크기를 적용하고, 항상 모니터 중앙에 배치한다.
     */
    private static void showScene(Parent root) {
        Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
        stage.setScene(scene);
        stage.setWidth(APP_WIDTH);
        stage.setHeight(APP_HEIGHT);
        stage.show();
        stage.centerOnScreen();
    }

    /*
     * 로그인하지 않은 상태에서 내부 화면에 접근하는 것을 막는다.
     * UI 흐름상 내부 화면은 항상 로그인 사용자와 역할 정보를 필요로 한다.
     */
    private static void requireLogin() {
        if (currentRole == null) {
            showLoginView();
            throw new IllegalStateException("로그인이 필요한 화면입니다.");
        }
    }
}
