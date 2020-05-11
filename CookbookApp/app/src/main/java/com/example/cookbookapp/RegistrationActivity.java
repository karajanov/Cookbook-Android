package com.example.cookbookapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cookbookapp.Interfaces.IUserApi;
import com.example.cookbookapp.Models.User;
import com.example.cookbookapp.Models.VerificationStatus;
import com.example.cookbookapp.Utility.Helper;
import com.example.cookbookapp.Utility.RetrofitBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegistrationActivity extends AppCompatActivity {

    public static final String EXTRA_USER = "com.example.cookbookapp.EXTRA_USER";

    private EditText editTextEmail, editTextUsername, editTextPassword, editTextRe;
    private ArrayList<EditText> editTextList;
    private Button confirmRegistrationBtn;
    private Retrofit rb = RetrofitBuilder.getBuilder(Helper.RECIPES_API_BASE);
    private IUserApi userApiRef = rb.create(IUserApi.class);

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
        if (confirmRegistrationBtn == null)
            return;
        confirmRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.hasEmptyFieldTestPassed(editTextList))
                    return;
                if (!Helper.hasEmailTestPassed(editTextEmail))
                    return;
                if (!Helper.hasMinLengthTestPassed(editTextUsername, Helper.MIN_USERNAME_LENGTH))
                    return;
                if (!Helper.hasMinLengthTestPassed(editTextPassword, Helper.MIN_PASSWORD_LENGTH))
                    return;
                if (!Helper.hasPasswordMatchTestPassed(editTextPassword, editTextRe))
                    return;

                checkEmailAvailability();
            }

        });
    }

    private void addFieldsToList() {
        editTextList.add(editTextEmail);
        editTextList.add(editTextUsername);
        editTextList.add(editTextPassword);
        editTextList.add(editTextRe);
    }

    private void checkEmailAvailability() {
        Call<Boolean> call = userApiRef.isEmailTaken(editTextEmail.getText().toString());
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(!response.isSuccessful()) {
                    Log.e(getLocalClassName(), "checkEmailAvailability: status code " + response.code());
                    return;
                }
                Boolean isTaken = response.body();
                if(isTaken == null || isTaken) {
                    Helper.displayErrorMessage(editTextEmail,
                            editTextEmail.getText().toString() + " is taken");
                } else {
                    Helper.clearErrorField(editTextEmail);
                    checkUsernameAvailability();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(getLocalClassName(), t.getMessage());
            }
        });
    }

    private void checkUsernameAvailability() {
        Call<Boolean> call = userApiRef.isUsernameTaken(editTextUsername.getText().toString());
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(!response.isSuccessful()) {
                    Log.e(getLocalClassName(), "checkUsernameAvailability: status code " + response.code());
                    return;
                }
                Boolean isTaken = response.body();
                if(isTaken == null || isTaken) {
                    Helper.displayErrorMessage(editTextUsername, editTextUsername.getText().toString() + " is taken");
                } else {
                    Helper.clearErrorField(editTextUsername);
                    String email = editTextEmail.getText().toString();
                    String username = editTextUsername.getText().toString();
                    String password = editTextPassword.getText().toString();
                    final User user = new User(email, username, password);
                    AlertDialog.Builder adBuilder = new AlertDialog.Builder(RegistrationActivity.this);
                    adBuilder.setTitle(R.string.registration_dialog_title);
                    adBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            postUser(user);
                        }
                    });
                    adBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog ad = adBuilder.create();
                    ad.show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(getLocalClassName(), t.getMessage());
            }
        });

    }

    private void postUser(final User u) {
        if(u == null) {
            return;
        }
        Call<VerificationStatus> call = userApiRef.verifyUser(u);

        call.enqueue(new Callback<VerificationStatus>() {
            @Override
            public void onResponse(Call<VerificationStatus> call, Response<VerificationStatus> response) {
                if(!response.isSuccessful()) {
                    Log.e(getLocalClassName(), "postUser, status code: " + response.code());
                    Toast.makeText(RegistrationActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                VerificationStatus vr = response.body();
                if(!vr.isValid()) {
                    Log.e(getLocalClassName(),vr.getStatusCode() + " - " + vr.getErrorMessage());
                    Toast.makeText(RegistrationActivity.this, vr.getErrorMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(VerificationActivity.class, u);
                }
            }
            @Override
            public void onFailure(Call<VerificationStatus> call, Throwable t) {
                Log.e(getLocalClassName(), t.getMessage());
                Toast.makeText(RegistrationActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startActivity(Class<?> dest, User u) {
        Intent intent = new Intent(RegistrationActivity.this, dest);
        intent.putExtra(EXTRA_USER, u);
        startActivity(intent);
    }

}