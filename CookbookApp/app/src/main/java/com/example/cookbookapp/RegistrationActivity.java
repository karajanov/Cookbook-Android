package com.example.cookbookapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextUsername, editTextPassword, editTextRe;
    private ArrayList<EditText> editTextList;
    private Button confirmRegistrationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Preventing landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Initialization
        editTextEmail = (EditText) findViewById(R.id.edit_text_reg_email);
        editTextUsername = (EditText) findViewById(R.id.edit_text_reg_username);
        editTextPassword = (EditText) findViewById(R.id.edit_text_reg_password);
        editTextRe = (EditText) findViewById(R.id.edit_text_register_re);
        editTextList = new ArrayList<EditText>();
        confirmRegistrationBtn = (Button) findViewById(R.id.btn_confirm_reg);

        //Event handlers
        addFieldsToList();
        addConfirmationEventHandler();

    }

    private void addConfirmationEventHandler() {
        if(confirmRegistrationBtn == null)
            return;
        confirmRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Helper.performEmptyFieldCheck(editTextList);
               if(Helper.areAllFieldsFilledIn(editTextList)) {
                   boolean isEmailValid = Helper.isEmailValid(editTextEmail);
                   if(!isEmailValid) {
                       Helper.displayErrorMessage(editTextEmail, "Invalid email");
                       return;
                   } else {
                       Helper.clearErrorField(editTextEmail);
                   }
                   if(Helper.getFieldLength(editTextUsername) < Helper.MIN_USERNAME_LENGTH) {
                       Helper.displayErrorMessage(editTextUsername, "Min Length is 3");
                       return;
                   }else {
                       Helper.clearErrorField(editTextUsername);
                   }
                   //p length
               }
            }
        });
    }

    private void addFieldsToList() {
        editTextList.add(editTextEmail);
        editTextList.add(editTextUsername);
        editTextList.add(editTextPassword);
        editTextList.add(editTextRe);
    }



}
