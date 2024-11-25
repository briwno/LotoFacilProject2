import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.json.JSONArray;
import org.json.JSONObject;

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
        tela.setResizable(false);


        Button loginButton = (Button) root.lookup("#loginbutton");
        Button registerButton = (Button) root.lookup("#registerbutton");
        Button forgetButton = (Button) root.lookup("#forget");
        Button logarAdmin = (Button) root.lookup("#entrarAdmin");
        CheckBox remember = (CheckBox) root.lookup("#lembrar");
        TextField user = (TextField) root.lookup("#usernameField");
        PasswordField password = (PasswordField) root.lookup("#senhaField");

        logarAdmin.setOnAction(e -> {
            System.out.println("Logar como admin");
            AdminController admin = new AdminController();
            try {
                admin.start(new Stage());
                tela.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        loginButton.setOnAction(e -> {
            String username = user.getText();
            String senha = password.getText();
        
            StringBuilder sb = autenticador.autenticarUsuario(username, senha);
            String usuarioLogado = sb.substring(0, sb.indexOf(";"));
            String idLogado = sb.substring(sb.indexOf(";") + 1);

            
        
            // abrir tela de apostador daquele usuario
            if (usuarioLogado != null) {
                ApostadorController apostador = new ApostadorController();

                apostador.setUsuarioLogado(usuarioLogado);
                apostador.setIdLogado(idLogado);

                
                try {
                    apostador.start(new Stage());
                    tela.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setHeaderText("Usuário ou senha inválidos");
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
            String emailRec = dialog.showAndWait().orElse(null);

            if (emailRec != null && autenticador.verificarEmailCadastrado(emailRec)) {
                String codigoEmail = autenticador.gerarCodigoRecuperacao();

                try {
                    Message msg = new MimeMessage(session);
                    msg.setFrom(new InternetAddress("esqueletodoclash@gmail.com"));
                    msg.addRecipient(Message.RecipientType.TO, new InternetAddress(emailRec));
                    msg.setSubject("Recuperação de senha");
                    msg.setText("Seu código de recuperação é: " + codigoEmail);

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
                if (dialog2.showAndWait().orElse("").equals(codigoEmail)) {
                    TextInputDialog dialog3 = new TextInputDialog();
                    dialog3.setTitle("Recuperação de senha");
                    dialog3.setHeaderText("Digite a nova senha");
                    dialog3.setContentText("Nova senha:");
                    String novaSenha = dialog3.showAndWait().orElse("");

                    try {
                        String content = new String(Files.readAllBytes(Paths.get("src/db/users.json")),
                                StandardCharsets.UTF_8);
                        JSONArray usersArray = new JSONArray(content);

                        boolean userFound = false;
                        for (int i = 0; i < usersArray.length(); i++) {
                            JSONObject usuario = usersArray.getJSONObject(i);
                            if (usuario.getString("email").equals(emailRec)) {
                                usuario.put("senha", novaSenha);
                                userFound = true;
                                break;
                            }
                        }

                        if (userFound) {

                            Files.write(Paths.get("src/db/users.json"),
                                    usersArray.toString(4).getBytes(StandardCharsets.UTF_8));
                            System.out.println("Senha alterada com sucesso");
                        } else {
                            System.out.println("Usuário não encontrado");
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        System.out
                                .println("Erro ao ler ou escrever no arquivo users.json: " + ioException.getMessage());
                    }
                } else {
                    Alert alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.setHeaderText("Código de recuperação inválido");
                    alerta.show();
                }
            } else {
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setHeaderText("Email não cadastrado");
                alerta.show();
            }
        });

        remember.setOnAction(e -> {
            System.out.println("Remember");
            // Implementar lógica de lembrar credenciais
        });
    }
}
