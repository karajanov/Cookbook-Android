package com.example.cookbookapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {

    private int recipeId;
    private String recipeTitle = null;
    private TextView textViewTitle, textViewPrep, textViewDetails;
    private ProgressBar progressBar;
    private RetrofitBuilder rb;
    private IRecipesApi recipesApiRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Preventing landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Initialization
        textViewTitle = (TextView) findViewById(R.id.text_view_details_title);
        textViewPrep = (TextView) findViewById(R.id.text_view_details_preptime);
        textViewDetails =(TextView) findViewById(R.id.text_view_details_scrollable);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_details);
        rb = new RetrofitBuilder();
        recipesApiRef = rb.getBuilder(LookupActivity.RECIPES_API_BASE).create(IRecipesApi.class);

        Intent intent = getIntent();
        setRecipeId(intent, LookupActivity.EXTRA_RECIPE_ID);
        setRecipeTitle(intent, LookupActivity.EXTRA_RECIPE_TITLE);

        textViewTitle.setText(recipeTitle);
        textViewDetails.setText("");
        progressBar.setVisibility(View.VISIBLE);
        setRecipeDetails();
        setIngredients();
    }

    private void setRecipeDetails() {
        Call<RecipeDetails>  call = recipesApiRef.getRecipeDetailsById(recipeId);

        call.enqueue(new Callback<RecipeDetails>() {
            @Override
            public void onResponse(Call<RecipeDetails> call, Response<RecipeDetails> response) {
                if(!response.isSuccessful()) {
                    Log.e(getLocalClassName(), "setRecipeDetails -> status code: " + response.code());
                    return;
                }

                RecipeDetails recipeDetails = response.body();
                textViewPrep.setText(recipeDetails.getPrepTime());

                String instructions = "\nInstructions:\n";
                textViewDetails.append(instructions + recipeDetails.getInstructions() + "\n");
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RecipeDetails> call, Throwable t) {
                Log.e(getLocalClassName(), t.getMessage());
                return;
            }
        });
    }

    private void setIngredients() {
        Call<List<RecipeMeasurement>> call = recipesApiRef.getRecipeMeasurementsById(recipeId);

        call.enqueue(new Callback<List<RecipeMeasurement>>() {
            @Override
            public void onResponse(Call<List<RecipeMeasurement>> call, Response<List<RecipeMeasurement>> response) {
                if(!response.isSuccessful()) {
                    Log.e(getLocalClassName(), "setIngredients -> status code: " + response.code());
                    return;
                }

                String ingredients = "\nIngredient - Quantity - Consistency";
                List<RecipeMeasurement> recipeMsList = response.body();
                for(RecipeMeasurement rm : recipeMsList) {
                    String m = "\n"
                            + rm.getIngredient() + " - "
                            + rm.getQuantity() + " - "
                            + rm.getConsistency() + "\n";
                    textViewDetails.append(m);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<RecipeMeasurement>> call, Throwable t) {
                Log.e(getLocalClassName(), t.getMessage());
                return;
            }
        });
    }

    private void setRecipeId(Intent intent, String extra) {
        recipeId = intent.getIntExtra(extra, -1);
    }

    private void setRecipeTitle(Intent intent, String extra) {
        recipeTitle = intent.getStringExtra(extra);
    }
}
