package com.example.cookbookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LookupActivity extends AppCompatActivity {

    public static final String RECIPES_API_BASE = "http://localhost:5000/api/";

    private RadioGroup radioGroupFilter;
    private TextView groupLabel;
    private Spinner itemSpinner;
    private RetrofitBuilder rb;
    private IRecipesApi recipesApiRef;

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
        rb = new RetrofitBuilder();
        recipesApiRef = rb.getBuilder(RECIPES_API_BASE).create(IRecipesApi.class);

        //Event handlers
        addRadioGroupEventHandler();
        addFilterItems("category", itemSpinner);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.general, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.general_login_item:
                //
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addRadioGroupEventHandler() {
        radioGroupFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_btn_category:
                        groupLabel.setText(R.string.category_items);
                        addFilterItems("category", itemSpinner);
                        break;
                    case R.id.radio_btn_cuisine:
                        groupLabel.setText(R.string.cuisine_items);
                        addFilterItems("cuisine", itemSpinner);
                        break;
                }
            }
        });
    }

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
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e(getLocalClassName(), t.getMessage());
                return;
            }
        });
    }

    private <T> void addItemsToSpinner(List<T> inputArr, Spinner s) {
        ArrayAdapter<T> adapter = new ArrayAdapter<T>(this, android.R.layout.simple_spinner_item, inputArr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
    }
}
