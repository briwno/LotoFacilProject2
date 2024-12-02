import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Aposta;

public class ApostadorController extends Application {

    private static String CONCURSOS_FILE = "src/db/concursos.json";
    private static String APOSTAS_FILE = "src/db/apostas.json";
    private static String USERS_FILE = "src/db/users.json";

    private String usuarioLogado;
    private String idLogado;

    public void setUsuarioLogado(String usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public void setIdLogado(String idLogado) {
        this.idLogado = idLogado;
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

        TextField idField = (TextField) root.lookup("#idField");
        TextField numerosField = (TextField) root.lookup("#numerosField");
        TextField dataField = (TextField) root.lookup("#dataField");
        TextField  situacaoField = (TextField) root.lookup("#situacaoField");
        Button gerarButton = (Button) root.lookup("#gerarButton");
        Button escolherButton = (Button) root.lookup("#escolherNumerosButton");
        Button apostarButton = (Button) root.lookup("#apostarButton");
        Button sairButton = (Button) root.lookup("#sairButton");


        AnchorPane ApostarPane = (AnchorPane) root.lookup("#ApostarPane");
        AnchorPane ApostasPane = (AnchorPane) root.lookup("#ApostasPane");
        AnchorPane ResultadosPane = (AnchorPane) root.lookup("#ResultadosPane");
        AnchorPane PerfilPane = (AnchorPane) root.lookup("#PerfilPane");

        ApostarPane.setVisible(false);
        ApostasPane.setVisible(false);
        ResultadosPane.setVisible(false);
        PerfilPane.setVisible(false);

        Button btnApostar = (Button) root.lookup("#btnApostar");
        Button btnMinhasApostas = (Button) root.lookup("#btnMinhasApostas");
        Button btnResultados = (Button) root.lookup("#btnResultados");
        Button btnPerfil = (Button) root.lookup("#btnPerfil");

        btnResultados.setOnAction(e -> {
            ApostarPane.setVisible(false);
            ApostasPane.setVisible(false);
            ResultadosPane.setVisible(true);

            ListView<String> resultadosList = (ListView<String>) root.lookup("#resultadosList");
            resultadosList.getItems().clear();

            try {
                String concursos = new String(Files.readAllBytes(Paths.get(CONCURSOS_FILE)));
                JSONArray concursosArray = new JSONArray(concursos);

                for (int i = 0; i < concursosArray.length(); i++) {
                    JSONObject concurso = concursosArray.getJSONObject(i);
                    String resultadoInfo = "Concurso: " + concurso.getInt("id") + ", Data: " + concurso.getString("dataSorteio") + ", Números: " + concurso.getJSONArray("numerosSorteados").toString();
                    resultadosList.getItems().add(resultadoInfo);
                }

                String apostas = new String(Files.readAllBytes(Paths.get(APOSTAS_FILE)));
                JSONArray apostasArray = new JSONArray(apostas);

                for (int i = 0; i < apostasArray.length(); i++) {
                    JSONObject aposta = apostasArray.getJSONObject(i);
                    if (aposta.getString("apostador").equals(usuarioLogado)) {
                        int qtdeAcertos = aposta.getInt("qtdeAcertos");
                        if (qtdeAcertos >= 15) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText("Parabéns! Você ganhou com " + qtdeAcertos + " acertos.");
                            alert.setContentText("Valor ganho: " + aposta.getDouble("valorGanho"));
                            alert.show();
                        }
                    }
                }
            } catch (IOException | JSONException e2) {
                e2.printStackTrace();
            }
        });

        btnApostar.setOnAction(e -> {
            ApostarPane.setVisible(true);
            ApostasPane.setVisible(false);
            ResultadosPane.setVisible(false);
            PerfilPane.setVisible(false);

            JSONArray concursos = Autenticador.carregarConcursos();
            for (int i = 0; i < concursos.length(); i++) {
                JSONObject concurso = concursos.getJSONObject(i);
                if (concurso.getString("situacao").equals("Ativo")) {
                    idField.setText(String.valueOf(concurso.getInt("id")));
                    
                    StringBuilder dataSorteio = new StringBuilder();
                    dataSorteio.append(concurso.getString("dataSorteio"));
                    dataSorteio.append(" ");
                    dataSorteio.append(concurso.getString("horario"));
                    dataField.setText(dataSorteio.toString());
                    situacaoField.setText(concurso.getString("situacao"));
                }
            }

            escolherButton.setOnAction(e2 -> {
                try {
                    FXMLLoader escolherLoader = new FXMLLoader(getClass().getResource("/view/escolherNumerosTela.fxml"));
                    Parent escolherRoot = escolherLoader.load();
                    Stage escolherStage = new Stage();
                    escolherStage.setScene(new Scene(escolherRoot));
                    escolherStage.setTitle("Escolher Números");
                    escolherStage.show();
                    TextField qtdeSelecionados = (TextField) escolherRoot.lookup("#selecionadosField");
                    qtdeSelecionados.setText("0");
                    TextField custoField = (TextField) escolherRoot.lookup("#custoField");
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

                            if(Integer.parseInt(qtdeSelecionados.getText()) > 20) {
                                numeros[index].setSelected(false);
                                numeros[index].setStyle("");
                                qtdeSelecionados.setText(String.valueOf(Integer.parseInt(qtdeSelecionados.getText()) - 1));
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setHeaderText("Máximo de 20 números selecionados");
                                alert.show();
                            }

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
                                    custoAposta = "46.512,00";
                                    break;
                            }
                            custoField.setText(custoAposta);
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
                                    custoAposta = "46.512,00";
                                    break;
                            }
                            custoField.setText(custoAposta);
                            
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
                                custoAposta = "46.512,00";
                                break;
                        }

                        TextField custoField2 = (TextField) root.lookup("#custoField");
                        custoField2.setText(custoAposta);

                       
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
                aposta.setIdConcurso(Integer.parseInt(idField.getText()));
                aposta.setIdApostador(Integer.parseInt(idLogado));
                aposta.setValorPago(Double.parseDouble(((TextField) root.lookup("#custoField")).getText().replace(",", ".")));
                aposta.setDataCriacao(LocalDate.now());
                aposta.setNumerosSelecionados(new ArrayList<>());
                String numeros = numerosField.getText().replace("[", "").replace("]", "");
                for (String num : numeros.split(",")) {
                    aposta.getNumerosSelecionados().add(Integer.parseInt(num.trim()));
                }
                aposta.setQtdeAcertos(0);
                aposta.setValorGanho(0.0);
                
                Autenticador.salvarAposta(aposta);

                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setHeaderText("Aposta realizada com sucesso");
                alert2.show();
            });
        });

        btnMinhasApostas.setOnAction(e -> {
            ApostarPane.setVisible(false);
            ApostasPane.setVisible(true);
            ResultadosPane.setVisible(false);

            ListView<String> apostasList = (ListView<String>) root.lookup("#apostasList");
            apostasList.getItems().clear();

            try {
            String content = new String(Files.readAllBytes(Paths.get(APOSTAS_FILE)));
            JSONArray apostasArray = new JSONArray(content);

            for (int i = 0; i < apostasArray.length(); i++) {
                JSONObject aposta = apostasArray.getJSONObject(i);
                if (aposta.getString("apostador").equals(usuarioLogado)) {
                String apostaInfo = "Concurso: " + aposta.getInt("id") + ", Números: " + aposta.getJSONArray("numerosSelecionados").toString() + ", Data: " + aposta.getString("dataCriacao") + ", Valor Pago: " + aposta.getDouble("valorPago");
                apostasList.getItems().add(apostaInfo);
                }
            }
            } catch (IOException | JSONException e2) {
            e2.printStackTrace();
            }

            apostasList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedAposta = apostasList.getSelectionModel().getSelectedItem();
                if (selectedAposta != null) {
                try {
                    String content = new String(Files.readAllBytes(Paths.get(APOSTAS_FILE)));
                    JSONArray apostasArray = new JSONArray(content);

                    for (int i = 0; i < apostasArray.length(); i++) {
                    JSONObject aposta = apostasArray.getJSONObject(i);
                    if (selectedAposta.contains("Concurso: " + aposta.getInt("id"))) {
                        FXMLLoader editarLoader = new FXMLLoader(getClass().getResource("/view/editarApostaTela.fxml"));
                        Parent editarRoot = editarLoader.load();
                        Stage editarStage = new Stage();
                        editarStage.setScene(new Scene(editarRoot));
                        editarStage.setTitle("Editar Aposta");
                        editarStage.show();

                        TextField editarNumerosField = (TextField) editarRoot.lookup("#numerosField");
                        Button editarNumerosButton = (Button) editarRoot.lookup("#editarNumerosButton");
                        Button salvarButton = (Button) editarRoot.lookup("#salvarButton");
                        Button excluirButton = (Button) editarRoot.lookup("#excluirButton");

                        editarNumerosField.setText(aposta.getJSONArray("numerosSelecionados").toString());

                        editarNumerosButton.setOnAction(e2 -> {
                            if (aposta.getString("situacao").equals("Encerrado")) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setHeaderText("Concurso já realizado, impossivel editar");
                                alert.show();
                                return;
                            }
                        try {
                            FXMLLoader escolherLoader = new FXMLLoader(getClass().getResource("/view/escolherNumerosTela.fxml"));
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
                            for (int j = 0; j < 25; j++) {
                            numeros[j] = (ToggleButton) escolherRoot.lookup("#num" + (j + 1));
                            final int index = j;
                            numeros[index].setOnAction(e3 -> {
                                if (numeros[index].isSelected()) {
                                numeros[index].setStyle("-fx-background-color: #00ff00");
                                qtdeSelecionados.setText(String.valueOf(Integer.parseInt(qtdeSelecionados.getText()) + 1));
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
                                for (int k = 0; k < 25; k++) {
                                numeros[k].setSelected(false);
                                numeros[k].setStyle("");
                                }
                                List<Integer> selecionados = new ArrayList<>();
                                while (selecionados.size() < qtde) {
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
                            for (int k = 0; k < 25; k++) {
                                numeros[k].setSelected(false);
                                numeros[k].setStyle("");
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
                            for (int k = 0; k < 25; k++) {
                                if (numeros[k].isSelected()) {
                                selecionados.add(k + 1);
                                }
                            }
                            editarNumerosField.setText(selecionados.toString());
                            });

                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                        });

                        salvarButton.setOnAction(e2 -> {
                        try {
                            aposta.put("numerosSelecionados", new JSONArray(editarNumerosField.getText()));
                            Files.write(Paths.get(APOSTAS_FILE), apostasArray.toString(4).getBytes());

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText("Aposta atualizada com sucesso");
                            alert.show();
                            editarStage.close();
                        } catch (IOException | JSONException e3) {
                            e3.printStackTrace();
                        }
                        });

                        final int index = i;
                        excluirButton.setOnAction(e2 -> {
                        try {
                            apostasArray.remove(index);
                            Files.write(Paths.get(APOSTAS_FILE), apostasArray.toString(4).getBytes());

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText("Aposta excluída com sucesso");
                            alert.show();
                            editarStage.close();
                            apostasList.getItems().remove(selectedAposta);
                        } catch (IOException | JSONException e3) {
                            e3.printStackTrace();
                        }
                        });
                    }
                    }
                } catch (IOException | JSONException e1) {
                    e1.printStackTrace();
                }
                }
            }
            });
        });

        btnPerfil.setOnAction(e -> {
            ApostarPane.setVisible(false);
            ApostasPane.setVisible(false);
            ResultadosPane.setVisible(false);
            PerfilPane.setVisible(true);

            System.out.println(usuarioLogado);
            System.out.println(idLogado);

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

            Button salvarInfoButton = (Button) root.lookup("#salvarInfoButton");
            Button editarInfoButton = (Button) root.lookup("#editarInfoButton");
            Button cancelarButton = (Button) root.lookup("#cancelarButton");

            nomeField.setEditable(false);
            emailField.setEditable(false);
            generoField.setDisable(true);
            cpfField.setEditable(false);
            telefoneField.setEditable(false);
            ufField.setEditable(false);
            cidadeField.setEditable(false);
            bairroField.setEditable(false);
            cepField.setEditable(false);
            ruaField.setEditable(false);
            userField.setEditable(false);
            senhaField.setEditable(false);
            dataNascPicker.setDisable(true);

            salvarInfoButton.setVisible(false);
            cancelarButton.setVisible(false);

            try {
                String content = new String(Files.readAllBytes(Paths.get(USERS_FILE)));
                JSONArray usersArray = new JSONArray(content);

                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject usuario = usersArray.getJSONObject(i);
                    if (usuario.getInt("id") == Integer.parseInt(idLogado)) {
                        nomeField.setText(usuario.getString("nome"));
                        emailField.setText(usuario.getString("email"));
                        generoField.setValue(usuario.getString("genero"));
                        cpfField.setText(usuario.getString("cpf"));
                        telefoneField.setText(usuario.getString("telefone"));
                        dataNascPicker.setPromptText(usuario.getString("dataNascimento"));
                        JSONObject endereco = usuario.getJSONObject("endereco");
                        ufField.setText(endereco.getString("estado"));
                        cidadeField.setText(endereco.getString("cidade"));
                        bairroField.setText(endereco.getString("bairro"));
                        cepField.setText(String.valueOf(endereco.getInt("cep")));
                        ruaField.setText(endereco.getString("rua"));

                        userField.setText(usuario.getString("user"));
                        senhaField.setText(usuario.getString("senha"));

                    }
                }

            } catch (Exception e2) {
                // TODO: handle exception
            }

            editarInfoButton.setOnAction(e2 -> {
                nomeField.setEditable(true);
                emailField.setEditable(true);
                generoField.setDisable(false);
                cpfField.setEditable(true);
                telefoneField.setEditable(true);
                ufField.setEditable(true);
                cidadeField.setEditable(true);
                bairroField.setEditable(true);
                cepField.setEditable(true);
                ruaField.setEditable(true);
                userField.setEditable(true);
                senhaField.setEditable(true);
                dataNascPicker.setDisable(false);

                salvarInfoButton.setVisible(true);
                cancelarButton.setVisible(true);

                salvarInfoButton.setOnAction(e3 -> {
                    try {
                        String content = new String(Files.readAllBytes(Paths.get(USERS_FILE)));
                        JSONArray usersArray = new JSONArray(content);

                        for (int i = 0; i < usersArray.length(); i++) {
                            JSONObject usuario = usersArray.getJSONObject(i);
                            if (usuario.getInt("id") == Integer.parseInt(idLogado)) {
                                usuario.put("nome", nomeField.getText());
                                usuario.put("email", emailField.getText());
                                usuario.put("genero", generoField.getValue());
                                usuario.put("cpf", cpfField.getText());
                                usuario.put("telefone", telefoneField.getText());
                                if (dataNascPicker.getValue() != null) {
                                    usuario.put("dataNascimento", dataNascPicker.getValue().toString());
                                } else {
                                    usuario.put("dataNascimento", dataNascPicker.getPromptText());
                                }
                                JSONObject endereco = usuario.getJSONObject("endereco");
                                endereco.put("estado", ufField.getText());
                                endereco.put("cidade", cidadeField.getText());
                                endereco.put("bairro", bairroField.getText());
                                endereco.put("cep", Integer.parseInt(cepField.getText()));
                                endereco.put("rua", ruaField.getText());

                                usuario.put("user", userField.getText());
                                usuario.put("senha", senhaField.getText());
                            }
                        }

                        Files.write(Paths.get(USERS_FILE), usersArray.toString(4).getBytes());

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Informações salvas com sucesso");
                        alert.show();

                        salvarInfoButton.setVisible(false);
                        cancelarButton.setVisible(false);

                        nomeField.setEditable(false);
                        emailField.setEditable(false);
                        generoField.setDisable(true);
                        cpfField.setEditable(false);
                        telefoneField.setEditable(false);
                        ufField.setEditable(false);
                        cidadeField.setEditable(false);
                        bairroField.setEditable(false);
                        cepField.setEditable(false);
                        ruaField.setEditable(false);
                        userField.setEditable(false);
                        senhaField.setEditable(false);
                        dataNascPicker.setDisable(true);

                    } catch (Exception e4) {
                        e4.printStackTrace();
                    }

                });

                cancelarButton.setOnAction(e3 -> {
                    nomeField.setEditable(false);
                    emailField.setEditable(false);
                    generoField.setDisable(true);
                    cpfField.setEditable(false);
                    telefoneField.setEditable(false);
                    ufField.setEditable(false);
                    cidadeField.setEditable(false);
                    bairroField.setEditable(false);
                    cepField.setEditable(false);
                    ruaField.setEditable(false);
                    userField.setEditable(false);
                    senhaField.setEditable(false);
                    dataNascPicker.setDisable(true);

                    salvarInfoButton.setVisible(false);
                    cancelarButton.setVisible(false);

                    try {
                        String content = new String(Files.readAllBytes(Paths.get(USERS_FILE)));
                        JSONArray usersArray = new JSONArray(content);

                        for (int i = 0; i < usersArray.length(); i++) {
                            JSONObject usuario = usersArray.getJSONObject(i);
                            if (usuario.getInt("id") == Integer.parseInt(idLogado)) {
                                nomeField.setText(usuario.getString("nome"));
                                emailField.setText(usuario.getString("email"));
                                generoField.setValue(usuario.getString("genero"));
                                cpfField.setText(usuario.getString("cpf"));
                                telefoneField.setText(usuario.getString("telefone"));
                                dataNascPicker.setPromptText(usuario.getString("dataNascimento"));

                                JSONObject endereco = usuario.getJSONObject("endereco");
                                ufField.setText(endereco.getString("estado"));
                                cidadeField.setText(endereco.getString("cidade"));
                                bairroField.setText(endereco.getString("bairro"));
                                cepField.setText(String.valueOf(endereco.getInt("cep")));
                                ruaField.setText(endereco.getString("rua"));

                                userField.setText(usuario.getString("user"));
                                senhaField.setText(usuario.getString("senha"));

                            }
                        }

                    } catch (Exception e6) {
                        // TODO: handle exception
                    }

                });

            });

        });

        sairButton.setOnAction(e -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("Sim", "Sim", "Não");
            dialog.setTitle("Sair");
            dialog.setHeaderText("Deseja realmente sair?");
            dialog.setContentText("Escolha uma opção:");

            dialog.showAndWait().ifPresent(opcao -> {
                if (opcao.equals("Sim")) {
                    LoginController login = new LoginController();
                    try {
                        login.start(tela);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
        });

    }

}