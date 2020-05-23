package com.example.cookbookapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cookbookapp.Interfaces.IRecipesApi;
import com.example.cookbookapp.Models.RecipePreview;
import com.example.cookbookapp.Utility.*;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SecondLookupActivity extends AppCompatActivity
implements RecipePreviewAdapter.OnItemClickListener {

    private ConstraintLayout layout;
    private RadioGroup radioGroup;
    private EditText editTextSearch;
    private Button buttonSearch;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private int selectedFilter = 1;
    private Retrofit rb = RetrofitBuilder.getBuilder(Validator.RECIPES_API_BASE);
    private IRecipesApi recipesApiRef;
    private RecipePreviewAdapter recipePreviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_lookup);

        //Preventing landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Initialization
        layout = (ConstraintLayout) findViewById(R.id.lookup_two_layout);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup_title_filter);
        editTextSearch = (EditText) findViewById(R.id.edit_text_search);
        buttonSearch = (Button) findViewById(R.id.btn_title_search);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_three);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_two);
        recipesApiRef = rb.create(IRecipesApi.class);

        //Event Handlers
        progressBar.setVisibility(View.GONE);
        addRadioGroupEventHandler();
        addSearchButtonEventHandler();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void addRadioGroupEventHandler() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
               switch (checkedId) {
                   case R.id.radio_btn_startsWith:
                       selectedFilter = 0;
                       break;
                   case R.id.radio_btn_exact:
                       selectedFilter = 1;
                       break;
                   case R.id.radio_btn_contains:
                       selectedFilter = 2;
                       break;
               }
            }
        });
    }

    private void addSearchButtonEventHandler() {
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerm = editTextSearch.getText().toString();
                hideKeyboard();
                Validator.removeFocus(editTextSearch);
                if(!TextUtils.isEmpty(searchTerm)) {
                    progressBar.setVisibility(View.VISIBLE);
                    displayRecipePreviews(searchTerm);
                }
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
    }

    private void displayRecipePreviews(String searchTerm) {
        Call<List<RecipePreview>> call = null;
        switch(selectedFilter) {
            case 0:
                call = recipesApiRef.getRecipePreviewThatStartsWithKey(searchTerm);
                break;
            case 1:
                call = recipesApiRef.getRecipePreviewByExactTitle(searchTerm);
                break;
            case 2:
                call = recipesApiRef.getRecipePreviewThatContainsKey(searchTerm);
                break;

        }
        call.enqueue(new Callback<List<RecipePreview>>() {
            @Override
            public void onResponse(Call<List<RecipePreview>> call, Response<List<RecipePreview>> response) {
                if(!response.isSuccessful()) {
                    Log.e(getLocalClassName(), "displayRecipePreview -> status code: " + response.code());
                    return;
                }
                if(response.body().size() == 0) {
                    Toast.makeText(SecondLookupActivity.this, "No Recipes Found", Toast.LENGTH_SHORT).show();
                }
                recipePreviewAdapter = new RecipePreviewAdapter(SecondLookupActivity.this, response.body());
                recyclerView.setAdapter(recipePreviewAdapter);
                recipePreviewAdapter.setOnItemClickListener(SecondLookupActivity.this);
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

        Intent intent = new Intent(SecondLookupActivity.this, DetailsActivity.class);
        RecipePreview clickedItem = recipePreviewList.get(position);
        intent.putExtra(LookupActivity.EXTRA_RECIPE_ID, clickedItem.getId());
        intent.putExtra(LookupActivity.EXTRA_RECIPE_TITLE, clickedItem.getTitle());
        startActivity(intent);
    }
}
