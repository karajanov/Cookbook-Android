package com.example.cookbookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LookupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String RECIPES_API_BASE = "http://localhost:5000/api/";

    private RadioGroup radioGroupFilter;
    private TextView groupLabel;
    private Spinner itemSpinner;
    private RetrofitBuilder rb;
    private IRecipesApi recipesApiRef;
    private ProgressBar progressBar;
    private Button buttonSearh;
    private RecyclerView recyclerViewRecipe;
    private RecipePreviewAdapter recipePreviewAdapter;
    private String selectedFilter = "";
    private String selectedSpinnerItem = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup);

        //Preventing landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Initialization
        radioGroupFilter = (RadioGroup) findViewById(R.id.rgroup_filter);
        groupLabel = (TextView) findViewById(R.id.textview_filter_items);
        itemSpinner = (Spinner) findViewById(R.id.spinner_items);
        progressBar = (ProgressBar) findViewById(R.id.progressbar_one);
        recyclerViewRecipe = (RecyclerView) findViewById(R.id.recycler_view_recipe);
        buttonSearh = (Button) findViewById(R.id.btn_search_recipe);
        rb = new RetrofitBuilder();
        recipesApiRef = rb.getBuilder(RECIPES_API_BASE).create(IRecipesApi.class);

        //Event handlers
        addRadioGroupEventHandler();
        addFilterItems("category", itemSpinner);
        itemSpinner.setOnItemSelectedListener(this);
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewRecipe.setHasFixedSize(true);
        recyclerViewRecipe.setLayoutManager(new LinearLayoutManager(this));
        addSearchEventHandler();

    }

    // <MENU>
    @Override // Embedding the menu in the activity
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.general, menu);
        return true;
    }

    @Override // Event handler for selecting a menu item
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.general_login_item:
                //
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // </MENU>

    // <SPINNER>
    @Override // When an item from the spinner is selected - event handler
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedSpinnerItem = parent.getItemAtPosition(position).toString();
    }

    @Override // When nothing from the spinner has been selected - event handler
    public void onNothingSelected(AdapterView<?> parent) {
    }
    // </SPINNER>

    //Event handler for selecting a specific radio button
    private void addRadioGroupEventHandler() {
        radioGroupFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_btn_category:
                        groupLabel.setText(R.string.category_items);
                        addFilterItems("category", itemSpinner);
                        selectedFilter = "category";
                        break;
                    case R.id.radio_btn_cuisine:
                        groupLabel.setText(R.string.cuisine_items);
                        addFilterItems("cuisine", itemSpinner);
                        selectedFilter = "cuisine";
                        break;
                }
            }
        });
    }

    //Adding specific items to the spinner
    private void addFilterItems(String filter, final Spinner s) {
        if (filter == null)
            return;

        Call<List<String>> call = (filter == "category") ?
                recipesApiRef.getCategoryTitles() : recipesApiRef.getCuisineTitles();

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (!response.isSuccessful()) {
                    Log.e(getLocalClassName(), "addFilterItems -> status: " + response.code());
                    return;
                }
                List<String> titles = response.body();
                addItemsToSpinner(titles, s);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e(getLocalClassName(), t.getMessage());
                return;
            }
        });
    }

    //Generic function for adding items to a spinner
    private <T> void addItemsToSpinner(List<T> inputArr, Spinner s) {
        ArrayAdapter<T> adapter = new ArrayAdapter<T>(this, android.R.layout.simple_spinner_item, inputArr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
    }

    //List of recipe previews placed inside of a recycler view
    private void addItemsToRecyclerView(String filter, String item) {
        if(filter == null)
            return;

        Call<List<RecipePreview>> call = (filter == "category") ?
                recipesApiRef.getRecipePreviewByCategory(item) : recipesApiRef.getRecipePreviewByCuisine(item);

        call.enqueue(new Callback<List<RecipePreview>>() {
            @Override
            public void onResponse(Call<List<RecipePreview>> call, Response<List<RecipePreview>> response) {
                if(!response.isSuccessful()) {
                    Log.e(getLocalClassName(), "addFilterItems -> status: " + response.code());
                    return;
                }
                recipePreviewAdapter = new RecipePreviewAdapter(LookupActivity.this, response.body());
                recyclerViewRecipe.setAdapter(recipePreviewAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<RecipePreview>> call, Throwable t) {
                Log.e(getLocalClassName(), t.getMessage());
                return;
            }
        });
    }

    //Search button event listener
    private void addSearchEventHandler() {
        buttonSearh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                addItemsToRecyclerView(selectedFilter, selectedSpinnerItem);
            }
        });
    }
}
