import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
import javafx.scene.image.Image;
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

            gerarButton.setOnAction(e1 -> {
                List<Integer> numeroGerados = new ArrayList<>();

                while (numeroGerados.size() < 15) {
                    int numero = (int) (Math.random() * 25) + 1;
                    if (!numeroGerados.contains(numero)) {
                        numeroGerados.add(numero);
                    }
                }

                StringBuilder numeros = new StringBuilder();
                for (int i = 0; i < numeroGerados.size(); i++) {
                    numeros.append(numeroGerados.get(i));
                    if (i < numeroGerados.size() - 1) {
                        numeros.append(", ");
                    }
                }
                numerosField.setText(numeros.toString());
                
            });

            escolherButton.setOnAction(e2 -> {
                try {
                    FXMLLoader escolherLoader = new FXMLLoader(getClass().getResource("/view/escolherNumerosTela.fxml"));
                    Parent escolherRoot = escolherLoader.load();
                    Stage escolherStage = new Stage();
                    escolherStage.setScene(new Scene(escolherRoot));
                    escolherStage.setTitle("Escolher Números");
                    escolherStage.show();

                    Button okButton = (Button) escolherRoot.lookup("#okButton");

                    okButton.setOnAction(e3 -> {
                        escolherStage.close();
                    });
                    


                    
                    for (int i = 1; i <= 15; i++) {
                        final int numero = i;
                        RadioButton botao = (RadioButton) escolherRoot.lookup("#numero" + numero);
                        botao.setOnAction(e3 -> {
                            if (botao.isSelected()) {
                                numerosField.setText(numerosField.getText() + String.valueOf(numero) + ", ");
                            } else {
                                numerosField.setText(numerosField.getText().replace(numero + ", ", ""));
                            }
                        });
                    }

                

                    
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