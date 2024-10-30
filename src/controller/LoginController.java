import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.Apostador;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("esqueletodoclash@gmail.com", "limt arrw cozz buck");
                }
            });

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Recuperação de senha");
            dialog.setHeaderText("Digite seu email para recuperação de senha");
            dialog.setContentText("Email:");
            String emailRec = dialog.showAndWait().get();
            String codigoEmail = autenticador.gerarCodigoRecuperacao();

            dialog.showAndWait().ifPresent(email -> {
                try {
                    Message msg = new MimeMessage(session);
                    msg.setFrom(new InternetAddress("esqueletodoclash@gmail.com"));
                    msg.addRecipient(Message.RecipientType.TO,
                            new InternetAddress(emailRec, "Mr. User"));
                    msg.setSubject("Recuperação de senha");

                    msg.setText("Seu codigo de recuperação é: " + codigoEmail);

                    Transport.send(msg);
                    System.out.println("Email enviado com sucesso");
                } catch (AddressException e1) {
                    e1.printStackTrace();
                    System.out.println("Erro no endereço de email: " + e1.getMessage());
                } catch (MessagingException e2) {
                    e2.printStackTrace();
                    System.out.println("Erro ao enviar o email: " + e2.getMessage());
                } catch (Exception e3) {
                    e3.printStackTrace();
                    System.out.println("Erro inesperado: " + e3.getMessage());
                }

                TextInputDialog dialog2 = new TextInputDialog();
                dialog2.setTitle("Recuperação de senha");
                dialog2.setHeaderText("Digite o código de recuperação enviado para o email");
                dialog2.setContentText("Código:");
                if (dialog2.showAndWait().get().equals(codigoEmail)) {
                    TextInputDialog dialog3 = new TextInputDialog();
                    dialog3.setTitle("Recuperação de senha");
                    dialog3.setHeaderText("Digite a nova senha");
                    dialog3.setContentText("Nova senha:");
                    String novaSenha = dialog3.showAndWait().get();
                    Apostador apostador = new Apostador();
                    apostador.setSenha(novaSenha);

                } else {
                    Alert alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.setHeaderText("Código de recuperação inválido");
                    alerta.show();
                }
            });

        });

        remember.setOnAction(e -> {
            System.out.println("Remember");
            // Implementar lógica de lembrar credenciais
        });
    }

}