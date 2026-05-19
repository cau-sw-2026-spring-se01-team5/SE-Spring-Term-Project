package app;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        SceneManager.init(stage);
        SceneManager.showLoginView();
    }

    public static void main(String[] args) {
        launch(args);
    }
}