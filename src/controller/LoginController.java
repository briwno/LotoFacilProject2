import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class LoginController extends Application {

    private static final String USERS_FILE = "src/db/users.txt";

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

            String role = autenticarUsuario(username, senha);

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

    private String autenticarUsuario(String username, String senha) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados[0].equals(username) && dados[1].equals(senha)) {
                    return dados[2]; //retorna qual a role do usuário
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String registrarUsuario(String username, String senha, String role) {
        // Implementar lógica de registro de usuário
        return null;
    }
}
