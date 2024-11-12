import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;

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
            CheckBox situacao = (CheckBox) root.lookup("#ativarCheck");
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
                Concurso concurso = new Concurso();
                concurso.setId(Integer.parseInt(idConcurso.getText()));
                concurso.setDataCriacao(new Date());
                concurso.setDataSorteio(java.sql.Date.valueOf(dataConcurso.getValue()));
                List<Integer> numeros = new ArrayList<>();
                for (String num : numerosSorteados.getText().split(", ")) {
                    numeros.add(Integer.parseInt(num));
                }
                concurso.setNumerosSorteados(numeros);
                concurso.setSituacao(situacao.isSelected() ? "Ativo" : "Inativo");

                Autenticador.salvarConcurso(concurso);
            });

            //painel de ver e editar concursos
            verBotao.setOnAction(e2 -> {
                painelCriar.setVisible(false);
                painelVer.setVisible(true);

                
                




            });

        });
    }
}
