package model;

import java.sql.*;
import java.util.ArrayList;

public class JDBC {

    public void InsertIntoTB (Person person){

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/person", "root", "91117033");

            Statement statement = connection.createStatement();

            PreparedStatement pdst = null;

            pdst = connection.prepareStatement("insert into user (username, name ,passwordHash, email) values(?,?,?,?)");
            pdst.setString(1,person.getUserName());
            pdst.setString(2,person.getName());
            pdst.setString(3,person.getPasswordHash());
            pdst.setString(4,person.getEmail());

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

                Users.add(new Person(resultSet.getString("name"), resultSet.getString("username"),
                        resultSet.getString("email"), resultSet.getString("passwordHash")));
            }
            return Users;

        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public boolean checkExistence (String field , String theValue){

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
}