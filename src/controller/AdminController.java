import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.DatePicker;
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
        ChoiceBox concursosBox = (ChoiceBox) root.lookup("#concursosBox");

        painelCriar.setVisible(false);
        painelVer.setVisible(false);

        Button criarBotao = (Button) root.lookup("#criarBotao");
        Button verBotao = (Button) root.lookup("#verBotao");

        criarBotao.setOnAction(e -> {
            painelCriar.setVisible(true);
            painelVer.setVisible(false);

            TextField idConcurso = (TextField) root.lookup("#idField");
            DatePicker dataConcurso = (DatePicker) root.lookup("#dataField");

            CheckBox situacao = (CheckBox) root.lookup("#ativarCheck");
            Button salvarConcurso = (Button) root.lookup("#salvarButton");

            salvarConcurso.setOnAction(e2 -> {
                Concurso concurso = new Concurso();
                concurso.setId(Integer.parseInt(idConcurso.getText()));
                concurso.setDataCriacao(new Date());
                concurso.setDataSorteio(java.sql.Date.valueOf(dataConcurso.getValue()));
                concurso.setNumerosSorteados(new ArrayList<>());
                concurso.setSituacao(situacao.isSelected() ? "Ativo" : "Inativo");
                concurso.setPremioAcumulado(0);
                concurso.setApostas(new ArrayList<>());

                Autenticador.salvarConcurso(concurso);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Concurso salvo");
                alert.setHeaderText("Concurso salvo com sucesso");
                alert.showAndWait();

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

            concursosBox.setOnAction(e -> {
                Integer concursoId = (Integer) concursosBox.getValue();
                if (concursoId != null) {
                    for (int i = 0; i < concursosatualizado.length(); i++) {
                        JSONObject concurso = concursosatualizado.getJSONObject(i);
                        if (concurso.getInt("id") == concursoId) {
                            dataSorteioField.setText(concurso.getString("dataSorteio"));
                            dataCriacaoField.setText(concurso.getString("dataCriacao"));
                            situacaoCheckBox.setSelected(concurso.getString("situacao").equals("Ativo"));
                            premioAcumuladoField.setText(String.valueOf(concurso.getInt("premioAcumulado")));
                            numerosSorteadosField.setText(concurso.getJSONArray("numerosSorteados").toString());

                            TableView<Aposta> tabelaApostadores = (TableView<Aposta>) root.lookup("#tabelaApostadores");

                            TableColumn<Aposta, String> nomeApostadores = (TableColumn<Aposta, String>) ((TableView<?>) root
                                    .lookup("#tabelaApostadores")).getColumns().get(0);
                            TableColumn<Aposta, String> numerosApostadores = (TableColumn<Aposta, String>) ((TableView<?>) root
                                    .lookup("#tabelaApostadores")).getColumns().get(1);
                            TableColumn<Aposta, LocalDate> dataAposta = (TableColumn<Aposta, LocalDate>) ((TableView<?>) root
                                    .lookup("#tabelaApostadores")).getColumns().get(2);
                            TableColumn<Aposta, Double> valorAposta = (TableColumn<Aposta, Double>) ((TableView<?>) root
                                    .lookup("#tabelaApostadores")).getColumns().get(3);

                            JSONArray apostas = Autenticador.carregarApostas();
                            ObservableList<Aposta> apostasList = FXCollections.observableArrayList();
                            for (int j = 0; j < apostas.length(); j++) {
                                JSONObject apostaJson = apostas.getJSONObject(i);
                                Aposta aposta = new Aposta();
                                aposta.setApostador(apostaJson.getString("apostador"));
                                aposta.setNumerosSelecionados(new ArrayList<>());
                                for (Object numero : apostaJson.getJSONArray("numerosSelecionados")) {
                                    aposta.getNumerosSelecionados().add((Integer) numero);
                                }
                                aposta.setDataCriacao(LocalDate.parse(apostaJson.getString("dataCriacao")));
                                aposta.setValorPago(apostaJson.getDouble("valorPago"));
                                apostasList.add(aposta);
                            }

                            nomeApostadores.setCellValueFactory(new PropertyValueFactory<>("apostador"));
                            numerosApostadores.setCellValueFactory(new PropertyValueFactory<>("numerosSelecionados"));
                            dataAposta.setCellValueFactory(new PropertyValueFactory<>("dataCriacao"));
                            valorAposta.setCellValueFactory(new PropertyValueFactory<>("valorPago"));

                            tabelaApostadores.setItems(apostasList);
                        }
                    }
                }

            });

            editarConcursoButton.setOnAction(e3 -> {
                concluirEdicaoButton.setVisible(true);
                editarConcursoButton.setVisible(false);

                dataSorteioField.setEditable(true);
                situacaoCheckBox.setDisable(false);
                premioAcumuladoField.setEditable(true);
            });

            concluirEdicaoButton.setOnAction(e4 -> {
                concluirEdicaoButton.setVisible(false);
                editarConcursoButton.setVisible(true);

                dataSorteioField.setEditable(false);
                situacaoCheckBox.setDisable(true);
                premioAcumuladoField.setEditable(false);

                int concursoId = (int) concursosBox.getValue();

                for (int i = 0; i < concursosatualizado.length(); i++) {
                    JSONObject concurso = concursosatualizado.getJSONObject(i);
                    if (concurso.getInt("id") == concursoId) {
                        concurso.put("dataSorteio", dataSorteioField.getText());
                        concurso.put("situacao", situacaoCheckBox.isSelected() ? "Ativo" : "Inativo");
                        concurso.put("premioAcumulado", Integer.parseInt(premioAcumuladoField.getText()));

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

            verificarGanhadorButton.setOnAction(e7 -> {
                int concursoId = (int) concursosBox.getValue();
                JSONArray apostas = Autenticador.carregarApostas();

                for (int i = 0; i < apostas.length(); i++) {
                    JSONObject aposta = apostas.getJSONObject(i);
                    if (aposta.getInt("id") == concursoId) {
                        List<Object> numerosSorteados = new ArrayList<>();
                        for (int j = 0; j < concursosatualizado.length(); j++) {
                            JSONObject concurso = concursosatualizado.getJSONObject(j);
                            if (concurso.getInt("id") == concursoId) {
                                numerosSorteados = concurso.getJSONArray("numerosSorteados").toList();
                            }
                        }

                        List<Object> numerosApostados = aposta.getJSONArray("numerosSelecionados").toList();
                        int qtdeAcertos = 0;
                        for (Object numero : numerosApostados) {
                            if (numerosSorteados.contains(numero)) {
                                qtdeAcertos++;
                            }
                        }

                        aposta.put("qtdeAcertos", qtdeAcertos);

                        try {
                            BufferedWriter writer = new BufferedWriter(new FileWriter(APOSTAS_FILE));
                            writer.write(apostas.toString(4));
                            writer.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Apostas verificadas");
                alert.setHeaderText("Apostas verificadas com sucesso");
                alert.showAndWait();
                for (int i = 0; i < apostas.length(); i++) {
                    JSONObject aposta = apostas.getJSONObject(i);
                    if (aposta.getInt("id") == concursoId) {
                        int qtdeAcertos = aposta.getInt("qtdeAcertos");
                        if (qtdeAcertos >= 11) {
                            double valorGanho = 0;
                            switch (qtdeAcertos) {
                                case 15:
                                    valorGanho = 520192.42;
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

                            StringBuilder valorReceber = new StringBuilder();
                            valorReceber.append(String.format("R$ %,.2f", valorGanho));

                            
                            Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                            alert2.setTitle("Ganhador encontrado");
                            alert2.setHeaderText("Aposta ganhadora encontrada");
                            alert2.setContentText("Apostador: " + aposta.getString("apostador") + "\nQuantidade de acertos: " + qtdeAcertos + "\nValor ganho: " + valorReceber);
                            alert2.showAndWait();
                        }
                    }
                }
                
            });

        });

    }

}
