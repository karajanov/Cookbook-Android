package com.example.cookbookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    private Button registerBtn, lookupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialization
        lookupBtn = (Button) findViewById(R.id.btn_lookup);

        //Preventing landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Event handlers
        addLookupEventHandler();
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

    //Event handler for the lookup btn
    private void addLookupEventHandler() {
        if(lookupBtn == null)
            return;

        lookupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLookupActivity();
            }
        });
    }

    //Open the lookup activity
    private void openLookupActivity() {
        Intent intent = new Intent(this, LookupActivity.class);
        startActivity(intent);
    }
}
