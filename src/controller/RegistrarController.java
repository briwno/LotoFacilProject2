// Importa as classes necessárias para manipulação de JSON, conexão HTTP, e elementos do JavaFX
import org.json.JSONObject; // Para manipular objetos JSON
import java.net.HttpURLConnection; // Para criar e gerenciar conexões HTTP
import java.net.URL; // Para representar URLs

import javafx.application.Application; // Classe base para criar aplicações JavaFX
import javafx.fxml.FXMLLoader; // Para carregar layouts definidos em arquivos FXML
import javafx.scene.Parent; // Representa o nó raiz de uma cena
import javafx.scene.Scene; // Representa uma cena no JavaFX
import javafx.scene.control.*; // Para controles de interface como botões e campos de texto
import javafx.stage.Stage; // Representa a janela principal da aplicação
import model.Apostador; // Classe que representa um apostador (model)
import model.Apostador.Endereco; // Classe aninhada que representa o endereço de um apostador

// Classe principal que herda de Application para criar uma aplicação JavaFX
public class RegistrarController extends Application {

    // Instância da classe Autenticador para gerenciar autenticações
    Autenticador autenticador = new Autenticador();

    @Override
    public void start(Stage tela) throws Exception {
        // Carrega o layout da interface de registro de um arquivo FXML
        Parent root = FXMLLoader.load(getClass().getResource("/view/layoutRegistrar.fxml"));
        Scene scene = new Scene(root);
        tela.setScene(scene); // Define a cena para a janela
        tela.setTitle("Registro de Usuário"); // Define o título da janela
        tela.show(); // Exibe a janela

        // Obtém referências para os elementos da interface a partir do FXML
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
        Hyperlink termosLink = (Hyperlink) root.lookup("#termosLink");

        // Preenche o ComboBox de gênero com opções
        generoField.getItems().addAll("Masculino", "Feminino", "Outro");

        // Define a ação para o hyperlink de termos
        termosLink.setOnAction(e -> {
            try {
                // Abre o link dos termos no navegador padrão
                java.awt.Desktop.getDesktop().browse(new java.net.URI("https://www.caixa.gov.br/Downloads/circulares-caixa-loterias/circular_caixa_lotericas.pdf"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Define a ação para o botão de verificação de CEP
        verificarCEPButton.setOnAction(e -> {
            String logradouro = "";
            String uf = "";
            String cidade = "";
            String bairro = "";
            String cep = cepField.getText(); // Obtém o valor do campo de CEP
            try {
                // Cria a URL para consulta ao serviço ViaCEP
                URL url = new URL("https://viacep.com.br/ws/" + cep + "/json/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // Abre a conexão
                connection.setRequestMethod("GET"); // Define o método como GET
                connection.connect();
                if (connection.getResponseCode() == 200) { // Verifica se a resposta foi bem-sucedida
                    String json = new String(connection.getInputStream().readAllBytes()); // Lê a resposta como JSON
                    JSONObject obj = new JSONObject(json); // Converte para objeto JSON
                    uf = obj.getString("uf");
                    cidade = obj.getString("localidade");
                    bairro = obj.getString("bairro");
                    logradouro = obj.getString("logradouro");

                    // Preenche os campos da interface com os valores obtidos
                    ruaField.setText(logradouro);
                    ufField.setText(uf);
                    cidadeField.setText(cidade);
                    bairroField.setText(bairro);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Define a ação para o botão de registrar
        registrarButton.setOnAction(event -> {
            // Verifica se o CPF já está registrado
            if (autenticador.cpfJaRegistrado(cpfField.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("CPF já registrado");
                alert.setContentText("O CPF informado já está registrado no sistema");
                alert.show();
                return;
            }

            // Verifica se o nome de usuário já está registrado
            if (autenticador.usuarioJaRegistrado(userField.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Usuário já registrado");
                alert.setContentText("O usuário informado já está registrado no sistema");
                alert.show();
                return;
            }

            // Verifica se todos os campos obrigatórios foram preenchidos
            if (cpfField.getText().isEmpty() || nomeField.getText().isEmpty() || emailField.getText().isEmpty() || telefoneField.getText().isEmpty() || generoField.getValue() == null || userField.getText().isEmpty() || senhaField.getText().isEmpty() || dataField.getValue() == null || ruaField.getText().isEmpty() || bairroField.getText().isEmpty() || cidadeField.getText().isEmpty() || ufField.getText().isEmpty() || cepField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Campos obrigatórios não preenchidos");
                alert.setContentText("Todos os campos devem ser preenchidos");
                alert.show();
                return;
            }

            // Verifica se o CPF possui 11 dígitos
            if (cpfField.getText().length() != 11) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("CPF inválido");
                alert.setContentText("O CPF deve conter 11 dígitos");
                alert.show();
                return;
            }

            // Verifica se os termos foram aceitos
            if (termosCheck.isSelected()) {
                // Cria um novo apostador com os dados fornecidos
                Apostador apostador = new Apostador();
                apostador.setNome(nomeField.getText());
                apostador.setEmail(emailField.getText());
                apostador.setCpf(cpfField.getText());
                apostador.setTelefone(telefoneField.getText());
                apostador.setGenero(generoField.getValue());
                apostador.setUser(userField.getText());
                apostador.setSenha(senhaField.getText());
                apostador.setDataNascimento(dataField.getValue().toString());

                // Cria um endereço associado ao apostador
                Endereco endereco = apostador.new Endereco();
                endereco.setRua(ruaField.getText());
                endereco.setBairro(bairroField.getText());
                endereco.setCidade(cidadeField.getText());
                endereco.setUF(ufField.getText());
                endereco.setCep(Integer.parseInt(cepField.getText()));

                apostador.setEndereco(endereco);

                // Registra o usuário no sistema
                autenticador.registrarUsuario(apostador);

                // Exibe mensagem de sucesso
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText("Usuário registrado com sucesso");
                alert.setContentText("Seu usuário foi registrado com sucesso, você já pode fazer login");
                alert.show();
                tela.close(); // Fecha a janela de registro
            } else {
                // Exibe erro caso os termos não sejam aceitos
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Termos de uso não aceitos");
                alert.setContentText("Você deve aceitar os termos de uso para continuar");
                alert.show();
            }
        });
    }
}
