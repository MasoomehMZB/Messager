package model;

import java.sql.*;
import java.util.ArrayList;

public class JDBC {

    //Users
    public void InsertIntoTB (Person person){

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            PreparedStatement pdst = null;

            pdst = connection.prepareStatement("insert into user (username , passwordHash, email) values(?,?,?)");
            pdst.setString(1,person.getUserName());
            pdst.setString(2,person.getPasswordHash());
            pdst.setString(3,person.getEmail());

            pdst.executeUpdate();

            pdst.close();

            connection.close();

        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Person> ReadIntoArrayList() {
        try{
            Connection connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            String sql = "select * from user";

            ResultSet resultSet;

            resultSet = statement.executeQuery(sql);

            ArrayList<Person> Users = new ArrayList<>();

            while (resultSet.next()) {

                Users.add(new Person(resultSet.getString("username"),
                        resultSet.getString("email"), resultSet.getString("passwordHash")));
            }
            return Users;

        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }


    public boolean checkExistenceUser(String field , String theValue){

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            String SQL = "select * from user where " + field +"= '" + theValue + "'";

            ResultSet resultSet = statement.executeQuery(SQL);

            if (resultSet.next()){
                connection.close();
                return true;
            }
            else {
                connection.close();
                return false;
            }


        }catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /*public boolean checkExistenceRelationships(String field , String username , int status){

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            String SQL = "select * from relationships where " + field +"= '" + username + "' AND status = '"+status+"';";

            ResultSet resultSet = statement.executeQuery(SQL);

            if (resultSet.next()){
                connection.close();
                return true;
            }
            else {
                connection.close();
                return false;
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }*/

    public void sendFriendRequest (String sender , String receiver ,int status){

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            String SQL = "INSERT INTO relationships (username1, username2 , status) VALUES ('" +
                    sender +"','" + receiver +"', '" + status + "' );";

            statement.executeUpdate(SQL);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void AcceptFriendRequest (String sender ,String receiver){

        Connection connection;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            String SQL = "UPDATE relationships SET status = 1 "+
            "WHERE username1 ='" + sender+"' AND username2 = '"+receiver+"';";

            statement.executeUpdate(SQL);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void Block (String user1 ,String user2){

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            String SQL = "UPDATE relationships SET status = -1 WHERE username1 = '"+
                    user1 + "' AND username2 = '"+ user2 +"' OR username2 = '"+
                    user1 +"' AND username1 = '"+ user2 +"';";

            statement.executeUpdate(SQL);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void DeclineFriendRequest (String sender ,String receiver ){

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            String SQL = "DELETE FROM relationships WHERE username2 = '"+
                    receiver +"' AND username1 ='"+ sender +"';";

            statement.executeUpdate(SQL);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Relationships > GetRelations (String username , int status){

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            String SQL ="SELECT relationships.username2 FROM relationships " +
           " WHERE relationships.username1 = '"+ username +"' AND relationships.status = '" + status +"' ;";

            ResultSet resultSet = statement.executeQuery(SQL);


            ArrayList<Relationships> relations = new ArrayList<>();

            while (resultSet.next()){
                relations.add(new Relationships(status ,
                        resultSet.getString("username2")));
            }

            connection.close();
            return relations;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    //relation that were set from the other user
    public ArrayList<Relationships > GetReceivedRelations (String username , int status){

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            String SQL ="SELECT username1 FROM relationships " +
                    " WHERE username2 = '"+ username +"' AND relationships.status = '" + status +"' ;";

            ResultSet resultSet = statement.executeQuery(SQL);

            ArrayList<Relationships> relations = new ArrayList<>();

            while (resultSet.next()){
                relations.add(new Relationships(status , resultSet.getString("username1")));
            }

            connection.close();
            return relations;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Chat id
    public void SetChatID (String username ,String friend) {

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            String SQL = "INSERT IGNORE INTO chat_id (username , friend)" +
                    " VALUES ('"+username+"' , '"+friend+"');";

            statement.executeUpdate(SQL);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int GetChatID (String username ,String friend) {

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            String SQL = "SELECT id FROM chat_id WHERE username = '"+username+"' AND friend = '"+friend+"' ;";

            ResultSet resultSet = statement.executeQuery(SQL);

            int id = -10;
            if (resultSet.next()){
                id = resultSet.getInt("id");
            }

            connection.close();

            return id;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -10;
    }

    //Chats
    public void InsertChats (int ChatID,String Username ,String Line_text){

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            String SQL = "INSERT INTO chat ( chat_id, username, line_text ) VALUES ('"+ChatID+"' , '"+
                    Username+"' , '"+Line_text+"');";

            statement.executeUpdate(SQL);

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void ClearChats (int ChatID){

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            String SQL = "DELETE FROM chat WHERE chat_id = '"+ChatID+"';";

            statement.executeUpdate(SQL);

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ChatLine > getChats(int chatID){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            System.out.println(chatID);
            String SQL ="SELECT username , line_text , created_at FROM chat " +
                    " WHERE chat_id = '"+ chatID +"' ;";

            ResultSet resultSet = statement.executeQuery(SQL);

            ArrayList<ChatLine> chatLines = new ArrayList<>();

            while (resultSet.next()){
                chatLines.add(new ChatLine(chatID , resultSet.getString("username"),resultSet.getString("line_text"),
                        resultSet.getTimestamp("created_at")));
            }

            connection.close();
            return chatLines;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Groups
    public void InsertIntoGroup (Group_info newGroup){

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            String SQL = "INSERT IGNORE INTO person.group ( group_name , added_people , group_admin , groupLink , status ) " +
                    "VALUES ('"+ newGroup.getName() +"','"+newGroup.getUser()+"' , '"+newGroup.getAdmin()+"' , " +
                    "'"+newGroup.getLink()+"' , '"+newGroup.getStatus()+"');";

            statement.executeUpdate(SQL);

            connection.close();

        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Group_info> ReadFromGroup(String username) {
        try{
            Connection connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            String sql = "SELECT idGroup , group_name , group_admin , added_people , groupLink, status " +
                    "FROM person.group WHERE added_people = '"+username+"';";

            ResultSet resultSet = statement.executeQuery(sql);

            ArrayList<Group_info> group_info = new ArrayList<>();

            while (resultSet.next()) {
                group_info.add(new Group_info(resultSet.getString("group_name"),
                        resultSet.getString("groupLink"), resultSet.getString("added_people"),
                        resultSet.getString("group_admin"),resultSet.getInt("status")
                        , resultSet.getInt("idGroup")));
            }
            return group_info;

        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public Group_info FindHyperLinks(String link){

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            String SQL = "select * from person.group where groupLink = '" + link +"' AND status = '0';";

            ResultSet resultSet = statement.executeQuery(SQL);

            if (resultSet.next()){
                Group_info group_info = new Group_info(resultSet.getString("group_name") ,
                        resultSet.getString("group_admin"));
                connection.close();
                return group_info;
            }
            else {
                connection.close();
                return null;
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public ArrayList<String> GetGroupInfo ( String groupName ){

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            String SQL = "SELECT added_people FROM person.group WHERE group_name = '"+ groupName +"';";

            ResultSet resultSet = statement.executeQuery(SQL);

            ArrayList<String> users = new ArrayList<>();

            while (resultSet.next()){
                users.add(resultSet.getString("added_people"));
            }

            return users;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void RemoveParticipant( String name , String group_name ){

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            String SQL = "DELETE FROM person.group WHERE added_people = '"+ name +"' AND group_name = '"+ group_name +"';";

            statement.executeUpdate(SQL);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Each text message
    public void editText(String newText , String oldText , int chatId ){

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            String SQL = "UPDATE chat SET line_text = '"+ newText +"' WHERE chat_id = '"+ chatId +"' AND line_text = '"+ oldText + "';";

            statement.executeUpdate(SQL);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteText( String Text , int chatId ){

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            String SQL = "DELETE FROM chat WHERE line_text = '"+ Text +"' AND chat_id = '"+ chatId +"';";

            statement.executeUpdate(SQL);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}