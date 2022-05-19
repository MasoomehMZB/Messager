import controller.StartPageController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("\\view\\StartPageView.fxml"));
        try {
            fxmlLoader.load();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        StartPageController controller = fxmlLoader.getController();
        controller.initFunction(primaryStage);

        Scene scene = new Scene(fxmlLoader.getRoot());
        primaryStage.setScene(scene);
        Image icon = new Image("img/icons8-messaging-100.png");
        primaryStage.getIcons().add(icon);
        scene.getStylesheets().add(getClass().getResource("\\view\\StyleSheet.css").toExternalForm());
        primaryStage.show();
    }
}