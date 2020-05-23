package com.example.cookbookapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cookbookapp.Utility.UserSession;

public class UserRecipesActivity extends AppCompatActivity {

    private TextView textViewUserRecipes;
    private Button insertDestBtn, editDestBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_recipes);

        //Preventing landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Initialization
        textViewUserRecipes = (TextView) findViewById(R.id.text_view_user_recipes);
        insertDestBtn = (Button) findViewById(R.id.btn_insert_new_recipes);
        editDestBtn = (Button) findViewById(R.id.btn_edit_recipes);
        Intent intent = getIntent();

        displayUsernameInTextView();
        displayEditMessageIfNecessary(intent, InsertRecipesActivity.EXTRA_INSERT_SUCCESS);
        displayEditMessageIfNecessary(intent, UpdateRecipeActivity.EXTRA_UPDATE_SUCCESS);
        displayEditMessageIfNecessary(intent, DeleteRecipeActivity.EXTRA_DELETE_SUCCESS);
        redirectToActivity(insertDestBtn, InsertRecipesActivity.class);
        redirectToActivity(editDestBtn, EditPreviewActivity.class);
    }

    private void displayUsernameInTextView() {
        SharedPreferences sp = getSharedPreferences(UserSession.SHARED_PREFS, MODE_PRIVATE);
        String currentUser = UserSession.getUser(sp).toUpperCase();
        textViewUserRecipes.setText(currentUser + "\'S" + " RECIPES");
    }

    private void redirectToActivity(Button b, final Class<?> dest) {
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(dest);
            }
        });
    }

    private void openActivity(Class<?> dest) {
        Intent intent = new Intent(UserRecipesActivity.this, dest);
        startActivity(intent);
    }

    private void displayEditMessageIfNecessary(Intent intent, String key) {
        String msg = intent.getStringExtra(key);
        if(msg != null) {
            Toast.makeText(UserRecipesActivity.this, msg, Toast.LENGTH_LONG).show();
        }
    }
}
