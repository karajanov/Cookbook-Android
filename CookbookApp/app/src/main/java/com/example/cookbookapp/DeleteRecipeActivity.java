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
import android.widget.TextView;
import android.widget.Toast;

import com.example.cookbookapp.Interfaces.IRecipesApi;
import com.example.cookbookapp.Models.RegularStatus;
import com.example.cookbookapp.Utility.Helper;
import com.example.cookbookapp.Utility.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DeleteRecipeActivity extends AppCompatActivity {

    public static final String EXTRA_DELETE_SUCCESS = "com.example.cookbookapp.EXTRA_DELETE_OK";

    private TextView textViewRecipeTitle;
    private Button btnDeleteRecipe;
    private int recipeId;
    private Retrofit rb = RetrofitBuilder.getBuilder(Helper.RECIPES_API_BASE);
    private IRecipesApi recipeApiRef = rb.create(IRecipesApi.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_recipe);

        //Preventing landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        textViewRecipeTitle = (TextView) findViewById(R.id.text_view_delete_title);
        btnDeleteRecipe = (Button) findViewById(R.id.btn_delete_recipe);

        Intent intent = getIntent();
        recipeId = intent.getIntExtra(EditPreviewActivity.EXTRA_EDIT_RECIPE_ID, 0);
        displaySelectedTitle(intent);
        deleteRecipeEventHandler();
    }

    private void deleteRecipeEventHandler() {
        btnDeleteRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(DeleteRecipeActivity.this);
                adBuilder.setTitle(R.string.delete_dialog_title);
                //Confirm recipe deletion
                adBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callDeleteRecipe(recipeId);
                    }
                });
                //Cancel recipe deletion
                adBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog ad = adBuilder.create();
                ad.show();
            }
        });
    }

    private void displaySelectedTitle(Intent intent) {
        String title = intent.getStringExtra(EditPreviewActivity.EXTRA_EDIT_RECIPE_TITLE);
        textViewRecipeTitle.setText(title);
    }

    private void callDeleteRecipe(int id) {
        Call<RegularStatus> call = recipeApiRef.deleteRecipe(id);
        call.enqueue(new Callback<RegularStatus>() {
            @Override
            public void onResponse(Call<RegularStatus> call, Response<RegularStatus> response) {
                if(!response.isSuccessful()) {
                    Log.e(getLocalClassName(), "callDeleteRecipe: status code " + response.code());
                    Toast.makeText(DeleteRecipeActivity.this, "Operation failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                RegularStatus rs = response.body();
                if(rs.isValid()) {
                    Intent intent = new Intent(DeleteRecipeActivity.this, UserRecipesActivity.class);
                    String recipeTitle = textViewRecipeTitle.getText().toString();
                    intent.putExtra(EXTRA_DELETE_SUCCESS, recipeTitle + " successfully deleted");
                    startActivity(intent);
                } else {
                    String errMsg = rs.getErrorMessage();
                    Toast.makeText(DeleteRecipeActivity.this, errMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegularStatus> call, Throwable t) {
                Log.e(getLocalClassName(), t.getMessage());
                Toast.makeText(DeleteRecipeActivity.this, "Operation failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
