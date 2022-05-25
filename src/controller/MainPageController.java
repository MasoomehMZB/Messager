package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {

    @FXML
    private Button CreateBTN;
    @FXML
    private ListView<String> UsersListLVW;

    @FXML
    private ListView<String> RequestRListLVW;

    @FXML
    private ListView<String> RequestSListLVW;;

    @FXML
    private TextField SearchTFD;

    @FXML
    private MenuItem BlockMIM;

    @FXML
    private HBox SearchHBX;

    @FXML
    private Button AddBTN;

    @FXML
    private Text FoundUserTXT;

    @FXML
    private MenuItem NewGroupMIM;

    @FXML
    private Button SearchBTN;

    @FXML
    private TextField GroupNameTFD;

    @FXML
    private Pane GroupPAN;

    @FXML
    private TextField GroupLinkTFD;

    @FXML
    private Text errorTXT;

    @FXML
    private ListView<String> GroupsListLVW;

    private Stage MainPageStage;

    private ArrayList<Person> Users = new ArrayList<>();

    private  ArrayList<Relationships> Friends = new ArrayList<>();

    private  ArrayList<Relationships> Blocked = new ArrayList<>();

    private  ArrayList<Relationships> Pending = new ArrayList<>();

    private  ArrayList<Relationships> ReceivedReq = new ArrayList<>();

    private  ArrayList<Group_info> Groups = new ArrayList<>();
    private int index;

    private int groupChatRoom;

    private int i;

    private String GroupName;

    private String GroupLink;

    private int userChatRoom;

    private int friendChatRoom;
    JDBC jdbc = new JDBC();

    public void initFunction(Stage mainPageStage ,ArrayList<Person> users , int index ) {
        this.MainPageStage = mainPageStage;
        this.index = index;
        this.Users = users;


        Friends = jdbc.GetRelations(Users.get(index).getUserName() , 1);
        Friends.addAll(jdbc.GetReceivedRelations(Users.get(index).getUserName() , 1));
        for (Relationships friend : Friends ) {
            UsersListLVW.getItems().add(friend.getUsername());

            jdbc.SetChatID(Users.get(index).getUserName() ,friend.getUsername());

            jdbc.SetChatID(friend.getUsername() ,Users.get(index).getUserName());
        }


        Blocked = jdbc.GetRelations(Users.get(index).getUserName() , -1);
        Blocked.addAll(jdbc.GetReceivedRelations(Users.get(index).getUserName() , -1));


        Pending = jdbc.GetRelations(Users.get(index).getUserName() , 0);
        for (Relationships pending : Pending ) {
            RequestSListLVW.getItems().add(pending.getUsername());
        }


        ReceivedReq = jdbc.GetReceivedRelations(Users.get(index).getUserName() , 0);
        for (Relationships receivedReq : ReceivedReq) {
            RequestRListLVW.getItems().add(receivedReq.getUsername());
        }


        Groups = jdbc.ReadFromGroup(Users.get(index).getUserName());
        for (Group_info groupInfo : Groups ) {

            GroupsListLVW.getItems().add(groupInfo.getName());

            jdbc.SetChatID(groupInfo.getAdmin() ,groupInfo.getName());
        }
    }


    @FXML
    public void BlockHandler (ActionEvent event){

        String username = UsersListLVW.getSelectionModel().getSelectedItem();
        jdbc.Block(Users.get(index).getUserName() , username);
        UsersListLVW.getItems().remove(username);
    }
    @FXML
    public void SearchHandler(ActionEvent event){

        SearchHBX.setVisible(false);

        if (!SearchTFD.getText().isEmpty()){
            String searchedUser = SearchTFD.getText();
            try{
                i = searchForUsername(searchedUser);
                if (Users.get(i).getUserName().equals(searchedUser)){

                    SearchHBX.setVisible(true);
                    FoundUserTXT.setText(Users.get(i).getUserName());
                    EventHandler<ActionEvent> addHandler = new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            System.out.println("Add the friend");
                            jdbc.sendFriendRequest(Users.get(index).getUserName() , Users.get(i).getUserName() , 0);
                            Pending.add(new Relationships(0,Users.get(i).getUserName()));
                            RequestSListLVW.getItems().add(Users.get(i).getUserName());
                        }
                    };
                    AddBTN.setOnAction(addHandler);
                }
            }catch (UserNotFoundException e){
                e.printStackTrace();
            }
        }
    }
    public int searchForUsername(String searchedUser) throws UserNotFoundException {
        for (int i = 0 ; i < Users.size() ; i++) {
            if (Users.get(i).getUserName().equals(searchedUser)){
                return i;
            }
        }
        throw new UserNotFoundException("This username doesn't exist");
    }

    @FXML
    public void setNewGroup(ActionEvent event){
        GroupPAN.setVisible(true);
        Group_info group_info = new Group_info();
        CreateBTN.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!GroupLinkTFD.getText().isEmpty() && !GroupNameTFD.getText().isEmpty()){
                    if (group_info.LinkValidation(GroupLinkTFD.getText())){

                        GroupPAN.setVisible(false);

                        Group_info group_info = new Group_info(GroupNameTFD.getText(),GroupLinkTFD.getText(),"empty"
                                , Users.get(index).getUserName(),0 ,groupChatRoom);

                        jdbc.InsertIntoGroup(group_info);

                        jdbc.SetChatID(Users.get(index).getUserName() ,GroupNameTFD.getText());

                        GroupPAN.setVisible(false);

                        GroupsListLVW.getItems().add(GroupNameTFD.getText());

                        GroupLinkTFD.clear();
                        GroupNameTFD.clear();

                        Groups.add(group_info);
                    }
                    else{
                        errorTXT.setText("Your link should start with @ and \ncan't have space in it.");
                    }
                }
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // We need to create a new CellFactory so we can display our layout for each individual user
        RequestRListLVW.setCellFactory((Callback<ListView<String>, ListCell<String>>) param -> {
            return new ListCell<String>() {
                @Override
                protected void updateItem(String username, boolean empty) {
                    super.updateItem(username, empty);

                    if (username == null || empty) {
                        setText(null);
                    } else {
                        // Here we can build the layout we want for each ListCell. Let's use a HBox as our root.
                        HBox root = new HBox(10);
                        root.setAlignment(Pos.CENTER_LEFT);
                        root.setPadding(new Insets(5, 5, 5, 5));

                        // Within the root, we'll show the username on the left and our two buttons to the right
                        root.getChildren().add(new Label(username));

                        // I'll add another Region here to expand, pushing the buttons to the right
                        Region region = new Region();
                        HBox.setHgrow(region, Priority.ALWAYS);
                        root.getChildren().add(region);

                        // Now for our buttons
                        Button btnAddFriend = new Button("Accept");

                        // Code to add friend
                        btnAddFriend.setOnAction(event -> {

                            jdbc.AcceptFriendRequest(username , Users.get(index).getUserName());

                            int index = RequestRListLVW.getSelectionModel().getSelectedIndex();
                            RequestRListLVW.getItems().remove(index + 1);

                            jdbc.SetChatID(Users.get(index).getUserName() ,username);

                            jdbc.SetChatID(username,Users.get(index).getUserName());

                            for (int k = 0 ; k < ReceivedReq.size() ; k++){
                                if (username.equals(ReceivedReq.get(k).getUsername())){
                                    Friends.add(ReceivedReq.get(k));
                                    ReceivedReq.remove(k);
                                    break;
                                }
                            }

                            UsersListLVW.getItems().add(username);

                        });
                        Button btnDecline = new Button("Decline");

                        btnDecline.setOnAction(event -> {
                            // Code to remove friend
                            System.out.println("Broke up with " + username + "!");

                            jdbc.DeclineFriendRequest(username , Users.get(index).getUserName());

                            int index = RequestRListLVW.getSelectionModel().getSelectedIndex();
                            RequestRListLVW.getItems().remove(index + 1);
                            for (int k = 0 ; k < ReceivedReq.size() ; k++){
                                if (username.equals(ReceivedReq.get(k).getUsername())){
                                    ReceivedReq.remove(k);
                                    break;
                                }
                            }

                        });
                        root.getChildren().addAll(btnAddFriend, btnDecline);

                        // Finally, set our cell to display the root HBox
                        setText(null);
                        setGraphic(root);
                    }

                }
            };

        });

        UsersListLVW.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                String friend = UsersListLVW.getSelectionModel().getSelectedItem();

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("..\\view\\ChatRoomView.fxml"));
                try {
                    fxmlLoader.load();
                }
                catch (IOException e){
                    e.printStackTrace();
                }

                friendChatRoom = jdbc.GetChatID( friend , Users.get(index).getUserName() );
                userChatRoom = jdbc.GetChatID(Users.get(index).getUserName() , friend);

                ChatRoomController controller = fxmlLoader.getController();

                controller.initFunction(MainPageStage, Users.get(index).getUserName() , friend ,
                        userChatRoom ,friendChatRoom, Users , index ,ChatRoomType.PERSONAL) ;

                Scene scene = new Scene(fxmlLoader.getRoot());

                MainPageStage.setScene(scene);

                scene.getStylesheets().add(getClass().getResource("..\\view\\StyleSheet.css").toExternalForm());
            }
        });
        GroupsListLVW.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                String groupName = GroupsListLVW.getSelectionModel().getSelectedItem();

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("..\\view\\ChatRoomView.fxml"));
                try {
                    fxmlLoader.load();
                }
                catch (IOException e){
                    e.printStackTrace();
                }

                int k;
                for (k = 0 ; k < Groups.size() ; k++){
                    if (Groups.get(k).getName().equals(groupName))
                    {
                        break;
                    }
                }
                groupChatRoom = jdbc.GetChatID(Groups.get(k).getAdmin(),groupName);

                ChatRoomController controller = fxmlLoader.getController();

                controller.initFunction(MainPageStage, Users.get(index).getUserName() , groupName ,
                        groupChatRoom,groupChatRoom, Users , index , ChatRoomType.GROUP) ;

                Scene scene = new Scene(fxmlLoader.getRoot());

                MainPageStage.setScene(scene);
                MainPageStage.setResizable(false);

                scene.getStylesheets().add(getClass().getResource("..\\view\\StyleSheet.css").toExternalForm());
            }
        });

        SearchBTN.setOnAction(this::SearchHandler);
        BlockMIM.setOnAction(this::BlockHandler);
        NewGroupMIM.setOnAction(this::setNewGroup);
    }
}
