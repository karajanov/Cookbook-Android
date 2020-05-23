package com.example.cookbookapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cookbookapp.Interfaces.IUserApi;
import com.example.cookbookapp.Models.LoginCredentials;
import com.example.cookbookapp.Utility.Validator;
import com.example.cookbookapp.Utility.RetrofitBuilder;
import com.example.cookbookapp.Utility.UserSession;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private ArrayList<EditText> editTextList;
    private Button signInButton;
    private Retrofit rb = RetrofitBuilder.getBuilder(Validator.RECIPES_API_BASE);
    private IUserApi userApiRef = rb.create(IUserApi.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Preventing landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Initialization
        editTextUsername = (EditText) findViewById(R.id.edit_text_login_name);
        editTextPassword = (EditText) findViewById(R.id.edit_text_login_password);
        signInButton = (Button) findViewById(R.id.btn_login_act);
        editTextList = new ArrayList<EditText>();

        //Event Handlers
        addLoginFieldsToList();
        addSignInEventHandler();


    }

    private void addSignInEventHandler() {
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Validator.hasEmptyFieldTestPassed(editTextList))
                    return;
                String username = editTextUsername.getText().toString();
                String plainPassword = editTextPassword.getText().toString();
                LoginCredentials lc = new LoginCredentials(username, plainPassword);
                checkLoginInfo(lc);
            }
        });
    }

    private void addLoginFieldsToList() {
        editTextList.add(editTextUsername);
        editTextList.add(editTextPassword);
    }

    private void checkLoginInfo(final LoginCredentials lc) {
        Call<Boolean> call = userApiRef.checkLoginCredentials(lc);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(!response.isSuccessful()) {
                    Log.e(getLocalClassName(), "checkLoginInfo: status code " + response.code());
                    String err = "An error occurred, try again later";
                    Toast.makeText(LoginActivity.this, err, Toast.LENGTH_SHORT).show();
                    return;
                }
                Boolean isValid = response.body();
                if(isValid) {
                    SharedPreferences sp = getSharedPreferences(UserSession.SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    UserSession.saveUser(editor, lc.getUsername());
                    goBackToMainActivity();
                } else {
                    String errMsg = "Invalid login info";
                    Toast.makeText(LoginActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(getLocalClassName(), t.getMessage());
                String err = "An error occurred, try again later";
                Toast.makeText(LoginActivity.this, err, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goBackToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
