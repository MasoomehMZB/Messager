package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
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
    private Menu BlockMNU;
    @FXML
    private Button CreateBTN;
    @FXML
    private ListView<String> UsersListLVW;

    @FXML
    private ListView<String> RequestRListLVW;

    @FXML
    private ListView<String> RequestSListLVW;

    @FXML
    private ListView<String> BlockedListLVW;

    @FXML
    private TextField SearchTFD;

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
    private Label announceLBL;

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

    private int userChatRoom;

    private int friendChatRoom;

    JDBC jdbc = new JDBC();

    //getting the stage arraylist of users and index of entered user
    public void initFunction(Stage mainPageStage ,ArrayList<Person> users , int index ) {
        this.MainPageStage = mainPageStage;
        this.index = index;
        this.Users = users;

        //getting the friends list, setting chat room id for them and adding them to list view
        Friends = jdbc.GetRelations(Users.get(index).getUserName() , 1);
        Friends.addAll(jdbc.GetReceivedRelations(Users.get(index).getUserName() , 1));
        for (Relationships friend : Friends ) {
            UsersListLVW.getItems().add(friend.getUsername());

            jdbc.SetChatID(Users.get(index).getUserName() ,friend.getUsername());
            jdbc.SetChatID(friend.getUsername() ,Users.get(index).getUserName());
        }

        //getting blocked people list and adding them to list view
        Blocked = jdbc.GetRelations(Users.get(index).getUserName() , -1);
        Blocked.addAll(jdbc.GetReceivedRelations(Users.get(index).getUserName() , -1));
        for (Relationships blocked : Blocked ) {
            BlockedListLVW.getItems().add(blocked.getUsername());
        }

        //getting sent requests list and adding them to list view
        Pending = jdbc.GetRelations(Users.get(index).getUserName() , 0);
        for (Relationships pending : Pending ) {
            RequestSListLVW.getItems().add(pending.getUsername());
        }

        //getting received requests list
        ReceivedReq = jdbc.GetReceivedRelations(Users.get(index).getUserName() , 0);
        for (Relationships receivedReq : ReceivedReq) {
            RequestRListLVW.getItems().add(receivedReq.getUsername());
        }

        //getting groups list and setting chat id for them and adding them to list view
        Groups = jdbc.ReadFromGroup(Users.get(index).getUserName());
        for (Group_info groupInfo : Groups ) {
            GroupsListLVW.getItems().add(groupInfo.getName());
            jdbc.SetChatID(groupInfo.getAdmin() ,groupInfo.getName());
        }

        //adding friends as block menu items
        for (int i = 0 ; i < Friends.size() ; i++ ) {
            MenuItem person = new MenuItem(Friends.get(i).getUsername());
            BlockMNU.getItems().add(person);
        }
    }


    //making a menu of friends to choose and block
    @FXML
    public void BlockHandler (ActionEvent event){

        for (MenuItem item : BlockMNU.getItems()) {
            item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    jdbc.Block(Users.get(index).getUserName(), item.getText());
                    UsersListLVW.getItems().remove(item.getText());
                    BlockedListLVW.getItems().add(item.getText());
                    BlockMNU.getItems().remove(item);// this would do your thing you want to do.
                    event.consume();
                }
            });
        }
    }

    //for searching usernames
    @FXML
    public void SearchHandler(ActionEvent event){

        SearchHBX.setVisible(false);

        if (!SearchTFD.getText().isEmpty()){
            String searchedUser = SearchTFD.getText();
            try{
                //getting index if user is found
                i = searchForUsername(searchedUser);
                int relation = 10;
                if (Users.get(i).getUserName().equals(searchedUser)){

                    //showing the found user in h_box
                    SearchHBX.setVisible(true);
                    FoundUserTXT.setText(Users.get(i).getUserName());

                    //getting the int value of relationship and acting on it
                    int connection = getRelation(searchedUser);
                    switch (connection) {
                        case 1:{
                            announceLBL.setText("Already friends");
                            AddBTN.setVisible(false);
                            AddBTN.setDisable(true);
                            break;
                        }
                        case 0:{
                            announceLBL.setText("Requested");
                            AddBTN.setVisible(false);
                            AddBTN.setDisable(true);
                            break;
                        }
                        case 2:{
                            announceLBL.setText("Sent request");
                            AddBTN.setVisible(false);
                            AddBTN.setDisable(true);
                            break;
                        }
                        case -1: {
                            announceLBL.setText("Blocked");
                            AddBTN.setVisible(false);
                            AddBTN.setDisable(true);
                            break;
                        }
                        //if there's no relation between the two user
                        case -10: {
                            AddBTN.setVisible(true);
                            AddBTN.setDisable(false);
                            announceLBL.setText("");
                            //sending friend req
                            EventHandler<ActionEvent> addHandler = new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    jdbc.sendFriendRequest(Users.get(index).getUserName() , Users.get(i).getUserName() , 0);
                                    Pending.add(new Relationships(0,Users.get(i).getUserName()));
                                    RequestSListLVW.getItems().add(Users.get(i).getUserName());
                                }
                            };
                            AddBTN.setOnAction(addHandler);
                            break;
                        }
                        }
                    }


            }catch (UserNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    //finding the int value of relationships
    public int getRelation(String username){
        for (int i = 0 ; i < Friends.size() ; i++){
            if(Friends.get(i).getUsername().equals(username)){
                return 1;
            }
        }
        for (int i = 0 ; i < ReceivedReq.size() ; i++) {
            if(ReceivedReq.get(i).getUsername().equals(username)){
                return 0;
            }
        }
        for (int i = 0 ; i < Pending.size() ; i++) {
            if(Pending.get(i).getUsername().equals(username)){
                return 2;
            }
        }
        for (int i = 0 ; i < Blocked.size() ; i++) {
            if(Blocked.get(i).getUsername().equals(username)){
                return -1;
            }
        }
        return -10;
    }

    //this method searches for username on users arraylist and if user is not
    //found throws an exception
    public int searchForUsername(String searchedUser) throws UserNotFoundException {
        for (int i = 0 ; i < Users.size() ; i++) {
            if (Users.get(i).getUserName().equals(searchedUser)){
                return i;
            }
        }
        throw new UserNotFoundException("This username doesn't exist");
    }

    //making new group
    @FXML
    public void setNewGroup(ActionEvent event){
        GroupPAN.setVisible(true);
        Group_info group_info = new Group_info();
        CreateBTN.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //getting the name and link for new group and validating link
                if(!GroupLinkTFD.getText().isEmpty() && !GroupNameTFD.getText().isEmpty()){
                    if (group_info.LinkValidation(GroupLinkTFD.getText())){

                        GroupPAN.setVisible(false);

                        Group_info group_info = new Group_info(GroupNameTFD.getText(),GroupLinkTFD.getText(),Users.get(index).getUserName()
                                , Users.get(index).getUserName(),0 ,groupChatRoom);

                        //storing new group data and setting chatroom id for it
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

        //for accepting or declining friend request
        //(code owner)creating a new CellFactory so we can display our layout for each individual user
        RequestRListLVW.setCellFactory((Callback<ListView<String>, ListCell<String>>) param -> {
            return new ListCell<String>() {
                @Override
                protected void updateItem(String username, boolean empty) {
                    super.updateItem(username, empty);

                    if (username == null || empty) {
                        setText(null);
                    }
                    else {
                        // (code owner)Here we can build the layout we want for each ListCell. Let's use a HBox as our root.
                        HBox root = new HBox(10);
                        root.setAlignment(Pos.CENTER_LEFT);
                        root.setPadding(new Insets(5, 5, 5, 5));

                        //(code owner) Within the root, we'll show the username on the left and our two buttons to the right
                        root.getChildren().add(new Label(username));

                        // (code owner)I'll add another Region here to expand, pushing the buttons to the right
                        Region region = new Region();
                        HBox.setHgrow(region, Priority.ALWAYS);
                        root.getChildren().add(region);

                        //(code owner) Now for our buttons
                        Button btnAddFriend = new Button("Accept");

                        // (code owner)Code to add friend
                        //storing the user as new friend and adding them to friends list
                        btnAddFriend.setOnAction(event -> {

                            jdbc.AcceptFriendRequest(username , Users.get(index).getUserName());

                            int selectedIndex = RequestRListLVW.getSelectionModel().getSelectedIndex();
                            RequestRListLVW.getItems().remove(selectedIndex + 1);

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

                        //decline friend requests
                        //removing them from database and received requests list
                        btnDecline.setOnAction(event -> {

                            jdbc.DeclineFriendRequest(username , Users.get(index).getUserName());

                            int index = RequestRListLVW.getSelectionModel().getSelectedIndex();
                            RequestRListLVW.getItems().remove(index);
                            for (int k = 0 ; k < ReceivedReq.size() ; k++){
                                if (username.equals(ReceivedReq.get(k).getUsername())){
                                    ReceivedReq.remove(k);
                                    break;
                                }
                            }

                        });
                        root.getChildren().addAll(btnAddFriend, btnDecline);

                        // (code owner)Finally, set our cell to display the root HBox
                        setText(null);
                        setGraphic(root);
                    }

                }
            };

        });

        //adding listener to users/friends list view
        UsersListLVW.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                //going to the chat room of the selected person
                String friend = UsersListLVW.getSelectionModel().getSelectedItem();

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("..\\view\\ChatRoomView.fxml"));
                try {
                    fxmlLoader.load();
                }
                catch (IOException e){
                    e.printStackTrace();
                }

                //getting chatroom ids
                friendChatRoom = jdbc.GetChatID( friend , Users.get(index).getUserName() );
                userChatRoom = jdbc.GetChatID(Users.get(index).getUserName() , friend);

                ChatRoomController controller = fxmlLoader.getController();

                controller.initFunction(MainPageStage, Users.get(index).getUserName() , friend ,
                        userChatRoom ,friendChatRoom, Users , index ,ChatRoomType.PERSONAL) ;

                Scene scene = new Scene(fxmlLoader.getRoot());

                MainPageStage.setScene(scene);

                controller.setup();

                scene.getStylesheets().add(getClass().getResource("..\\view\\StyleSheet.css").toExternalForm());
            }
        });
        //adding change listener to users/friends list view
        GroupsListLVW.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                //going to the chat room of the selected group
                String groupName = GroupsListLVW.getSelectionModel().getSelectedItem();

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("..\\view\\ChatRoomView.fxml"));
                try {
                    fxmlLoader.load();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                //getting the index of the selected item in the array list
                int k;
                for (k = 0 ; k < Groups.size() ; k++){
                    if (Groups.get(k).getName().equals(groupName))
                    {
                        break;
                    }
                }
                //getting chatroom id
                groupChatRoom = jdbc.GetChatID(Groups.get(k).getAdmin(),groupName);

                ChatRoomController controller = fxmlLoader.getController();

                controller.initFunction(MainPageStage, Users.get(index).getUserName() , groupName ,
                        groupChatRoom,groupChatRoom, Users , index , ChatRoomType.GROUP) ;

                Scene scene = new Scene(fxmlLoader.getRoot());

                MainPageStage.setScene(scene);

                MainPageStage.setResizable(false);

                controller.setup();

                scene.getStylesheets().add(getClass().getResource("..\\view\\StyleSheet.css").toExternalForm());
            }
        });

        BlockedListLVW.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                //going to the chat room of the selected person
                String blocked = BlockedListLVW.getSelectionModel().getSelectedItem();

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("..\\view\\ChatRoomView.fxml"));
                try {
                    fxmlLoader.load();
                }
                catch (IOException e){
                    e.printStackTrace();
                }

                //getting chatroom ids
                friendChatRoom = jdbc.GetChatID( blocked , Users.get(index).getUserName() );
                userChatRoom = jdbc.GetChatID(Users.get(index).getUserName() , blocked);

                ChatRoomController controller = fxmlLoader.getController();

                controller.initFunction(MainPageStage, Users.get(index).getUserName() , blocked ,
                        userChatRoom ,friendChatRoom, Users , index ,ChatRoomType.BLOCKED) ;

                Scene scene = new Scene(fxmlLoader.getRoot());

                MainPageStage.setScene(scene);

                controller.setup();

                scene.getStylesheets().add(getClass().getResource("..\\view\\StyleSheet.css").toExternalForm());
            }
        });

        //set on actions
        SearchBTN.setOnAction(this::SearchHandler);
        //BlockMIM.setOnAction(this::BlockHandler);
        BlockMNU.setOnAction(this::BlockHandler);
        NewGroupMIM.setOnAction(this::setNewGroup);
    }
}
