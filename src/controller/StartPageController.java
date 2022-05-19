package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StartPageController implements Initializable {


    @FXML
    private Button RegisterBTN;

    @FXML
    private Button LoginBTN;

    private Stage StartPageStage;

    public void initFunction (Stage startPageStage){
        this.StartPageStage = startPageStage;
    }

    @FXML
    public void RegisterHandler(ActionEvent event){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("..\\view\\RegisterView.fxml"));
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RegisterController controller = fxmlLoader.getController();
        controller.initFunction(StartPageStage);

        Scene scene = new Scene(fxmlLoader.getRoot());
        StartPageStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("..\\view\\StyleSheet.css").toExternalForm());
    }

    @FXML
    public void SignInHandler(ActionEvent event)
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("..\\view\\SigninView.fxml"));
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SignInController controller = fxmlLoader.getController();
        controller.initFunction(StartPageStage);

        Scene scene = new Scene(fxmlLoader.getRoot());
        StartPageStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("..\\view\\StyleSheet.css").toExternalForm());
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        RegisterBTN.setOnAction(this::RegisterHandler);
        LoginBTN.setOnAction(this::SignInHandler);
    }
}
