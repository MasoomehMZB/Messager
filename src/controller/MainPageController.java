package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Person;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {

    @FXML
    private MenuItem NewGroupMIM;

    @FXML
    private ListView<?> UsersListLVW;

    @FXML
    private TextField SearchTFD;

    @FXML
    private MenuItem BlockMIM;

    @FXML
    private Button SearchBTN;

    @FXML
    private MenuButton MenuButtonMBN;

    private Stage MainPageStage;

    private ArrayList<Person> Users = new ArrayList<>();

    private int index;

    public void initFunction(Stage mainPageStage ,ArrayList<Person> users , int index ) {
        this.MainPageStage = mainPageStage;
        this.index = index;
        this.Users = users;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
