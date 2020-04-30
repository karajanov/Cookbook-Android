package com.example.cookbookapp.Models;

public class LoginCredentials {

    private String username;

    private String plainPassword;

    public LoginCredentials(String username, String plainPassword) {
        this.username = username;
        this.plainPassword = plainPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getPlainPassword() {
        return plainPassword;
    }
}
