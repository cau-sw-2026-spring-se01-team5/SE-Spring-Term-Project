package app;

import javafx.application.Application;
import javafx.stage.Stage;
import session.UserSession;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        // 화면 생성과 service wiring 선택만 담당한다.
        AppFrame frame = new AppFrame(stage);
        UserSession session = new UserSession();
        try {
            JavaFxWiring wiring = new CoreJavaFxWiring();
            // JavaFxWiring wiring = new MockJavaFxWiring();
            AppController controller = new AppController(frame, session, wiring.wire());
            controller.start();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to initialize JavaFX application.", exception);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
