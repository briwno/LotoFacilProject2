import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegistrarController extends Application {
    private Autenticador autenticador = new Autenticador();

    @Override
    public void start(Stage tela) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/layoutRegistrar.fxml"));
        Scene scene = new Scene(root);
        tela.setScene(scene);
        tela.setTitle("Registro de Usuário");
        tela.show();

        TextField nomeField = (TextField) root.lookup("#nomeField");
        TextField emailField = (TextField) root.lookup("#emailField");
        TextField cpfField = (TextField) root.lookup("#cpfField");
        TextField telefoneField = (TextField) root.lookup("#telefoneField");
        TextField ufField = (TextField) root.lookup("#ufField");
        TextField cidadeField = (TextField) root.lookup("#cidadeField");
        TextField cepField = (TextField) root.lookup("#cepField");
        TextField ruaField = (TextField) root.lookup("#ruaField");
        TextField numeroField = (TextField) root.lookup("#numeroField");
        TextField userField = (TextField) root.lookup("#userField");
        TextField senhaField = (TextField) root.lookup("#senhaField");
        ComboBox<String> generoField = (ComboBox<String>) root.lookup("#generoField");
        DatePicker dataField = (DatePicker) root.lookup("#dataField");
        CheckBox termosCheck = (CheckBox) root.lookup("#termosCheck");
        Button registrarButton = (Button) root.lookup("#registrarButton");

        

    }

    

}