package com.example.cookbookapp.Models;

import java.io.Serializable;

public class User implements Serializable {

    private String email;

    private String username;

    private String plainPassword;

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public User(String email, String username, String plainPassword) {
        this.email = email;
        this.username = username;
        this.plainPassword = plainPassword;
    }
}
