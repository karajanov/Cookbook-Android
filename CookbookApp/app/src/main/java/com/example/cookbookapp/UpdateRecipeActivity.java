package com.example.cookbookapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cookbookapp.Interfaces.IRecipesApi;
import com.example.cookbookapp.Models.FullRecipeInfo;
import com.example.cookbookapp.Utility.Helper;
import com.example.cookbookapp.Utility.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UpdateRecipeActivity extends AppCompatActivity {

    private int recipeId;
    private Retrofit rb = RetrofitBuilder.getBuilder(Helper.RECIPES_API_BASE);
    private IRecipesApi recipesApiRef = rb.create(IRecipesApi.class);
    private TextView textViewTitle, textViewPrep, textViewCuisine, textViewCategory;
    private EditText editTextTitle, editTextPrep, editTextCuisine, editTextCategory, editTextInstructions;
    private Button resetInstructionsBtn, chooseImageBtn, removeImageBtn, updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_recipe);

        //Preventing landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        recipeId = intent.getIntExtra(EditPreviewActivity.EXTRA_EDIT_RECIPE_ID, 0);
        textViewTitle = (TextView) findViewById(R.id.text_view_update_title);
        textViewPrep = (TextView) findViewById(R.id.text_view_update_prep);
        textViewCuisine = (TextView) findViewById(R.id.text_view_update_cuisine);
        textViewCategory = (TextView) findViewById(R.id.text_view_update_category);
        editTextTitle = (EditText) findViewById(R.id.edit_text_update_title);
        editTextPrep = (EditText) findViewById(R.id.edit_text_update_prep);
        editTextCuisine = (EditText) findViewById(R.id.edit_text_update_cuisine);
        editTextCategory = (EditText) findViewById(R.id.edit_text_update_category);
        editTextInstructions = (EditText) findViewById(R.id.edit_text_ml_update_instr);
        resetInstructionsBtn = (Button) findViewById(R.id.btn_reset_instructions);
        chooseImageBtn = (Button) findViewById(R.id.btn_update_image);
        removeImageBtn = (Button) findViewById(R.id.btn_clear_image);
        updateBtn = (Button) findViewById(R.id.btn_confirm_update);

        setCurrentRecipeInfo();
    }

    private void setCurrentRecipeInfo() {
        Call<FullRecipeInfo> call = recipesApiRef.getFullRecipeInfoById(recipeId);
        call.enqueue(new Callback<FullRecipeInfo>() {
            @Override
            public void onResponse(Call<FullRecipeInfo> call, Response<FullRecipeInfo> response) {
                if(!response.isSuccessful()) {
                    Log.e(getLocalClassName(), "setCurrentRecipeInfo: status code " + response.code());
                    String msg = "Current info could not be loaded, try again later";
                    Toast.makeText(UpdateRecipeActivity.this, msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                FullRecipeInfo fullInfo = response.body();
                textViewTitle.setText("Title: " + fullInfo.getTitle());
                textViewPrep.setText("Prep Time: " + fullInfo.getPrepTime());
                textViewCuisine.setText("Cuisine: " + fullInfo.getCuisineTitle());
                textViewCategory.setText("Category: " + fullInfo.getCategoryTitle());
                editTextInstructions.setText(fullInfo.getInstructions());
            }
            @Override
            public void onFailure(Call<FullRecipeInfo> call, Throwable t) {
                Log.e(getLocalClassName(), t.getMessage());
                String msg = "Current info failed to load due to server issues, try again later";
                Toast.makeText(UpdateRecipeActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
