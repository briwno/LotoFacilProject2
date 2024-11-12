import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.basic.BasicOptionPaneUI.ButtonAreaLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Aposta;

public class ApostadorController extends Application {
    private String usuarioLogado;

    private static String CONCURSOS_FILE = "src/db/concursos.json";
    private static String APOSTAS_FILE = "src/db/apostas.json";

    public void setUsuarioLogado(String usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage tela) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/apostadorTela.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/resources/style.css").toExternalForm());
        tela.setScene(scene);
        tela.setTitle("Apostador");
        tela.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        tela.show();

        Label nameLabel = (Label) root.lookup("#nameLabel");
        nameLabel.setText(usuarioLogado);
        ChoiceBox concursosBox = (ChoiceBox) root.lookup("#concursosBox");

        TextField idField = (TextField) root.lookup("#idField");
        TextField numerosField = (TextField) root.lookup("#numerosField");
        TextField dataField = (TextField) root.lookup("#dataField");
        Button gerarButton = (Button) root.lookup("#gerarButton");
        Button escolherButton = (Button) root.lookup("#escolherNumerosButton");
        Button apostarButton = (Button) root.lookup("#apostarButton");
        TextField custoField = (TextField) root.lookup("#custoField");

        JSONArray concursos = new JSONArray(new String(Files.readAllBytes(Paths.get(CONCURSOS_FILE))));

        for (int i = 0; i < concursos.length(); i++) {
            JSONObject concurso = concursos.getJSONObject(i);
            concursosBox.getItems().add(concurso.getInt("id"));
        }

        concursosBox.setOnAction(e -> {
            int concursoId = (int) concursosBox.getValue();

            for (int i = 0; i < concursos.length(); i++) {
                JSONObject concurso = concursos.getJSONObject(i);
                if (concurso.getInt("id") == concursoId) {
                    idField.setText(String.valueOf(concurso.getInt("id")));
                    dataField.setText(concurso.getString("data"));
                }

            }

            escolherButton.setOnAction(e2 -> {

                try {
                    FXMLLoader escolherLoader = new FXMLLoader(
                            getClass().getResource("/view/escolherNumerosTela.fxml"));
                    Parent escolherRoot = escolherLoader.load();
                    Stage escolherStage = new Stage();
                    escolherStage.setScene(new Scene(escolherRoot));
                    escolherStage.setTitle("Escolher Números");
                    escolherStage.show();
                    TextField qtdeSelecionados = (TextField) escolherRoot.lookup("#selecionadosField");
                    qtdeSelecionados.setText("0");
                    Button autoSelect = (Button) escolherRoot.lookup("#autoselectButton");
                    Button limpar = (Button) escolherRoot.lookup("#limparButton");
                    Button okButton = (Button) escolherRoot.lookup("#okButton");

                    ToggleButton[] numeros = new ToggleButton[25];
                    for (int i = 0; i < 25; i++) {
                        numeros[i] = (ToggleButton) escolherRoot.lookup("#num" + (i + 1));
                        final int index = i;
                        numeros[index].setOnAction(e3 -> {
                            if (numeros[index].isSelected()) {
                                numeros[index].setStyle("-fx-background-color: #00ff00");
                                qtdeSelecionados
                                        .setText(String.valueOf(Integer.parseInt(qtdeSelecionados.getText()) + 1));
                            } else {
                                numeros[index].setStyle("");
                            }
                        });
                    }

                    autoSelect.setOnAction(e3 -> {
                        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(15, 15, 16, 17, 18, 19, 20);
                        dialog.setTitle("Quantidade de Números");
                        dialog.setHeaderText("Seleção Automática");
                        dialog.setContentText("Escolha a quantidade de números:");

                        dialog.showAndWait().ifPresent(qtde -> {
                            for (int i = 0; i < 25; i++) {
                                numeros[i].setSelected(false);
                                numeros[i].setStyle("");
                            }
                            List<Integer> selecionados = new ArrayList<>();
                            for (selecionados.size(); selecionados.size() < qtde; selecionados.size()) {
                                int num = (int) (Math.random() * 25);
                                if (!selecionados.contains(num)) {
                                    selecionados.add(num);
                                    numeros[num].setSelected(true);
                                    numeros[num].setStyle("-fx-background-color: #00ff00");
                                }
                            }
                            qtdeSelecionados.setText(String.valueOf(selecionados.size()));
                        });
                    });

                    limpar.setOnAction(e3 -> {
                        for (int i = 0; i < 25; i++) {
                            numeros[i].setSelected(false);
                            numeros[i].setStyle("");
                        }
                        qtdeSelecionados.setText("0");
                    });

                    okButton.setOnAction(e4 -> {
                        if (Integer.parseInt(qtdeSelecionados.getText()) < 15) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText("Selecione ao menos 15 números");
                            alert.show();
                            return;
                        }
                        escolherStage.close();
                        List<Integer> selecionados = new ArrayList<>();
                        for (int i = 0; i < 25; i++) {
                            if (numeros[i].isSelected()) {
                                selecionados.add(i + 1);
                            }
                        }
                        numerosField.setText(selecionados.toString());

                        String custoAposta = "0,00";
                        switch (Integer.parseInt(qtdeSelecionados.getText())) {
                            case 15:
                                custoAposta = "3,00";
                                break;
                            case 16:
                                custoAposta = "44,00";
                                break;
                            case 17:
                                custoAposta = "408,00";
                                break;
                            case 18:
                                custoAposta = "2.448,00";
                                break;
                            case 19:
                                custoAposta = "11.628,00";
                                break;
                            case 20:
                                custoAposta = "36.512,00";
                                break;
                        }
                        custoField.setText(custoAposta);
                    });

                } catch (IOException e3) {
                    e3.printStackTrace();
                }

            });

            apostarButton.setOnAction(e2 -> {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Aposta realizada com sucesso");
                alert.show();

                Aposta aposta = new Aposta();

            });
        });

    }

}