import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Concurso;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

        JSONArray concursos = new JSONArray(new String(Files.readAllBytes(Paths.get(CONCURSOS_FILE))));

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

            concursosBox.getItems().clear();

            for (int i = 0; i < concursos.length(); i++) {
                JSONObject concurso = concursos.getJSONObject(i);
                concursosBox.getItems().add(concurso.getInt("id"));
            }

            Button encerrarApostasButton = (Button) root.lookup("#encerrarApostasButton");
            Button editarConcursoButton = (Button) root.lookup("#editarConcursoButton");
            Button concluirEdicaoButton = (Button) root.lookup("#concluirEdicaoButton");
            Button sortearButton = (Button) root.lookup("#sortearButton");

            concluirEdicaoButton.setVisible(false);

            CheckBox situacaoCheckBox = (CheckBox) root.lookup("#situacaoCheckBox");

            TextField numerosSorteadosField = (TextField) root.lookup("#numerosSorteadosField");

            TextField premioAcumuladoField = (TextField) root.lookup("#premioAcumuladoField");

            TextField dataSorteioField = (TextField) root.lookup("#dataSorteioField");
            TextField dataCriacaoField = (TextField) root.lookup("#datacriacaoField");

            concursosBox.setOnAction(e -> {
                
                int concursoId = (int) concursosBox.getValue();

                for (int i = 0; i < concursos.length(); i++) {
                    JSONObject concurso = concursos.getJSONObject(i);
                    if (concurso.getInt("id") == concursoId) {
                        dataSorteioField.setText(concurso.getString("dataSorteio"));
                        dataCriacaoField.setText(concurso.getString("dataCriacao"));
                        situacaoCheckBox.setSelected(concurso.getString("situacao").equals("Ativo"));
                        premioAcumuladoField.setText(String.valueOf(concurso.getInt("premioAcumulado")));
                        numerosSorteadosField.setText(concurso.getJSONArray("numerosSorteados").toString());
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

                for (int i = 0; i < concursos.length(); i++) {
                    JSONObject concurso = concursos.getJSONObject(i);
                    if (concurso.getInt("id") == concursoId) {
                        concurso.put("dataSorteio", dataSorteioField.getText());
                        concurso.put("situacao", situacaoCheckBox.isSelected() ? "Ativo" : "Inativo");
                        concurso.put("premioAcumulado", Integer.parseInt(premioAcumuladoField.getText()));

                        try {
                            BufferedWriter writer = new BufferedWriter(new FileWriter(CONCURSOS_FILE));
                            writer.write(concursos.toString());
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

        });

    }

}
