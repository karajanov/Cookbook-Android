package com.example.cookbookapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cookbookapp.Interfaces.IRecipesApi;
import com.example.cookbookapp.Models.Measurement;
import com.example.cookbookapp.Models.Recipe;
import com.example.cookbookapp.Models.RecipeMeasurement;
import com.example.cookbookapp.Utility.FileUtils;
import com.example.cookbookapp.Utility.Helper;
import com.example.cookbookapp.Utility.RetrofitBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonSerializer;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Multipart;

import static java.security.AccessController.getContext;

public class InsertRecipesActivity extends AppCompatActivity {

    public static final int MIN_LENGTH = 3;

    private static final int CHOOSE_IMAGE_REQUEST = 1;
    private ConstraintLayout layout;
    private TextView textViewListOfIngredients, textViewImage;
    private Button chooseImageBtn, addToListBtn, viewListBtn, confirmInsertBtn, removeImgBtn;
    private EditText editTextTitle, editTextPrepTime, editTextCuisine, editTextCategory, editTextInstructions;
    private EditText editTextIngredient, editTextQuantity, editTextConsistency;
    private Retrofit rb = RetrofitBuilder.getBuilder(Helper.RECIPES_API_BASE);
    private IRecipesApi recipesApiRef = rb.create(IRecipesApi.class);
    private Uri imageUri;
    private ArrayList<Measurement> msList;
    private static final Gson gsonConverter = new Gson();
    private String selectedImageName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_recipes);

        //Preventing landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Initialization
        layout = (ConstraintLayout) findViewById(R.id.insert_recipes_layout);
        chooseImageBtn = (Button) findViewById(R.id.btn_choose_image);
        addToListBtn = (Button) findViewById(R.id.btn_add_to_list);
        viewListBtn = (Button) findViewById(R.id.btn_view_hide_list);
        confirmInsertBtn = (Button) findViewById(R.id.btn_confirm_insert);
        removeImgBtn = (Button) findViewById(R.id.btn_remove_image);
        textViewListOfIngredients = (TextView) findViewById(R.id.text_view_list_ingredients);
        textViewImage = (TextView) findViewById(R.id.text_view_insert_img);
        editTextTitle = (EditText) findViewById(R.id.edit_text_insert_title);
        editTextPrepTime = (EditText) findViewById(R.id.edit_text_insert_prep);
        editTextCuisine = (EditText) findViewById(R.id.edit_text_insert_cuisine);
        editTextCategory = (EditText) findViewById(R.id.edit_text_insert_category);
        editTextInstructions = (EditText) findViewById(R.id.edit_text_ml_instr);
        editTextIngredient = (EditText) findViewById(R.id.edit_text_insert_ingredient);
        editTextQuantity = (EditText) findViewById(R.id.edit_text_insert_quantity);
        editTextConsistency = (EditText) findViewById(R.id.edit_text_insert_consistency);
        msList = new ArrayList<Measurement>();

        textViewListOfIngredients.setVisibility(View.GONE);

        //Event handlers
        confirmInsertEventHandler();
        chooseImageEventHandler();
        viewOrHideListEventHandler();
        addToListEventHandler();
        removeImageEventHandler();
    }

    @Override // Embedding the menu in the activity
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recipe_help_menu, menu);
        return true;
    }

    @Override // Menu items event handler
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_hide_keyboard:
                hideKeyboard();
                return true;
            case R.id.menu_item_clear_all:
                clearAllMeasurements();
                return true;
            case R.id.menu_item_clear_last_ingredient:
              clearLastMeasurement();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
    }

    private void addToListEventHandler() {
        addToListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Helper.isFieldEmpty(editTextIngredient)) {
                    Helper.displayErrorMessage(editTextIngredient, "Empty field not allowed");
                    return;
                }
                if(!Helper.hasMinLengthTestPassed(editTextIngredient, MIN_LENGTH)) {
                    return;
                }
                if(!Helper.hasOptionalFieldMinLengthTestPassed(editTextConsistency, MIN_LENGTH)) {
                    return;
                }
                String ingredient = editTextIngredient.getText().toString();
                String quantity = editTextQuantity.getText().toString();
                String consistency = editTextConsistency.getText().toString();
                Measurement ms = new Measurement(ingredient, quantity, consistency);
                if(isMeasurementAdded(ms)) {
                    String msg = "Measurement already added";
                    Toast.makeText(InsertRecipesActivity.this, msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                msList.add(ms);
                textViewListOfIngredients.append(ms.toString() + "\n");
                if(textViewListOfIngredients.getVisibility() == View.GONE) {
                    String addedItemMsg = "New item added";
                    Toast.makeText(InsertRecipesActivity.this, addedItemMsg, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void chooseImageEventHandler() {
        chooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            if(imageUri != null) {
                File f = FileUtils.getFile(InsertRecipesActivity.this, imageUri);
                selectedImageName = f.getName();
                textViewImage.setText("Image: (" + selectedImageName +")");
            }
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CHOOSE_IMAGE_REQUEST);
    }

    private void confirmInsertEventHandler() {
        confirmInsertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Recipe r = new Recipe("","","","",null,null);
                confirmRecipeInsertion(r, msList);
            }
        });
    }

    private void confirmRecipeInsertion(Recipe recipe, ArrayList<Measurement> measurementList) {

        MultipartBody.Part imgFile = null;

        if(imageUri != null) {
            File originalFile = FileUtils.getFile(InsertRecipesActivity.this, imageUri);
            imgFile = getImageAsMultipart(originalFile);
        }

        RequestBody recipeRbody = RequestBody.create(MultipartBody.FORM, gsonConverter.toJson(recipe));
        RequestBody msRbody = RequestBody.create(MultipartBody.FORM, gsonConverter.toJson(measurementList));

        Call<Boolean> call = recipesApiRef.insertRecipe(recipeRbody, msRbody, imgFile);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (!response.isSuccessful()) {
                    Log.e(getLocalClassName(), "insertRecipe: status code " + response.code());
                    String errMsg = "Operation failed";
                    Toast.makeText(InsertRecipesActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                    return;
                }
                Boolean b = response.body();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(getLocalClassName(), t.getMessage());
                String errMsg = "Insertion failed";
                Toast.makeText(InsertRecipesActivity.this, errMsg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private MultipartBody.Part getImageAsMultipart(File originalFile) {
           RequestBody filePart = RequestBody.create(MediaType.parse("image/*"), originalFile);
           MultipartBody.Part file = MultipartBody.Part
                   .createFormData("image", originalFile.getName(), filePart);

        return file;
    }

    private void removeImageEventHandler() {
        removeImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              imageUri = null;
              selectedImageName = null;
              textViewImage.setText(R.string.image);
            }
        });
    }

    private void viewOrHideListEventHandler() {
        viewListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textViewListOfIngredients.getVisibility() == View.GONE) {
                    textViewListOfIngredients.setVisibility(View.VISIBLE);
                    viewListBtn.setText(R.string.hide_list);
                } else {
                    textViewListOfIngredients.setVisibility(View.GONE);
                    viewListBtn.setText(R.string.view_list);
                }
            }
        });
    }

    private void clearAllMeasurements() {
        if(msList.size() == 0) {
            String msg = "No items to be removed";
            Toast.makeText(InsertRecipesActivity.this, msg, Toast.LENGTH_SHORT).show();
        } else {
            msList.clear();
            textViewListOfIngredients.setText("");
        }
    }

    private void clearLastMeasurement() {
        if(msList.size() == 0) {
            String msg = "No items to be removed";
            Toast.makeText(InsertRecipesActivity.this, msg, Toast.LENGTH_SHORT).show();
        } else {
            msList.remove(msList.size() - 1);
            textViewListOfIngredients.setText("");
            for(int i = 0; i < msList.size(); ++i) {
                textViewListOfIngredients.append(msList.get(i).toString()+"\n");
            }
        }
    }

    private boolean isMeasurementAdded(Measurement m) {
        if(msList.size() == 0) {
            return false;
        }
        for(int i = 0; i < msList.size(); ++i) {
            if(msList.get(i).toString().equals(m.toString())) {
                return true;
            }
        }
        return false;
    }
}