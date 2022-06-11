package model;

public class Relationships {

    //stores other person's username and their relationship's status
    private String username;

    private int status;

    public Relationships(int status, String username) {
        this.status = status;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public int getStatus() {
        return status;
    }
}
