import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public void start(Stage primaryStage) throws Exception {

        LoginController telaLogin = new LoginController();
        telaLogin.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
