import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("\\view\\LoginRegisterPageView.fxml"));
        fxmlLoader.load();
        Scene scene = new Scene(fxmlLoader.getRoot());
        primaryStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("\\view\\LoginRegisterView.css").toExternalForm());
        primaryStage.show();

    }
}