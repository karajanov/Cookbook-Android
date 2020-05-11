package com.example.cookbookapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cookbookapp.Interfaces.IRecipesApi;
import com.example.cookbookapp.Models.FullRecipeInfo;
import com.example.cookbookapp.Models.Measurement;
import com.example.cookbookapp.Utility.Helper;
import com.example.cookbookapp.Utility.MeasurementAdapter;
import com.example.cookbookapp.Utility.RetrofitBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DetailsActivity extends AppCompatActivity {

    private int recipeId;
    private TextView textViewTitle, textViewPrep,
            textViewCuisine, textViewCategory,
            textViewInstructions, textViewMeasurements;
    private RecyclerView recyclerViewMeasurements;
    private MeasurementAdapter measurementAdapter;
    private Retrofit rb = RetrofitBuilder.getBuilder(Helper.RECIPES_API_BASE);
    private IRecipesApi recipesApiRef = rb.create(IRecipesApi.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Preventing landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Initialization
        textViewTitle = (TextView) findViewById(R.id.text_view_details_title);
        textViewPrep = (TextView) findViewById(R.id.text_view_details_preptime);
        textViewCuisine = (TextView) findViewById(R.id.text_view_details_cuisine);
        textViewCategory = (TextView) findViewById(R.id.text_view_details_category);
        textViewInstructions =(TextView) findViewById(R.id.text_view_details_instructions);
        textViewMeasurements = (TextView) findViewById(R.id.text_view_details_measurements);
        recyclerViewMeasurements = (RecyclerView) findViewById(R.id.recycler_view_details_measurements);
        Intent intent = getIntent();
        recipeId = intent.getIntExtra(LookupActivity.EXTRA_RECIPE_ID, 0);

        recyclerViewMeasurements.setHasFixedSize(true);
        recyclerViewMeasurements.setLayoutManager(new LinearLayoutManager(this));
        setRecipeInfo();
        setMeasurements();
    }

    private void setRecipeInfo() {
        Call<FullRecipeInfo> call = recipesApiRef.getFullRecipeInfoById(recipeId);
        call.enqueue(new Callback<FullRecipeInfo>() {
            @Override
            public void onResponse(Call<FullRecipeInfo> call, Response<FullRecipeInfo> response) {
                if(!response.isSuccessful()) {
                    Log.e(getLocalClassName(), "setRecipeInfo in DetailsActivity: status code " + response.code());
                    String msg = "Info could not be obtained, try again later.";
                    Toast.makeText(DetailsActivity.this, msg, Toast.LENGTH_LONG).show();
                    return;
                }
                FullRecipeInfo fullRecipeInfo = response.body();
                String title = fullRecipeInfo.getTitle();
                String prep = fullRecipeInfo.getPrepTime();
                String cuisine = fullRecipeInfo.getCuisineTitle();
                String category = fullRecipeInfo.getCategoryTitle();
                String instructions = fullRecipeInfo.getInstructions();

                textViewTitle.setText("Title: " + title);
                textViewPrep.setText("Prep Time: " + prep);
                textViewCuisine.setText("Cuisine: " + cuisine);
                textViewCategory.setText("Category: " + category);
                textViewInstructions.setText(instructions);
            }
            @Override
            public void onFailure(Call<FullRecipeInfo> call, Throwable t) {
                Log.e(getLocalClassName(), t.getMessage());
                String msg = "Info could not be obtained due to server issues";
                Toast.makeText(DetailsActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setMeasurements() {
      Call<List<Measurement>> call = recipesApiRef.getRecipeMeasurementsById(recipeId);
      call.enqueue(new Callback<List<Measurement>>() {
          @Override
          public void onResponse(Call<List<Measurement>> call, Response<List<Measurement>> response) {
              if(!response.isSuccessful()) {
                  Log.e(getLocalClassName(), "setMeasurements in DetailsActivity: status code " + response.code());
                  String msg = "Measurements could not be obtained, try again later.";
                  Toast.makeText(DetailsActivity.this, msg, Toast.LENGTH_LONG).show();
                  return;
              }
              List<Measurement> measurementList = response.body();
              textViewMeasurements.setText("Measurements: " + measurementList.size());
              measurementAdapter = new MeasurementAdapter(DetailsActivity.this, measurementList);
              recyclerViewMeasurements.setAdapter(measurementAdapter);
          }

          @Override
          public void onFailure(Call<List<Measurement>> call, Throwable t) {
              Log.e(getLocalClassName(), t.getMessage());
              String msg = "Measurements could not be obtained due to server issues";
              Toast.makeText(DetailsActivity.this, msg, Toast.LENGTH_LONG).show();
          }
      });

    }
}
