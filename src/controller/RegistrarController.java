import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Apostador;
import model.Apostador.Endereco;

public class RegistrarController extends Application {

    Autenticador autenticador = new Autenticador();

    @Override
    public void start(Stage tela) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/layoutRegistrar.fxml"));
        Scene scene = new Scene(root);
        tela.setScene(scene);
        tela.setTitle("Registro de Usuário");
        tela.show();

        TextField nomeField = (TextField) root.lookup("#nomeField");
        TextField emailField = (TextField) root.lookup("#emailField");
        TextField cpfField = (TextField) root.lookup("#cpfField");
        TextField telefoneField = (TextField) root.lookup("#telefoneField");
        TextField ufField = (TextField) root.lookup("#ufField");
        TextField cidadeField = (TextField) root.lookup("#cidadeField");
        TextField cepField = (TextField) root.lookup("#cepField");
        TextField ruaField = (TextField) root.lookup("#ruaField");
        TextField bairroField = (TextField) root.lookup("#bairroField");
        TextField userField = (TextField) root.lookup("#userField");
        TextField senhaField = (TextField) root.lookup("#senhaField");
        ComboBox<String> generoField = (ComboBox<String>) root.lookup("#generoField");
        DatePicker dataField = (DatePicker) root.lookup("#dataField");
        CheckBox termosCheck = (CheckBox) root.lookup("#termosCheck");
        Button registrarButton = (Button) root.lookup("#registrarButton");
        Button verificarCEPButton = (Button) root.lookup("#verificarCEPButton");

        generoField.getItems().addAll("Masculino", "Feminino", "Outro");

        verificarCEPButton.setOnAction(e -> {
            String logradouro = "";
            String uf = "";
            String cidade = "";
            String bairro = "";
            String cep = cepField.getText();
            try {
                URL url = new URL("https://viacep.com.br/ws/" + cep + "/json/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                if (connection.getResponseCode() == 200) {
                    String json = new String(connection.getInputStream().readAllBytes());
                    JSONObject obj = new JSONObject(json);
                    uf = obj.getString("uf");
                    cidade = obj.getString("localidade");
                    bairro = obj.getString("bairro");
                    logradouro = obj.getString("logradouro");

                    ruaField.setText(logradouro);
                    ufField.setText(uf);
                    cidadeField.setText(cidade);
                    bairroField.setText(bairro);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        registrarButton.setOnAction(event -> {
            if (termosCheck.isSelected()) {
                Apostador apostador = new Apostador();
                apostador.setNome(nomeField.getText());
                apostador.setEmail(emailField.getText());
                apostador.setCpf(cpfField.getText());
                apostador.setTelefone(telefoneField.getText());
                apostador.setGenero(generoField.getValue());
                apostador.setUser(userField.getText());
                apostador.setSenha(senhaField.getText());
                apostador.setDataNascimento(dataField.getValue().toString());

                Endereco endereco = apostador.new Endereco();
                endereco.setRua(ruaField.getText());
                endereco.setBairro(bairroField.getText());
                endereco.setCidade(cidadeField.getText());
                endereco.setUF(ufField.getText());
                endereco.setCep(Integer.parseInt(cepField.getText()));

                apostador.setEndereco(endereco);

                autenticador.registrarUsuario(apostador);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText("Usuário registrado com sucesso");
                alert.setContentText("Seu usuário foi registrado com sucesso, você já pode fazer login");
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Termos de uso não aceitos");
                alert.setContentText("Você deve aceitar os termos de uso para continuar");
                alert.show();
            }
        });
    }

}