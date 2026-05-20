package app;

import javafx.application.Application;
import javafx.stage.Stage;
import session.UserSession;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        AppFrame frame = new AppFrame(stage);
        UserSession session = new UserSession();
        AppController controller = new AppController(frame, session, BackendProvider.backend());
        controller.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
