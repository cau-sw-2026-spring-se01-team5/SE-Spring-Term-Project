package app;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*
 * JavaFX Stage를 감싸는 화면 출력 담당 클래스이다.
 *
 * Swing의 AppFrame처럼 실제 창 크기, 제목, 중앙 배치 정책을 한 곳에서 관리한다.
 * 각 화면이 Stage를 직접 다루지 않게 하여 화면 전환 정책이 여러 파일에 흩어지는 것을 막는다.
 */
public class AppFrame {

    private static final int APP_WIDTH = 1100;
    private static final int APP_HEIGHT = 720;

    private final Stage stage;

    public AppFrame(Stage stage) {
        this.stage = stage;
        this.stage.setMinWidth(APP_WIDTH);
        this.stage.setMinHeight(APP_HEIGHT);
    }

    public void showLogin(Parent root) {
        setScreen("이슈 관리 시스템 - 로그인", root);
    }

    public void showMain(Parent root) {
        setScreen("이슈 관리 시스템", root);
    }

    private void setScreen(String title, Parent root) {
        stage.setTitle(title);
        stage.setScene(new Scene(root, APP_WIDTH, APP_HEIGHT));
        stage.setWidth(APP_WIDTH);
        stage.setHeight(APP_HEIGHT);
        stage.show();
        stage.centerOnScreen();
    }
}
