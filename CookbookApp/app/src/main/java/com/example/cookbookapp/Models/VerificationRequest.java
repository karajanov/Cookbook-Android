package com.example.cookbookapp.Models;

public class VerificationRequest {

    private User userViewModel;

    private String verificationCode;

    public User getUserViewModel() {
        return userViewModel;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public VerificationRequest(User userViewModel, String verificationCode) {
        this.userViewModel = userViewModel;
        this.verificationCode = verificationCode;
    }
}
