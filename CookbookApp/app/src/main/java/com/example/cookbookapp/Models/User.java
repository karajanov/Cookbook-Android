package com.example.cookbookapp.Models;

public class User {

    private String email;

    private String username;

    private String plainPassword;

    public User(String email, String username, String plainPassword) {
        this.email = email;
        this.username = username;
        this.plainPassword = plainPassword;
    }
}
