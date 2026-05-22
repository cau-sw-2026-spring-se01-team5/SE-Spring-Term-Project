package app;

import backend.BackendFactory;
import javafx.application.Application;
import javafx.stage.Stage;
import session.UserSession;

/*
 * JavaFX 애플리케이션의 실행 시작점이다.
 *
 * 이 클래스는 창(AppFrame), 로그인 세션(UserSession), backend 구현체를 만들고
 * AppController에 넘겨준다. 각 화면에서 backend를 직접 생성하지 않게 해서,
 * mock 방식과 실제 backend 연동 방식을 한 지점에서 바꿀 수 있도록 했다.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        /*
         * AppFrame은 JavaFX Stage를 감싸는 창 관리 객체이다.
         * 실제 화면 전환은 AppController가 지시하고, AppFrame은 Scene 교체만 담당한다.
         */
        AppFrame frame = new AppFrame(stage);

        /*
         * UserSession은 현재 로그인한 사용자 정보를 저장한다.
         * 여러 화면에 loginId와 role을 계속 넘기기보다 세션 객체 하나를 공유한다.
         */
        UserSession session = new UserSession();

        /*
         * BackendFactory가 실제 backend와 mock backend 중 사용할 구현체를 선택한다.
         * 기본값은 실제 core backend이고, 필요하면 mock 방식으로 쉽게 되돌릴 수 있다.
         */
        AppController controller = new AppController(frame, session, BackendFactory.create());
        controller.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
