package com.example.cookbookapp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cookbookapp.Interfaces.IUserApi;
import com.example.cookbookapp.Models.User;
import com.example.cookbookapp.Utility.Helper;
import com.example.cookbookapp.Utility.RetrofitBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextUsername, editTextPassword, editTextRe;
    private ArrayList<EditText> editTextList;
    private Button confirmRegistrationBtn;
    private RetrofitBuilder rb;
    private IUserApi userApiRef;
    private final Boolean[] isUsernameValidArr = { false };
    private final Boolean[] isEmailValidArr =  { false };

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
        rb = new RetrofitBuilder();
        userApiRef = rb.getBuilder(Helper.RECIPES_API_BASE).create(IUserApi.class);

        //Event handlers
        addFieldsToList();
        addConfirmationEventHandler();

    }

    private void addConfirmationEventHandler() {
        if (confirmRegistrationBtn == null)
            return;
        confirmRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!Helper.hasEmptyFieldTestPassed(editTextList))
//                    return;
//                if (!Helper.hasEmailTestPassed(editTextEmail))
//                    return;
//                if (!Helper.hasMinLengthTestPassed(editTextUsername, Helper.MIN_USERNAME_LENGTH))
//                    return;
//                if (!Helper.hasMinLengthTestPassed(editTextPassword, Helper.MIN_PASSWORD_LENGTH))
//                    return;
//                if (!Helper.hasPasswordMatchTestPassed(editTextPassword, editTextRe))
//                    return;
//
//                checkAvailability(editTextEmail, isEmailValidArr);
//                checkAvailability(editTextUsername, isUsernameValidArr);
//
//                if(isEmailValidArr[0] == null || isUsernameValidArr[0] == null) {
//                    String errMsg = "An error occurred, try again later";
//                    Toast.makeText(RegistrationActivity.this, errMsg, Toast.LENGTH_SHORT)
//                            .show();
//                } else if(isEmailValidArr[0] && isUsernameValidArr[0]) {
//                    Toast.makeText(RegistrationActivity.this, "g2g", Toast.LENGTH_SHORT)
//                            .show();
//                } else {
//                    String infoMsg = "Processing info, wait, then try again.";
//                    Toast.makeText(RegistrationActivity.this, infoMsg, Toast.LENGTH_SHORT)
//                            .show();
//                }
                User u = new User("karajanovb@yahoo.com", "borjan", "doesthiswork?");
                regUser(u);
            }

        });
    }

    private void addFieldsToList() {
        editTextList.add(editTextEmail);
        editTextList.add(editTextUsername);
        editTextList.add(editTextPassword);
        editTextList.add(editTextRe);
    }

    private void checkAvailability(final EditText editText, final Boolean[] isReadyArr) {
        if(isReadyArr == null) {
            return;
        }
        Call<Boolean> call;
        int editTextId = editText.getId();

        switch (editTextId) {
            case R.id.edit_text_reg_email:
                call = userApiRef.isEmailTaken(editText.getText().toString());
                break;
            case R.id.edit_text_reg_username:
                call = userApiRef.isUsernameTaken(editText.getText().toString());
                break;
            default:
                return;
        }

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(!response.isSuccessful()) {
                    Log.e(getLocalClassName(), "checkUsernameAvailability: status code " + response.code());
                    isReadyArr[0] = null;
                    return;
                }
                Boolean isTaken = response.body();
                if(isTaken) {
                    Helper.displayErrorMessage(editText, editText.getText().toString() + " is taken");
                    isReadyArr[0] = false;
                } else if(!isTaken){
                    Helper.clearErrorField(editText);
                    isReadyArr[0] = true;
                } else {
                    isReadyArr[0] = null;
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(getLocalClassName(), t.getMessage());
                isReadyArr[0] = null;
            }
        });
    }

    private void regUser(User u) {
        if(u == null) {
            return;
        }
        Call<Integer> call = userApiRef.registerUser(u);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                Toast.makeText(RegistrationActivity.this, "" + response.code(), Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e(getLocalClassName(), t.getMessage());
            }
        });
    }

}
