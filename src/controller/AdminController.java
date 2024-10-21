import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;

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
        });

        verBotao.setOnAction(e -> {
            painelCriar.setVisible(false);
            painelVer.setVisible(true);
        });

    }
}
