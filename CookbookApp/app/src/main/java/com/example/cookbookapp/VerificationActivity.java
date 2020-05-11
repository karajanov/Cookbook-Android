package com.example.cookbookapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cookbookapp.Interfaces.IUserApi;
import com.example.cookbookapp.Models.User;
import com.example.cookbookapp.Models.VerificationRequest;
import com.example.cookbookapp.Models.VerificationStatus;
import com.example.cookbookapp.Utility.Helper;
import com.example.cookbookapp.Utility.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class VerificationActivity extends AppCompatActivity {

    public static final String EXTRA_REGISTRATION_OK = "com.example.cookbookapp.EXTRA_REGISTRATION_OK";

    private EditText editTextVerificationCode;
    private Button confirmRegistrationBtn;
    private TextView textViewVerificationInfo;
    private Retrofit rb = RetrofitBuilder.getBuilder(Helper.RECIPES_API_BASE);
    private IUserApi userApiRef = rb.create(IUserApi.class);
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        //Preventing landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Initialization
        editTextVerificationCode = (EditText) findViewById(R.id.edit_text_verification_code);
        confirmRegistrationBtn = (Button) findViewById(R.id.btn_send_verification_code);
        textViewVerificationInfo = (TextView) findViewById(R.id.text_view_verif_info);

        user = getUserExtra(RegistrationActivity.EXTRA_USER);
        textViewVerificationInfo.append("\n" + user.getEmail());

        addConfirmBtnEventHandler();
    }

    private void addConfirmBtnEventHandler() {
        confirmRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Helper.isFieldEmpty(editTextVerificationCode)) {
                    Helper.displayErrorMessage(editTextVerificationCode, "Empty field not allowed");
                } else {
                    Helper.clearErrorField(editTextVerificationCode);
                    String verificationCode = editTextVerificationCode.getText().toString();
                    final VerificationRequest request = new VerificationRequest(user, verificationCode);
                    AlertDialog.Builder adBuilder = new AlertDialog.Builder(VerificationActivity.this);
                    adBuilder.setTitle(R.string.verification_dialog_title);
                    adBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendRequest(request);
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
        });
    }

    private User getUserExtra(String key) {
        Intent intent = getIntent();
        return (User) intent.getSerializableExtra(key);
    }


    private void sendRequest(final VerificationRequest request) {
        Call<VerificationStatus> call = userApiRef.registerUser(request);
        call.enqueue(new Callback<VerificationStatus>() {
            @Override
            public void onResponse(Call<VerificationStatus> call, Response<VerificationStatus> response) {
                if(!response.isSuccessful()) {
                    Log.e(getLocalClassName(), "sendRequest: status code " + response.code());
                    return;
                }
                VerificationStatus vr = response.body();
                if(!vr.isValid()) {
                    Log.e(getLocalClassName(),vr.getStatusCode() + " - " + vr.getErrorMessage());
                    Toast.makeText(VerificationActivity.this, vr.getErrorMessage(), Toast.LENGTH_LONG)
                            .show();
                } else {
                    Intent intent = new Intent(VerificationActivity.this, MainActivity.class);
                    intent.putExtra(EXTRA_REGISTRATION_OK, request.getUserViewModel().getUsername());
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<VerificationStatus> call, Throwable t) {
                Log.e(getLocalClassName(), t.getMessage());
            }
        });
    }
}
