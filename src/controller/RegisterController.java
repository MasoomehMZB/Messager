package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.JDBC;
import model.Person;
import model.SendMail;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private Text MessageTXT;

    @FXML
    private Button BackBTN;

    @FXML
    private Text UMessageTXT;

    @FXML
    private PasswordField PasswordPFD;

    @FXML
    private Button RegisterBTN;

    @FXML
    private TextField UsernameTFD;

    @FXML
    private Text PMessageTXT;

    @FXML
    private TextField EmailTFD;

    @FXML
    private TextField VCodeTFD;

    private Stage RegisterStage;

    private String VCode;

    public Person getPerson() {
        return person;
    }

    private Person person;
    public void initFunction(Stage registerStage){
        VCodeTFD.setVisible(false);
        this.RegisterStage = registerStage;}

    @FXML
    void BackHandler (ActionEvent event) {
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
        controller.initFunction(RegisterStage);

        Scene scene = new Scene(loader.getRoot());
        RegisterStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("..\\view\\StyleSheet.css").toExternalForm());
    }

    @FXML
    public void RegisterHandler(ActionEvent event){

        clearTextFields();

        JDBC jdbc = new JDBC();

        boolean valid = true;

        person = new Person();

        if (Person.UsernamePasswordValidation(UsernameTFD.getText())){
            if (jdbc.checkExistenceUser("username" , UsernameTFD.getText())){
                UMessageTXT.setText("This username already exists");
                valid = false;
                UsernameTFD.clear();
            }
            else{
                person.setUserName(UsernameTFD.getText());
            }
        }
        else{
            UMessageTXT.setText("Your username should only consist of upper and lower case letters and numbers.");
            valid = false;
            UsernameTFD.clear();
        }


        if (Person.UsernamePasswordValidation(PasswordPFD.getText())){
            person.setPasswordHash(PasswordPFD.getText());
        }
        else{
            PMessageTXT.setText("Your password should only consist of upper and lower case letters and numbers.");
            valid = false;
            UsernameTFD.clear();
        }


        if (Person.EmailValidation(EmailTFD.getText())){
            if (jdbc.checkExistenceUser("email" , EmailTFD.getText())){
                MessageTXT.setText("This username already exists");
                valid = false;
                EmailTFD.clear();
            }
            else {
                person.setEmail(EmailTFD.getText());
            }
        }
        else{
            MessageTXT.setText("Invalid Email");
            valid = false;
            System.out.println(UMessageTXT.getText());
            EmailTFD.clear();
        }
        try{
            if (valid && !VCode.isEmpty()){
                if ( VCodeTFD.getText().equals(VCode)){
                    jdbc.InsertIntoTB(person);

                    ArrayList<Person> Users = jdbc.ReadIntoArrayList();

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
                    controller.initFunction(RegisterStage , Users  , Users.size()-1);

                    Scene scene = new Scene(loader.getRoot());
                    RegisterStage.setScene(scene);
                    scene.getStylesheets().add(getClass().getResource("..\\view\\StyleSheet.css").toExternalForm());

                }
                else if (!VCodeTFD.getText().isEmpty() && !VCodeTFD.getText().equals(VCode)){
                    MessageTXT.setText("Wrong code. Try again.");
                }
            }
        }catch (NullPointerException e){
            if ( valid ) {
                Random random = new Random();
                VCode = String.format("%04d", random.nextInt(10000));
                System.out.println(VCode);
                SendMail sendMail = new SendMail(person.getEmail());
                sendMail.Send(VCode);
                MessageTXT.setText("The verification code has been sent to your email.");
                VCodeTFD.setVisible(true);
            }
        }
    }

    public void clearTextFields(){
        UMessageTXT.setText("");
        PMessageTXT.setText("");
        MessageTXT.setText("");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BackBTN.setOnAction(this::BackHandler);
        RegisterBTN.setOnAction(this::RegisterHandler);
    }
}
