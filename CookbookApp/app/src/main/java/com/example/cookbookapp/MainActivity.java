package com.example.cookbookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button registerBtn, lookupBtn, secondLookupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Preventing landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Initialization
        registerBtn = (Button) findViewById(R.id.btn_register);
        lookupBtn = (Button) findViewById(R.id.btn_lookup);
        secondLookupBtn = (Button) findViewById(R.id.btn_lookup_two);

        //Event handlers
         addRedirectEventHandler(registerBtn, RegistrationActivity.class);
         addRedirectEventHandler(lookupBtn, LookupActivity.class);
         addRedirectEventHandler(secondLookupBtn, SecondLookupActivity.class);
         displayRegistrationMessage();
    }

    @Override // Embedding the menu in the activity
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.general, menu);
        return true;
    }

    @Override // Menu items event handler
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.general_login_item:
                //
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }

    }

    private void addRedirectEventHandler(final Button b, final Class<?> dest) {
        if(b == null)
            return;

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(dest);
            }
        });
    }

    private void openActivity(Class<?> dest) {
        Intent intent = new Intent(MainActivity.this, dest);
        startActivity(intent);
    }
    
    private void displayRegistrationMessage() {
        Intent intent = getIntent();
        String newUser = intent.getStringExtra(VerificationActivity.EXTRA_REGISTRATION_OK);
        if(newUser != null) {
            Toast.makeText(MainActivity.this, "Successfully registered as " + newUser, Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
