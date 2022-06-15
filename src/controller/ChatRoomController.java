package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import model.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class ChatRoomController implements Initializable {

    @FXML
    private TextField TextTFD;

    @FXML
    private VBox MessagesVBX;

    @FXML
    private HBox SendHBX;

    @FXML
    private Button SendBTN;

    @FXML
    private Button OkBTN;

    private Stage ChatRoomStage;

    private String Friend_GroupName;

    private String Username;

    private int FriendChatRoom;

    private int userChatRoom;

    @FXML
    private MenuItem ClearHistoryMBN;

    @FXML
    private MenuItem BackMBN;

    @FXML
    private Label friendNameLBL;

    @FXML
    private Menu kickMNU;

    @FXML
    private Label participantsLBL;

    @FXML
    private Button unsendBTN;

    private ChatRoomType type;

    private ArrayList<Person> users;

    private int index;

    private final JDBC jdbc = new JDBC();


    //getting the stage and chatroom info
    public void initFunction(Stage chatRoomStage , String username , String friend_groupName , int userChatRoom,
                             int friendChatRoom ,ArrayList<Person> users , int index , ChatRoomType type) {
        this.ChatRoomStage = chatRoomStage;
        this.Friend_GroupName = friend_groupName;
        this.Username = username;
        this.FriendChatRoom = friendChatRoom;
        this.userChatRoom = userChatRoom;
        this.users = users;
        this.index = index;
        this.type = type;

        friendNameLBL.setText(Friend_GroupName);

        //getting and inserting chats from before
        ArrayList<ChatLine> chatLines = jdbc.getChats(userChatRoom);

        for (ChatLine chatline : chatLines) {

            //setting the color and text alignment for sent messages
            TextFlow textFlow = new TextFlow();
            Text theUsername;
            if (chatline.getUsername().equals(username)){
                theUsername = new Text(chatline.getUsername() + "\t");
                theUsername.setFill(Color.GREEN);
                theUsername.setFont(Font.font("Helvetica", 18));
                Text time = new Text(chatline.getTime() + "\n");
                time.setFill(Color.GRAY);
                time.setFont(Font.font("Helvetica", 16));

                textFlow.getChildren().add(theUsername);
                textFlow.getChildren().add(time);

                Group_info group_info = new Group_info();
                if (group_info.LinkValidation(chatline.getLine_text())){
                    Hyperlink hyperlink = new Hyperlink(chatline.getLine_text());
                    hyperlink.setFont(Font.font("Verdana", 20));
                    textFlow.getChildren().add(hyperlink);

                }
                else{
                    Text text = new Text(chatline.getLine_text());
                    text.setFill(Color.BLACK);
                    text.setFont(Font.font("Verdana", 20));
                    textFlow.getChildren().add(text);
                }

                textFlow.setTextAlignment(TextAlignment.LEFT);
                textFlow.setLineSpacing(10.0f);
                textFlow.setMaxWidth(600);
                textFlow.setStyle("-fx-background-color: #faf8d7");
            }
            else {
                theUsername = new Text(chatline.getUsername() + "\n");
                theUsername.setFill(Color.GREEN);
                theUsername.setFont(Font.font("Helvetica", 18));
                Text time = new Text(chatline.getTime() + "\t");
                time.setFill(Color.GRAY);
                time.setFont(Font.font("Helvetica", 16));

                textFlow.getChildren().add(time);
                textFlow.getChildren().add(theUsername);

                //getting the group hyperlinks and setting on action for them
                Group_info group_info = new Group_info();
                //making sure the text is a hyperlink
                if (group_info.LinkValidation(chatline.getLine_text())){

                    Hyperlink hyperlink = new Hyperlink(chatline.getLine_text());
                    hyperlink.setFont(Font.font("Verdana", 20));
                    textFlow.getChildren().add(hyperlink);
                    hyperlink.setOnAction(event -> {
                        Group_info groupInfo;
                        groupInfo = jdbc.FindHyperLinks(chatline.getLine_text());
                        groupInfo.setLink(chatline.getLine_text());
                        groupInfo.setStatus(1);
                        groupInfo.setUser(Username);
                        jdbc.InsertIntoGroup(groupInfo);
                        event.consume();
                    });
                }
                //if it's just text
                else{
                    Text text = new Text(chatline.getLine_text());
                    text.setFill(Color.BLACK);
                    text.setFont(Font.font("Verdana", 20));
                    textFlow.getChildren().add(text);
                }

                textFlow.setTextAlignment(TextAlignment.RIGHT);
                textFlow.setLineSpacing(10.0f);
                textFlow.setMaxWidth(600);
                textFlow.setStyle("-fx-background-color: #c2ede5");
            }
            textFlow.setPadding (new Insets(0 , 5 , 5 , 5));
            MessagesVBX.getChildren().add(textFlow);

        }

        //showing group participants on chatroom view
        if (type == ChatRoomType.GROUP) {
            ArrayList<String> participants = jdbc.GetGroupInfo(Friend_GroupName);
            String Participants = "";

            for (String person : participants){
               Participants = Participants.concat(person + ", ");
            }
            participantsLBL.setText(Participants);

            //enabling kick menu for the admin
            for (int i = 1; i < participants.size() ; i++ ) {
                MenuItem person = new MenuItem(participants.get(i));
                kickMNU.getItems().add(person);
            }
            if (participants.get(0).equals(username)) {
                kickMNU.setVisible(true);
                for (MenuItem item : kickMNU.getItems()) {
                    item.setOnAction(event -> {

                        jdbc.RemoveParticipant(item.getText(), Friend_GroupName);
                        kickMNU.getItems().remove(item);
                        participantsLBL.setText(participantsLBL.getText().replaceAll(item.getText()+", ", ""));
                        event.consume();
                    });
                }
            }
            else {
                kickMNU.setVisible(false);
            }
        }

        //not allowing users to send message if they blocked each other
        if (ChatRoomType.BLOCKED == type){
            SendHBX.setDisable(true);
        }
    }

    @FXML
    public void SendHandler(ActionEvent event) {

        if (!TextTFD.getText().isEmpty()) {

            //starting the timer
            Timer timer = new Timer();
            timer.start();

            //text attributes
            TextFlow textFlow = new TextFlow();

            Text username = new Text(Username + "\t");
            username.setFill(Color.GREEN);
            username.setFont(Font.font("Helvetica", 18));
            DateTimeFormatter Format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.now();
            Text time = new Text(dateTime.format(Format) + "\n");
            time.setFill(Color.GRAY);
            time.setFont(Font.font("Helvetica", 16));

            textFlow.getChildren().add(username);
            textFlow.getChildren().add(time);

            String textValue;

            Hyperlink hyperlink = new Hyperlink(TextTFD.getText());
            Text text = new Text(TextTFD.getText());

            Group_info group_info = new Group_info();
            if (group_info.LinkValidation(TextTFD.getText())) {

                hyperlink.setFont(Font.font("Verdana", 20));
                textValue = TextTFD.getText();
                textFlow.getChildren().add(hyperlink);

            } else {
                textValue = TextTFD.getText();

                text.setFill(Color.BLACK);
                text.setFont(Font.font("Verdana", 20));
                textFlow.getChildren().add(text);
            }

            textFlow.setTextAlignment(TextAlignment.LEFT);
            textFlow.setLineSpacing(10.0f);
            textFlow.setMaxWidth(600);
            textFlow.setStyle("-fx-background-color: #faf8d7");
            textFlow.setPadding(new Insets(0, 5, 5, 5));
            MessagesVBX.getChildren().add(textFlow);

            //storing data
            if (type == ChatRoomType.GROUP) {
                jdbc.InsertChats(userChatRoom, Username, TextTFD.getText());
            } else {
                jdbc.InsertChats(FriendChatRoom, Username, TextTFD.getText());
                jdbc.InsertChats(userChatRoom, Username, TextTFD.getText());
            }

            //unsent message
            unsendBTN.setOnAction(event1 -> {
                if (!timer.check) {
                    jdbc.deleteText(textValue, userChatRoom);
                    jdbc.deleteText(textValue, FriendChatRoom);
                    MessagesVBX.getChildren().remove(textFlow);
                }

            });


            //Context menu for sent messages
            final ContextMenu MessageCMU = new ContextMenu();
            MenuItem editMessage = new MenuItem("Edit");
            MenuItem deleteMessage = new MenuItem("Delete");

            editMessage.setOnAction(e -> {

                TextTFD.setText(textValue);

                OkBTN.setVisible(true);
                SendBTN.setDisable(true);

                OkBTN.setOnAction(event12 -> {
                    jdbc.editText(TextTFD.getText(), textValue, userChatRoom);
                    jdbc.editText(TextTFD.getText(), textValue, FriendChatRoom);

                    Group_info group_info1 = new Group_info();
                    if (group_info1.LinkValidation(TextTFD.getText())) {

                        textFlow.getChildren().remove(hyperlink);
                        Hyperlink newHyperlink = new Hyperlink(TextTFD.getText());
                        hyperlink.setFont(Font.font("Verdana", 20));
                        textFlow.getChildren().add(newHyperlink);

                    } else {

                        Text newText = new Text(TextTFD.getText());
                        newText.setFill(Color.BLACK);
                        newText.setFont(Font.font("Verdana", 20));
                        textFlow.getChildren().remove(text);
                        textFlow.getChildren().add(newText);

                    }
                    TextTFD.clear();
                    OkBTN.setVisible(false);
                    SendBTN.setDisable(false);
                });

            });

            deleteMessage.setOnAction(e -> {
                jdbc.deleteText(textValue, userChatRoom);
                jdbc.deleteText(textValue, FriendChatRoom);
                MessagesVBX.getChildren().remove(textFlow);
            });

            MessageCMU.getItems().add(editMessage);
            MessageCMU.getItems().add(deleteMessage);

            textFlow.addEventHandler(MouseEvent.MOUSE_CLICKED,
                    e -> {
                        if (e.getButton() == MouseButton.SECONDARY)
                            MessageCMU.show(textFlow, e.getScreenX(), e.getScreenY());
                    });

        }

        TextTFD.clear();

    }

    private void setUnsentAccelerator(Button button) {
        if (button == null) {
            System.out.println("Button is null");
        }
        assert button != null;
        Scene scene = button.getScene();
        if (scene == null) {
            throw new IllegalArgumentException("setSaveAccelerator must be called when a button is attached to a scene");
        }

        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.B),
                new Runnable() {
                    @FXML
                    public void run() {

                        button.fire();
                    }
                }
        );
    }
    public void setup() {
        setUnsentAccelerator(unsendBTN);
    }

    @FXML
    public void BackHandler (ActionEvent event){
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
        controller.initFunction(ChatRoomStage , users , index);

        Scene scene = new Scene(loader.getRoot());
        ChatRoomStage.setScene(scene);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("..\\view\\StyleSheet.css")).toExternalForm());
    }

    @FXML
    public void ClearHistory (ActionEvent event){

        //making a alert
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setContentText("Clear History?");

        ButtonType for_me = new ButtonType("for me", ButtonBar.ButtonData.OK_DONE);
        ButtonType for_everyone = new ButtonType("for everyone", ButtonBar.ButtonData.APPLY);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        if(ChatRoomType.GROUP == type){
            alert.getDialogPane().getButtonTypes().addAll(for_everyone ,cancel);
        }
        else if (ChatRoomType.PERSONAL == type || ChatRoomType.BLOCKED == type){
            alert.getDialogPane().getButtonTypes().addAll(for_me ,for_everyone ,cancel);
        }

        Optional<ButtonType> result = alert.showAndWait();
        if(!result.isPresent()){
            alert.close();
        }
        // (code owner)alert is exited, no button has been pressed.
        else if(result.get() == for_everyone){
            if (ChatRoomType.GROUP != type) {
                jdbc.ClearChats(FriendChatRoom);
            }
            jdbc.ClearChats(userChatRoom);
            MessagesVBX.getChildren().removeAll(MessagesVBX.getChildren());
        }
        else if(result.get() == for_me){
            jdbc.ClearChats(userChatRoom);
            MessagesVBX.getChildren().removeAll(MessagesVBX.getChildren());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        SendBTN.setOnAction(this::SendHandler);
        BackMBN.setOnAction(this::BackHandler);
        ClearHistoryMBN.setOnAction(this::ClearHistory);

    }
}

