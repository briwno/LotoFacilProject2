import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class LoginController extends Application {

    @Override
    public void start(Stage tela) throws Exception {
        
        Parent root = FXMLLoader.load(getClass().getResource("/view/layoutLogin.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/resources/style.css").toExternalForm());
        tela.setScene(scene);
        tela.setTitle("Login");

        
        tela.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        tela.show();

        
        Button loginButton = (Button) root.lookup("#loginbutton");
        Button registerButton = (Button) root.lookup("#registerbutton");
        Button forgetButton = (Button) root.lookup("#forget");
        CheckBox remember = (CheckBox) root.lookup("#lembrar");

        TextField user = (TextField) root.lookup("#usernameField");
        PasswordField password = (PasswordField) root.lookup("#senhaField");

        loginButton.setOnAction(e -> {
            String username = user.getText();
            String senha = password.getText();

            System.out.println("Login");
            System.out.println("Username: " + username);
            System.out.println("Password: " + senha);

            if (username.equals("") && senha.equals("")) {
                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setHeaderText("Login como ADMIN efetuado com sucesso");
                tela.close();
                AdminController admin = new AdminController();
                try {
                    admin.start(new Stage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        registerButton.setOnAction(e -> {
            System.out.println("Register");
        });

        forgetButton.setOnAction(e -> {
            System.out.println("Forget");
        });

        remember.setOnAction(e -> {
            System.out.println("Remember");
        });
    }
}
