package com.example.bruno_desousa_trakker;

public class User {
    private String username;
    private String password;
    private int smsBool;

    // Constructors, getters, and setters

    public User() {
        // Default constructor
    }

    public User(String username, String password, int smsBool) {
        this.username = username;
        this.password = password;
        this.smsBool = smsBool;
    }

    // Getter and setter methods for 'username'
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and setter methods for 'password'
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter and setter methods for 'smsBool'
    public int getSmsBool() {
        return smsBool;
    }

    public void setSmsBool(int smsBool) {
        this.smsBool = smsBool;
    }
}
