import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

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
                    numerosField.setText(concurso.getString("numeros"));
                    dataField.setText(concurso.getString("data"));
                }

            }

            apostarButton.setOnAction(e2 -> {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Aposta realizada com sucesso");
                alert.show();

                try {
                    JSONObject aposta = new JSONObject();
                    aposta.put("id", idField.getText());
                    aposta.put("numeros", numerosField.getText());
                    aposta.put("data", dataField.getText());
                    aposta.put("apostador", usuarioLogado);


                    String apostasContent = new String(Files.readAllBytes(Paths.get(APOSTAS_FILE)));
                    JSONArray apostas = apostasContent.isEmpty() ? new JSONArray() : new JSONArray(apostasContent);
                    apostas.put(aposta);

                    Files.write(Paths.get(APOSTAS_FILE), apostas.toString(4).getBytes());

                } catch (JSONException | IOException e3) {
                    e3.printStackTrace();
                    
                } 
            });
        });

    }

}