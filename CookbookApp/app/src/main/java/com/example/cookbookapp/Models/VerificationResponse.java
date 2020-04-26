package com.example.cookbookapp.Models;

public class VerificationResponse {

    private boolean isValid;

    private int statusCode;

    private String errorMessage;

    public boolean isValid() {
        return isValid;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
