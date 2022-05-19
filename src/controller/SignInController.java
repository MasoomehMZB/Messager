package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.JDBC;
import model.Person;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SignInController implements Initializable {

    @FXML
    private Button BackBTN;

    @FXML
    private Text UMessageTXT;

    @FXML
    private PasswordField PasswordPFD;

    @FXML
    private Button LoginBTN;

    @FXML
    private TextField UsernameTFD;

    @FXML
    private Text PMessageTXT;

    public ArrayList<Person> getUsers() {
        return Users;
    }

    private ArrayList<Person> Users = new ArrayList<>();

    private Stage Sign_inStage;

    public void initFunction(Stage sign_inStage){
        this.Sign_inStage = sign_inStage;
    }

    @FXML
    public void BackHandler(ActionEvent event){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("..\\view\\StartPageView.fxml"));
        try{
            loader.load();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        StartPageController controller = loader.getController();
        controller.initFunction(Sign_inStage);

        Scene scene = new Scene(loader.getRoot());
        Sign_inStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("..\\view\\StyleSheet.css").toExternalForm());
    }

    @FXML
    public void LoginHandler(ActionEvent event){
        clearTextFields();
        JDBC jdbc = new JDBC();
        Users = jdbc.ReadIntoArrayList();

        for (int i = 0 ; i < Users.size() ; i++) {

            System.out.println(Users.get(i).getPasswordHash());

            if ( !UsernameTFD.getText().isEmpty() && Users.get(i).getUserName().equals(UsernameTFD.getText())) {
                Person person = new Person();
                String passwordHash = person.setPasswordHash(PasswordPFD.getText());
                if (Users.get(i).getPasswordHash().equals(passwordHash)) {

                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("..\\view\\MainPageView.fxml"));
                    try{
                        loader.load();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    MainPageController controller = loader.getController();
                    controller.initFunction(Sign_inStage , Users , i);

                    Scene scene = new Scene(loader.getRoot());
                    Sign_inStage.setScene(scene);
                    scene.getStylesheets().add(getClass().getResource("..\\view\\StyleSheet.css").toExternalForm());

                } else {
                    PMessageTXT.setText("Wrong Password.");
                }
            } else {
                UMessageTXT.setText("Please make sure the fields aren't empty and the username is correct.");
            }
        }

    }

    public void clearTextFields(){
        UMessageTXT.setText("");
        PMessageTXT.setText("");
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        BackBTN.setOnAction(this::BackHandler);
        LoginBTN.setOnAction(this::LoginHandler);
    }
}
