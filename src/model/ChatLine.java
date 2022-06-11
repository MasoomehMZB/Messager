package model;

import java.sql.Timestamp;

public class ChatLine {

    public int getChatID() {
        return chatID;
    }

    public void setChatID(int chatID) {
        this.chatID = chatID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLine_text() {
        return line_text;
    }

    public void setLine_text(String line_text) {
        this.line_text = line_text;
    }

    public Timestamp getTime() {return time;}

    public ChatLine(int chatID, String username, String line_text, Timestamp time) {
        this.chatID = chatID;
        this.username = username;
        this.line_text = line_text;
        this.time = time;
    }

    private int chatID;
    //the sender of the text
    private String username;
    private String line_text;
    private Timestamp time;
}
