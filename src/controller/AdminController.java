import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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
import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class AdminController {

    public void start(Stage tela) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/view/adminTela.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/resources/style.css").toExternalForm());
        tela.setScene(scene);
        tela.setTitle("Admin");

        tela.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        tela.show();

        AnchorPane painelCriar = (AnchorPane) root.lookup("#painelCriar");
        AnchorPane painelVer = (AnchorPane) root.lookup("#painelVer");

        painelCriar.setVisible(false);
        painelVer.setVisible(false);

        Button criarBotao = (Button) root.lookup("#criarBotao");
        Button verBotao = (Button) root.lookup("#verBotao");

        criarBotao.setOnAction(e -> {
            painelCriar.setVisible(true);
            painelVer.setVisible(false);

            TextField idConcurso = (TextField) root.lookup("#idField");
            DatePicker dataConcurso = (DatePicker) root.lookup("#dataField");
            TextField numerosSorteados = (TextField) root.lookup("#numerosField");

            Button gerar = (Button) root.lookup("#gerarButton");
            Button salvarConcurso = (Button) root.lookup("#salvarButton");

            gerar.setOnAction(e1 -> {
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

                numerosSorteados.setText(numeros.toString());
            });

            salvarConcurso.setOnAction(e2 -> {
                int id = Integer.parseInt(idConcurso.getText());
                String data = dataConcurso.getValue().toString();
                String numeros = numerosSorteados.getText();

                final String CONCURSOS_FILE = "C:\\Users\\bruno\\OneDrive\\Documentos\\LotoFacilProject2\\src\\db\\concursos.json";
                try {
                    String content = new String(Files.readAllBytes(Paths.get(CONCURSOS_FILE)),
                            StandardCharsets.UTF_8);
                    JSONArray concursosArray;
                    if (content.isEmpty()) {
                        concursosArray = new JSONArray();
                    } else {
                        concursosArray = new JSONArray(content);
                    }

                    JSONObject concurso = new JSONObject();

                    for (int i = 0; i < concursosArray.length(); i++) {
                        JSONObject existingConcurso = concursosArray.getJSONObject(i);
                        if (existingConcurso.getInt("id") == id) {
                            Alert alerta = new Alert(Alert.AlertType.ERROR);
                            alerta.setHeaderText("Concurso com o id: " + id + " já existe");
                            alerta.show();
                            return;
                        }
                    }

                    concurso.put("id", id);
                    concurso.put("data", data);
                    concurso.put("numeros", numeros);

                    concursosArray.put(concurso);

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONCURSOS_FILE))) {
                        writer.write(concursosArray.toString(4));
                        writer.newLine();
                        System.out.println("Concurso salvo com sucesso: " + concurso.toString(4));
                    }
                } catch (IOException e3) {
                    e3.printStackTrace();
                    System.out.println("Erro ao ler ou escrever no arquivo concurso.json: " + e3.getMessage());

                } catch (Exception e3) {
                    e3.printStackTrace();
                    System.out.println("Erro inesperado: " + e3.getMessage());
                }
            });

            verBotao.setOnAction(e2 -> {
                painelCriar.setVisible(false);
                painelVer.setVisible(true);

            });

        });
    }
}
