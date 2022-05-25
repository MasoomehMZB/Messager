package model;

public class Relationships {
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
