package app;

import javafx.application.Application;
import javafx.stage.Stage;
import session.UserSession;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        AppFrame frame = new AppFrame(stage);
        UserSession session = new UserSession();
        AppController controller = new AppController(frame, session, JavaFxServicesFactory.create());
        controller.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
