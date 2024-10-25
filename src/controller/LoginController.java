import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class LoginController extends Application {
    private Autenticador autenticador = new Autenticador();

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
            String role = autenticador.autenticarUsuario();
            if (role != null) {
                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setHeaderText("Login efetuado com sucesso como " + role);
                tela.close();
                if (role.equals("admin")) {
                    AdminController admin = new AdminController();
                    try {
                        admin.start(new Stage());
                        alerta.show();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else if (role.equals("apostador")) {
                    // ApostadorController apostador = new ApostadorController();
                    // apostador.start(new Stage());
                }
            } else {
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setHeaderText("Nome de usuário ou senha incorretos");
                alerta.show();
            }
        });

        registerButton.setOnAction(e -> {
            System.out.println("Register");
            RegistrarController registrar = new RegistrarController();
            try {
                registrar.start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        forgetButton.setOnAction(e -> {
            System.out.println("Forget");
            // Implementar lógica de recuperação de senha
        });

        remember.setOnAction(e -> {
            System.out.println("Remember");
            // Implementar lógica de lembrar credenciais
        });
    }

}