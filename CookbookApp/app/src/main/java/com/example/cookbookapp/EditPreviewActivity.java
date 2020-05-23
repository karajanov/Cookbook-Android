package com.example.cookbookapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.cookbookapp.Interfaces.IRecipesApi;
import com.example.cookbookapp.Models.RecipePreview;
import com.example.cookbookapp.Utility.Validator;
import com.example.cookbookapp.Utility.RecipePreviewAdapter;
import com.example.cookbookapp.Utility.RetrofitBuilder;
import com.example.cookbookapp.Utility.UserSession;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditPreviewActivity extends AppCompatActivity
        implements RecipePreviewAdapter.OnItemClickListener {

    public static final String EXTRA_EDIT_RECIPE_ID = "com.example.cookbookapp.EXTRA_EDIT_RECIPE_ID";
    public static final String EXTRA_EDIT_RECIPE_TITLE = "com.example.cookbookapp.EXTRA_EDIT_RECIPE_TITLE";

    private RadioGroup radioGroup;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView textViewRecipeCount;
    private Retrofit rb = RetrofitBuilder.getBuilder(Validator.RECIPES_API_BASE);
    private IRecipesApi recipesApiRef = rb.create(IRecipesApi.class);
    private RecipePreviewAdapter recipePreviewAdapter;
    private String selectedOption = "update";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_preview);

        //Preventing landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        radioGroup = (RadioGroup) findViewById(R.id.radio_group_edit_options);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_four);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_edit_preview);
        textViewRecipeCount = (TextView) findViewById(R.id.text_view_recipe_count);

        addRadioGroupOptionsEventHandler();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SharedPreferences sp = getSharedPreferences(UserSession.SHARED_PREFS, MODE_PRIVATE);
        String currentUser = UserSession.getUser(sp);
        displayRecipesByAuthor(currentUser);
    }

    private void addRadioGroupOptionsEventHandler() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_btn_update_recipe:
                        selectedOption = "update";
                        break;
                    case R.id.radio_btn_delete_recipe:
                        selectedOption = "delete";
                        break;
                }
            }
        });
    }

    private void displayRecipesByAuthor(String author) {
        Call<List<RecipePreview>> call = recipesApiRef.getRecipePreviewByAuthor(author);
        call.enqueue(new Callback<List<RecipePreview>>() {
            @Override
            public void onResponse(Call<List<RecipePreview>> call, Response<List<RecipePreview>> response) {
                if(!response.isSuccessful()) {
                    Log.e(getLocalClassName(), "displayRecipesByAuthor: status code " + response.code());
                    return;
                }
                List<RecipePreview> recipePreviewList = response.body();
                textViewRecipeCount.setText("Recipe Count: " + recipePreviewList.size());
                recipePreviewAdapter = new RecipePreviewAdapter(EditPreviewActivity.this, recipePreviewList);
                recyclerView.setAdapter(recipePreviewAdapter);
                recipePreviewAdapter.setOnItemClickListener(EditPreviewActivity.this);
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(Call<List<RecipePreview>> call, Throwable t) {
                Log.e(getLocalClassName(), t.getMessage());
            }
        });
    }

    @Override
    public void onItemClick(int position, List<RecipePreview> recipePreviewList) {
        RecipePreview clickedItem = recipePreviewList.get(position);
        Intent intent;
        if(selectedOption.equals("update")) {
            intent = new Intent(EditPreviewActivity.this, UpdateRecipeActivity.class);
            intent.putExtra(EXTRA_EDIT_RECIPE_ID, clickedItem.getId());
        } else {
            intent = new Intent(EditPreviewActivity.this, DeleteRecipeActivity.class);
            intent.putExtra(EXTRA_EDIT_RECIPE_ID, clickedItem.getId());
            intent.putExtra(EXTRA_EDIT_RECIPE_TITLE, clickedItem.getTitle());
        }
        startActivity(intent);
    }
}
