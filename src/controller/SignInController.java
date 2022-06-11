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
import model.UserNotFoundException;

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
    public void LoginHandler(ActionEvent event) {
        clearTexts();
        JDBC jdbc = new JDBC();
        Users = jdbc.ReadIntoArrayList();

        int i;
        //searching for the input username
        i = searchForUsername();
        Person person = new Person();
        //getting password's hash code
        String passwordHash = person.setPasswordHash(PasswordPFD.getText());
        if ( i != -1 ){
            if (Users.get(i).getPasswordHash().equals(passwordHash)) {

                //going to the main page
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("..\\view\\MainPageView.fxml"));
                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MainPageController controller = loader.getController();

                controller.initFunction(Sign_inStage, Users, i);

                Scene scene = new Scene(loader.getRoot());
                Sign_inStage.setScene(scene);
                scene.getStylesheets().add(getClass().getResource("..\\view\\StyleSheet.css").toExternalForm());

            } else {
                PMessageTXT.setText("Wrong Password.");
            }
        }
        else{
            UMessageTXT.setText("Make sure the username field is filled and username is correct.");
        }
    }

    //searches for username and if it exists returns the username's index in arraylist
    //else returns -1
    public int searchForUsername(){
        for (int i = 0 ; i < Users.size() ; i++) {
            if ( !UsernameTFD.getText().isEmpty() && Users.get(i).getUserName().equals(UsernameTFD.getText())){
                return i ;
            }
        }
        return -1;
    }

    //clearing all texts
    public void clearTexts(){
        UMessageTXT.setText("");
        PMessageTXT.setText("");
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        BackBTN.setOnAction(this::BackHandler);
        LoginBTN.setOnAction(this::LoginHandler);
    }
}
