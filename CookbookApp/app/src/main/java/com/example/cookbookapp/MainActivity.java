package com.example.cookbookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.cookbookapp.Utility.UserSession;

public class MainActivity extends AppCompatActivity {

    private Button registerBtn, lookupBtn, secondLookupBtn, myRecipesBtn;
    private MenuItem loginItem;

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
        myRecipesBtn = (Button) findViewById(R.id.btn_my_recipes);

        //Event handlers
        addRedirectEventHandler(registerBtn, RegistrationActivity.class);
        addRedirectEventHandler(lookupBtn, LookupActivity.class);
        addRedirectEventHandler(secondLookupBtn, SecondLookupActivity.class);
        addMyRecipesEventHandler();
        displayRegistrationMessageIfNecessary();
    }

    @Override // Embedding the menu in the activity
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.general, menu);
        return true;
    }

    @Override //Menu preparation, right before it is shown
    public boolean onPrepareOptionsMenu(Menu menu) {
        loginItem = menu.findItem(R.id.general_login_item);
        MenuItem logoffItem = menu.findItem(R.id.general_logoff_item);
        if(isUserLoggedIn()) {
            loginItem.setVisible(false);
            logoffItem.setVisible(true);
            logoffItem.setTitle("Log off (" + getCurrentUser() + ")");
            myRecipesBtn.setVisibility(View.VISIBLE);
        } else {
            logoffItem.setVisible(false);
            loginItem.setVisible(true);
            myRecipesBtn.setVisibility(View.GONE);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override // Menu items event handler
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.general_login_item:
                openActivity(LoginActivity.class);
                return true;
            case R.id.general_logoff_item:
                SharedPreferences sp = getSharedPreferences(UserSession.SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                UserSession.clearUser(editor);
                item.setVisible(false);
                myRecipesBtn.setVisibility(View.GONE);
                loginItem.setVisible(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addRedirectEventHandler(final Button b, final Class<?> dest) {
        if (b == null)
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

    private void displayRegistrationMessageIfNecessary() {
        Intent intent = getIntent();
        String newUser = intent.getStringExtra(VerificationActivity.EXTRA_REGISTRATION_OK);
        if (newUser != null) {
            Toast.makeText(MainActivity.this, "Successfully registered as " + newUser, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private boolean isUserLoggedIn() {
        String currentUser = getCurrentUser();
        return currentUser != null;
    }

    private String getCurrentUser() {
        SharedPreferences sp = getSharedPreferences(UserSession.SHARED_PREFS, MODE_PRIVATE);
        return UserSession.getUser(sp);
    }

    private void addMyRecipesEventHandler() {
        myRecipesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(UserRecipesActivity.class);
            }
        });
    }
}
