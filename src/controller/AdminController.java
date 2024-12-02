import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Aposta;
import model.Concurso;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import java.util.Date;

public class AdminController {

    private static String CONCURSOS_FILE = "src/db/concursos.json";
    private static String APOSTAS_FILE = "src/db/apostas.json";

    @SuppressWarnings("unchecked")
    public void start(Stage tela) throws Exception {

        JSONArray concursos = Autenticador.carregarConcursos();

        Parent root = FXMLLoader.load(getClass().getResource("/view/adminTela.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/resources/style.css").toExternalForm());
        tela.setScene(scene);
        tela.setTitle("Admin");

        tela.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        tela.show();

        AnchorPane painelCriar = (AnchorPane) root.lookup("#painelCriar");
        AnchorPane painelVer = (AnchorPane) root.lookup("#painelVer");
        AnchorPane painelUsers = (AnchorPane) root.lookup("#usersPainel");
        AnchorPane infoPane = (AnchorPane) root.lookup("#infoPane");
        ChoiceBox concursosBox = (ChoiceBox) root.lookup("#concursosBox");

        infoPane.setVisible(false);
        painelCriar.setVisible(false);
        painelVer.setVisible(false);
        painelUsers.setVisible(false);

        Button criarBotao = (Button) root.lookup("#criarBotao");
        Button verBotao = (Button) root.lookup("#verBotao");
        Button usersBotao = (Button) root.lookup("#usersButton");

        criarBotao.setOnAction(e -> {
            painelCriar.setVisible(true);
            painelVer.setVisible(false);

            TextField idConcurso = (TextField) root.lookup("#idField");
            DatePicker dataConcurso = (DatePicker) root.lookup("#dataField");
            TextField horarioField = (TextField) root.lookup("#horarioField");
            ChoiceBox situacaoChoiceBox = (ChoiceBox) root.lookup("#situacaoChoiceBox");
            situacaoChoiceBox.getItems().addAll("Ativo", "Inativo");

            Button gerarConcursoButton = (Button) root.lookup("#gerarConcursoButton");
            Button salvarConcurso = (Button) root.lookup("#salvarButton");

            idConcurso.setText(String.valueOf(concursos.length() + 1));

            gerarConcursoButton.setOnAction(e2 -> {
                dataConcurso.setValue(LocalDate.now());
                horarioField.setText("22:00");
            });

            salvarConcurso.setOnAction(e2 -> {

                if (idConcurso.getText().isEmpty() || dataConcurso.getValue() == null
                        || horarioField.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro ao salvar concurso");
                    alert.setHeaderText("Todos os campos devem ser preenchidos");
                    alert.showAndWait();
                    return;
                }

                if (java.sql.Date.valueOf(dataConcurso.getValue()).before(new Date())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro ao salvar concurso");
                    alert.setHeaderText("Data do concurso deve ser futura");
                    alert.showAndWait();
                    return;
                }

                for (int i = 0; i < concursos.length(); i++) {
                    JSONObject concursoExistente = concursos.getJSONObject(i);
                    LocalDate dataExistente = LocalDate.parse(concursoExistente.getString("dataSorteio"));
                    if (dataExistente.equals(dataConcurso.getValue())
                            && horarioField.getText().equals(concursoExistente.getString("horario"))) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erro ao salvar concurso");
                        alert.setHeaderText("Já existe um concurso na data selecionada");
                        alert.showAndWait();
                        return;
                    }
                }

                Concurso concurso = new Concurso();
                concurso.setId(Integer.parseInt(idConcurso.getText()));
                concurso.setDataCriacao(new Date());
                concurso.setDataSorteio(java.sql.Date.valueOf(dataConcurso.getValue()));
                concurso.setHorario(horarioField.getText());
                concurso.setNumerosSorteados(new ArrayList<>());
                concurso.setSituacao(situacaoChoiceBox.getValue().toString());
                for (int i = 0; i < concursos.length(); i++) {
                    JSONObject concursoExistente = concursos.getJSONObject(i);
                    if (concursoExistente.getString("situacao").equals("Ativo")) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erro ao salvar concurso");
                        alert.setHeaderText("Já existe um concurso ativo. Por favor, apague o concurso ativo ou defina-o como Inativo.");
                        alert.showAndWait();
                        return;
                    }
                }
                concurso.setPremioAcumulado(0);
                concurso.setApostas(new ArrayList<>());

                Autenticador.salvarConcurso(concurso);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Concurso salvo");
                alert.setHeaderText("Concurso salvo com sucesso");
                alert.showAndWait();

                try {
                    JSONArray concursosatualizado = Autenticador.carregarConcursos();
                    idConcurso.setText(String.valueOf(concursosatualizado.length() + 1));
                    dataConcurso.setValue(null);
                    horarioField.setText("");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });
        });

        // painel de ver e editar concursos
        verBotao.setOnAction(e2 -> {

            painelCriar.setVisible(false);
            painelVer.setVisible(true);

            JSONArray concursosatualizado = Autenticador.carregarConcursos();
            concursosBox.getItems().clear();
            for (int i = 0; i < concursosatualizado.length(); i++) {
                JSONObject concurso = concursosatualizado.getJSONObject(i);
                concursosBox.getItems().add(concurso.getInt("id"));
            }

            Button encerrarApostasButton = (Button) root.lookup("#encerrarApostasButton");
            Button apagarConcursoButton = (Button) root.lookup("#apagarConcursoButton");
            Button editarConcursoButton = (Button) root.lookup("#editarConcursoButton");
            Button concluirEdicaoButton = (Button) root.lookup("#concluirEdicaoButton");
            Button sortearButton = (Button) root.lookup("#sortearButton");
            Button verificarGanhadorButton = (Button) root.lookup("#verificarButton");

            concluirEdicaoButton.setVisible(false);

            CheckBox situacaoCheckBox = (CheckBox) root.lookup("#situacaoCheckBox");

            TextField numerosSorteadosField = (TextField) root.lookup("#numerosSorteadosField");

            TextField premioAcumuladoField = (TextField) root.lookup("#premioAcumuladoField");

            TextField dataSorteioField = (TextField) root.lookup("#dataSorteioField");
            TextField dataCriacaoField = (TextField) root.lookup("#datacriacaoField");
            TextField valoresApostadosField = (TextField) root.lookup("#valoresApostadosField");

            concursosBox.setOnAction(e -> {
                
                Integer concursoId = (Integer) concursosBox.getValue();
                if (concursoId != null) {
                    for (int i = 0; i < concursosatualizado.length(); i++) {
                        JSONObject concurso = concursosatualizado.getJSONObject(i);
                        if (concurso.getInt("id") == concursoId) {
                            dataSorteioField.setText(concurso.getString("dataSorteio"));
                            dataCriacaoField.setText(concurso.getString("dataCriacao"));
                            premioAcumuladoField.setText(String.valueOf(concurso.getInt("premioAcumulado")));
                            numerosSorteadosField.setText(concurso.getJSONArray("numerosSorteados").toString());

                            TableView<Aposta> tabelaApostadores = (TableView<Aposta>) root.lookup("#tabelaApostadores");

                            TableColumn<Aposta, String> idApostadores = (TableColumn<Aposta, String>) ((TableView<?>) root.lookup("#tabelaApostadores")).getColumns().get(0);
                            TableColumn<Aposta, String> numerosApostadores = (TableColumn<Aposta, String>) ((TableView<?>) root.lookup("#tabelaApostadores")).getColumns().get(1);
                            TableColumn<Aposta, LocalDate> dataAposta = (TableColumn<Aposta, LocalDate>) ((TableView<?>) root.lookup("#tabelaApostadores")).getColumns().get(2);
                            TableColumn<Aposta, Double> valorAposta = (TableColumn<Aposta, Double>) ((TableView<?>) root.lookup("#tabelaApostadores")).getColumns().get(3);

                            JSONArray apostas = Autenticador.carregarApostas();
                            ObservableList<Aposta> apostasList = FXCollections.observableArrayList();
                            for (int j = 0; j < apostas.length(); j++) {
                                JSONObject apostaJson = apostas.getJSONObject(j);
                                Aposta aposta = new Aposta();

                                aposta.setNumerosSelecionados(new ArrayList<>());
                                for (Object numero : apostaJson.getJSONArray("numerosSelecionados")) {
                                    aposta.getNumerosSelecionados().add((Integer) numero);
                                }
                                aposta.setDataCriacao(LocalDate.parse(apostaJson.getString("dataCriacao")));
                                aposta.setValorPago(apostaJson.getDouble("valorPago"));
                                apostasList.add(aposta);
                            }

                            idApostadores.setCellValueFactory(new PropertyValueFactory<>("idApostador"));
                            numerosApostadores.setCellValueFactory(new PropertyValueFactory<>("numerosSelecionados"));
                            dataAposta.setCellValueFactory(new PropertyValueFactory<>("dataCriacao"));
                            valorAposta.setCellValueFactory(new PropertyValueFactory<>("valorPago"));

                            tabelaApostadores.setItems(apostasList);

                            double totalValoresApostados = 0;
                            for (Aposta aposta : apostasList) {
                                totalValoresApostados += aposta.getValorPago();
                            }
                            valoresApostadosField.setText(String.format("R$ %,.2f", totalValoresApostados));

                        }
                    }
                }

            });

            editarConcursoButton.setOnAction(e3 -> {
                concluirEdicaoButton.setVisible(true);
                editarConcursoButton.setVisible(false);

                dataSorteioField.setEditable(true);
                numerosSorteadosField.setEditable(true);
                premioAcumuladoField.setEditable(true);
            });

            concluirEdicaoButton.setOnAction(e4 -> {
                concluirEdicaoButton.setVisible(false);
                editarConcursoButton.setVisible(true);

                dataSorteioField.setEditable(false);
                premioAcumuladoField.setEditable(false);
                numerosSorteadosField.setEditable(false);

                int concursoId = (int) concursosBox.getValue();

                for (int i = 0; i < concursosatualizado.length(); i++) {
                    JSONObject concurso = concursosatualizado.getJSONObject(i);
                    if (concurso.getInt("id") == concursoId) {
                        concurso.put("dataSorteio", dataSorteioField.getText());
                        concurso.put("premioAcumulado", Integer.parseInt(premioAcumuladoField.getText()));
                        concurso.put("numerosSorteados", new JSONArray(numerosSorteadosField.getText()));

                        try {
                            BufferedWriter writer = new BufferedWriter(new FileWriter(CONCURSOS_FILE));
                            writer.write(concursosatualizado.toString());
                            writer.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });

            encerrarApostasButton.setOnAction(e5 -> {
                int concursoId = (int) concursosBox.getValue();

                for (int i = 0; i < concursos.length(); i++) {
                    JSONObject concurso = concursos.getJSONObject(i);
                    if (concurso.getInt("id") == concursoId) {
                        concurso.put("situacao", "Encerrado");

                        try {
                            BufferedWriter writer = new BufferedWriter(new FileWriter(CONCURSOS_FILE));
                            writer.write(concursos.toString());
                            writer.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Concurso encerrado");
                alert.setHeaderText("Concurso encerrado com sucesso");
                alert.showAndWait();

                sortearButton.setDisable(false);

            });

            sortearButton.setOnAction(e6 -> {
                int concursoId = (int) concursosBox.getValue();
                for (int i = 0; i < concursos.length(); i++) {
                    JSONObject concurso = concursos.getJSONObject(i);
                    if (concurso.getInt("id") == concursoId) {
                        List<Integer> numerosSorteados = new ArrayList<>();
                        while (numerosSorteados.size() < 15) {
                            int numero = (int) (Math.random() * 25) + 1;
                            if (!numerosSorteados.contains(numero)) {
                                numerosSorteados.add(numero);
                            }
                        }
                        concurso.put("numerosSorteados", numerosSorteados);

                        try {
                            BufferedWriter writer = new BufferedWriter(new FileWriter(CONCURSOS_FILE));
                            writer.write(concursos.toString(4));
                            writer.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Números sorteados");
                        alert.setHeaderText("Números sorteados com sucesso");
                        alert.setContentText("Números: " + numerosSorteados.toString());
                        alert.showAndWait();

                        numerosSorteadosField.setText(numerosSorteados.toString());
                    }
                }

            });

            apagarConcursoButton.setOnAction(e6 -> {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmação de exclusão");
                confirmAlert.setHeaderText("Você tem certeza que deseja apagar este concurso?");
                confirmAlert.setContentText("Esta ação não pode ser desfeita.");

                if (confirmAlert.showAndWait().get() != ButtonType.OK) {
                    return;
                }
                int concursoId = (int) concursosBox.getValue();
                JSONArray concursosAtualizado = new JSONArray();
                for (int i = 0; i < concursos.length(); i++) {
                    JSONObject concurso = concursos.getJSONObject(i);
                    if (concurso.getInt("id") != concursoId) {
                        concursosAtualizado.put(concurso);
                    }
                }

                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(CONCURSOS_FILE));
                    writer.write(concursosAtualizado.toString(4));
                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Concurso apagado");
                alert.setHeaderText("Concurso apagado com sucesso");
                alert.showAndWait();

                concursosBox.getItems().clear();
                for (int i = 0; i < concursosAtualizado.length(); i++) {
                    JSONObject concurso = concursosAtualizado.getJSONObject(i);
                    concursosBox.getItems().add(concurso.getInt("id"));
                }

                dataSorteioField.setText("");
                premioAcumuladoField.setText("");
                numerosSorteadosField.setText("");
                dataCriacaoField.setText("");
                valoresApostadosField.setText("");




            });

            verificarGanhadorButton.setOnAction(e7 -> {
                int concursoId = (int) concursosBox.getValue();
                JSONArray apostas = Autenticador.carregarApostas();

                JSONObject concurso = null;
                List<Integer> numerosSorteados = new ArrayList<>();
                for (int i = 0; i < concursosatualizado.length(); i++) {
                    concurso = concursosatualizado.getJSONObject(i);
                    if (concurso.getInt("id") == concursoId) {
                        numerosSorteados = new ArrayList<>();
                        JSONArray numerosArray = concurso.getJSONArray("numerosSorteados");
                        for (int j = 0; j < numerosArray.length(); j++) {
                            numerosSorteados.add(numerosArray.getInt(j));
                        }
                        break;
                    }
                }

                StringBuilder resultado = new StringBuilder();
                boolean TemGanhadore = false;

                for (int i = 0; i < apostas.length(); i++) {
                    JSONObject aposta = apostas.getJSONObject(i);
                    if (aposta.getInt("id") == concursoId) {
                        List<Integer> numerosApostados = new ArrayList<>();
                        JSONArray numerosArray = aposta.getJSONArray("numerosSelecionados");
                        for (int j = 0; j < numerosArray.length(); j++) {
                            numerosApostados.add(numerosArray.getInt(j));
                        }
                        int qtdeAcertos = 0;
                        for (Integer numero : numerosApostados) {
                            if (numerosSorteados.contains(numero)) {
                                qtdeAcertos++;
                            }
                        }

                        aposta.put("qtdeAcertos", qtdeAcertos);

                        if (qtdeAcertos >= 11) {
                            TemGanhadore = true;
                            double valorGanho = 0;
                            switch (qtdeAcertos) {
                                case 15:
                                    valorGanho = 520192.42 +
                                            concurso.getDouble("premioAcumulado");
                                    break;
                                case 14:
                                    valorGanho = 2038.74;
                                    break;
                                case 13:
                                    valorGanho = 30.00;
                                    break;
                                case 12:
                                    valorGanho = 12.00;
                                    break;
                                case 11:
                                    valorGanho = 6.00;
                                    break;
                            }

                            aposta.put("valorGanho", valorGanho);

                            resultado.append("Apostador: ").append(aposta.getString("apostador"))
                                    .append("\nQuantidade de acertos: ").append(qtdeAcertos)
                                    .append("\nValor ganho: R$ ").append(String.format("%,.2f", valorGanho))
                                    .append("\n\n");
                        }
                    }
                }

                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(APOSTAS_FILE));
                    writer.write(apostas.toString(4));
                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                if (TemGanhadore) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Apostas verificadas");
                    alert.setHeaderText("Apostas verificadas com sucesso");
                    alert.setContentText(resultado.toString());
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Apostas verificadas");
                    alert.setHeaderText("Nenhum ganhador encontrado");
                    alert.showAndWait();

                    boolean nenhumAcertou15 = true;
                    for (int i = 0; i < apostas.length(); i++) {
                        JSONObject aposta = apostas.getJSONObject(i);
                        if (aposta.getInt("id") == concursoId && aposta.getInt("qtdeAcertos") == 15) {
                            nenhumAcertou15 = false;
                            break;
                        }
                    }

                    if (nenhumAcertou15) {
                        Double valorAcumulado = concurso.getDouble("premioAcumulado") + 520192.42;
                        concurso.put("premioAcumulado", valorAcumulado);
                    }
                }

            });

        });

        usersBotao.setOnAction(e -> {

            painelUsers.setVisible(true);
            painelCriar.setVisible(false);
            painelVer.setVisible(false);
            ListView<String> usersList = (ListView<String>) root.lookup("#usersList");

            JSONArray users = Autenticador.carregarUsuarios();
            ObservableList<String> usersObs = FXCollections.observableArrayList();
            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);
                usersObs.add(user.getString("nome") + " - " + user.getString("cpf") + " - " + user.getString("user"));
            }

            usersList.setItems(usersObs);

            usersList.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    String selectedUser = usersList.getSelectionModel().getSelectedItem();
                    if (selectedUser != null) {
                        infoPane.setVisible(true);

                        Button fecharInfoButton = (Button) root.lookup("#fecharInfoButton");
                        fecharInfoButton.setOnAction(e3 -> {
                            infoPane.setVisible(false);
                        });
                        TextField nomeField = (TextField) root.lookup("#nomeField");
                        TextField emailField = (TextField) root.lookup("#emailField");
                        ComboBox generoField = (ComboBox) root.lookup("#generoField");
                        TextField cpfField = (TextField) root.lookup("#cpfField");
                        TextField telefoneField = (TextField) root.lookup("#telefoneField");
                        DatePicker dataNascPicker = (DatePicker) root.lookup("#dataNascField");

                        TextField userField = (TextField) root.lookup("#userField");
                        PasswordField senhaField = (PasswordField) root.lookup("#senhaField");

                        TextField ufField = (TextField) root.lookup("#ufField");
                        TextField cidadeField = (TextField) root.lookup("#cidadeField");
                        TextField bairroField = (TextField) root.lookup("#bairroField");
                        TextField cepField = (TextField) root.lookup("#cepField");
                        TextField ruaField = (TextField) root.lookup("#ruaField");
                        Button verificarCEPButton = (Button) root.lookup("#verificarCEPButton");
                        verificarCEPButton.setDisable(true);

                        for (int i = 0; i < users.length(); i++) {
                            JSONObject user = users.getJSONObject(i);
                            String userString = user.getString("nome") + " - " + user.getString("cpf") + " - "
                                    + user.getString("user");
                            if (userString.equals(selectedUser)) {
                                JSONObject userObj = users.getJSONObject(i);
                                nomeField.setText(userObj.getString("nome"));
                                emailField.setText(userObj.getString("email"));
                                generoField.setValue(userObj.getString("genero"));
                                cpfField.setText(userObj.getString("cpf"));
                                telefoneField.setText(userObj.getString("telefone"));
                                dataNascPicker.setValue(LocalDate.parse(userObj.getString("dataNascimento")));
                                dataNascPicker.setDisable(true);
                                JSONObject endereco = userObj.getJSONObject("endereco");
                                ufField.setText(endereco.getString("estado"));
                                cidadeField.setText(endereco.getString("cidade"));
                                bairroField.setText(endereco.getString("bairro"));
                                cepField.setText(String.valueOf(endereco.getInt("cep")));
                                ruaField.setText(endereco.getString("rua"));

                                userField.setText(userObj.getString("user"));
                                senhaField.setText(userObj.getString("senha"));

                            }
                        }

                    }
                }
            });

        });

    }

}
