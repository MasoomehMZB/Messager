package model;

import java.util.regex.Pattern;

public class Group_info {
    private String name;
    private String link;
    private String Admin;
    private  int ChatId;
    private String user;
    private int status;

    //getters and setters
    public int getChatId() {
        return ChatId;
    }
    public void setChatId(int chatId) {
        ChatId = chatId;
    }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAdmin() {
        return Admin;
    }

    public void setAdmin(String admin) {
        Admin = admin;
    }

    //constructors
    public Group_info(String name, String link, String user , String admin , int status , int ChatId) {
        this.name = name;
        this.link = link;
        this.user = user;
        this.Admin = admin;
        this.status = status;
        this.ChatId = ChatId;
    }

    public Group_info() {

    }

    public Group_info(String name, String admin) {
        this.name = name;
        Admin = admin;
    }

    //link validation method
    public boolean LinkValidation(String Link){
        return Pattern.matches("@\\S{5,20}", Link);
    }
}
