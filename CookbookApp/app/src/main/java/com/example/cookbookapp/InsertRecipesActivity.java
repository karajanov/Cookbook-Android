package com.example.cookbookapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.cookbookapp.Interfaces.IRecipesApi;
import com.example.cookbookapp.Models.Measurement;
import com.example.cookbookapp.Models.Recipe;
import com.example.cookbookapp.Models.RegularStatus;
import com.example.cookbookapp.Utility.FileUtils;
import com.example.cookbookapp.Utility.Helper;
import com.example.cookbookapp.Utility.RetrofitBuilder;
import com.example.cookbookapp.Utility.UserSession;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class InsertRecipesActivity extends AppCompatActivity {

    public static final String EXTRA_INSERT_SUCCESS = "com.example.cookbookapp.EXTRA_INSERT_SUCCESS";
    public static final int MIN_LENGTH = 3;
    public static final int MIN_LENGTH_INSTRUCTIONS = 15;

    private static final int CHOOSE_IMAGE_REQUEST = 1;
    private static final long MAX_IMAGE_BYTES = 1572864; // 1.5 MB
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
    private Long imageSize = null;
    private ArrayList<EditText> requiredEditTextList;
    private ArrayList<EditText> optionalEditTextList;

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
        requiredEditTextList = new ArrayList<EditText>();
        optionalEditTextList = new ArrayList<EditText>();

        textViewListOfIngredients.setVisibility(View.GONE);
        requiredEditTextList.add(editTextTitle);
        requiredEditTextList.add(editTextInstructions);
        optionalEditTextList.add(editTextPrepTime);
        optionalEditTextList.add(editTextCuisine);
        optionalEditTextList.add(editTextCategory);

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
                if (Helper.isFieldEmpty(editTextIngredient)) {
                    Helper.displayErrorMessage(editTextIngredient, "Empty field not allowed");
                    return;
                }
                if (!Helper.hasMinLengthTestPassed(editTextIngredient, MIN_LENGTH)) {
                    return;
                }
                if (!Helper.hasOptionalFieldMinLengthTestPassed(editTextConsistency, MIN_LENGTH)) {
                    return;
                }
                String ingredient = editTextIngredient.getText().toString();
                String quantity = Helper.setToNullIfEmpty(editTextQuantity
                        .getText()
                        .toString());
                String consistency = Helper.setToNullIfEmpty(editTextConsistency
                        .getText()
                        .toString());
                Measurement ms = new Measurement(ingredient, quantity, consistency);
                if (isMeasurementAdded(ms)) {
                    String msg = "Measurement already added";
                    Toast.makeText(InsertRecipesActivity.this, msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                msList.add(ms);
                textViewListOfIngredients.append(ms.toString() + "\n");
                if (textViewListOfIngredients.getVisibility() == View.GONE) {
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
            if (imageUri != null) {
                File f = FileUtils.getFile(InsertRecipesActivity.this, imageUri);
                selectedImageName = f.getName();
                imageSize = f.length();
                textViewImage.setText("Image: (" + selectedImageName + ")");
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
                if(!Helper.hasEmptyFieldTestPassed(requiredEditTextList)) {
                    return;
                }
                if(!Helper.hasMinLengthTestPassed(editTextTitle, MIN_LENGTH)) {
                    return;
                }
                if(!Helper.hasMinLengthTestPassed(editTextInstructions, MIN_LENGTH_INSTRUCTIONS)) {
                    return;
                }
                if(!Helper.hasOptionalFieldMinLengthTestPassed(editTextPrepTime, MIN_LENGTH)) {
                    return;
                }
                if(!Helper.hasOptionalFieldMinLengthTestPassed(editTextCuisine, MIN_LENGTH)) {
                    return;
                }
                if(!Helper.hasOptionalFieldMinLengthTestPassed(editTextCategory, MIN_LENGTH)) {
                    return;
                }
                if(msList.size() == 0) {
                    Helper.displayErrorMessage(editTextIngredient, "At least one measurement required");
                    return;
                }
                Helper.clearErrorField(editTextIngredient);
                if(imageUri != null && imageSize > MAX_IMAGE_BYTES) {
                    String sizeMsg = "Size of image exceeded max size of 1.5 MB";
                    Toast.makeText(InsertRecipesActivity.this, sizeMsg, Toast.LENGTH_LONG).show();
                    return;
                }
                SharedPreferences sp = getSharedPreferences(UserSession.SHARED_PREFS, MODE_PRIVATE);
                String currentUser = UserSession.getUser(sp);
                if(currentUser == null) {
                    String msg = "Must be logged in to insert a recipe";
                    Toast.makeText(InsertRecipesActivity.this, msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                String recipeTitle = editTextTitle.getText().toString();
                String prep = Helper.setToNullIfEmpty(editTextPrepTime
                        .getText()
                        .toString());
                String cuisine = Helper.setToNullIfEmpty(editTextCuisine
                        .getText()
                        .toString());
                String category = Helper.setToNullIfEmpty(editTextCategory
                        .getText()
                        .toString());
                String instructions = editTextInstructions.getText().toString();
               final Recipe recipe = new Recipe(
                        recipeTitle, prep,
                        cuisine, category,
                        instructions, currentUser
                );
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(InsertRecipesActivity.this);
                adBuilder.setTitle(R.string.insert_dialog_title);
                adBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmRecipeInsertion(recipe, msList);
                    }
                });
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

    private void confirmRecipeInsertion(Recipe recipe, ArrayList<Measurement> measurementList) {

        MultipartBody.Part imgFile = null;

        if (imageUri != null) {
            File originalFile = FileUtils.getFile(InsertRecipesActivity.this, imageUri);
            imgFile = getImageAsMultipart(originalFile);
        }

        RequestBody recipeRbody = RequestBody.create(MultipartBody.FORM, gsonConverter.toJson(recipe));
        RequestBody msRbody = RequestBody.create(MultipartBody.FORM, gsonConverter.toJson(measurementList));

        Call<RegularStatus> call = recipesApiRef.insertRecipe(recipeRbody, msRbody, imgFile);
        call.enqueue(new Callback<RegularStatus>() {
            @Override
            public void onResponse(Call<RegularStatus> call, Response<RegularStatus> response) {
                if (!response.isSuccessful()) {
                    Log.e(getLocalClassName(), "insertRecipe: status code " + response.code());
                    String errMsg = "Insertion failed";
                    Toast.makeText(InsertRecipesActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                    return;
                }
                RegularStatus rs = response.body();
                if(rs.isValid()) {
                    Intent intent = new Intent(InsertRecipesActivity.this, UserRecipesActivity.class);
                    String successMsg = "You successfully inserted your recipe";
                    intent.putExtra(EXTRA_INSERT_SUCCESS, successMsg);
                    startActivity(intent);
                } else {
                    String msg = rs.getErrorMessage();
                    Toast.makeText(InsertRecipesActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegularStatus> call, Throwable t) {
                Log.e(getLocalClassName(), t.getMessage());
                String errMsg = "Insertion failed, due to server issues";
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
                imageSize = null;
                selectedImageName = null;
                textViewImage.setText(R.string.image);
            }
        });
    }

    private void viewOrHideListEventHandler() {
        viewListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewListOfIngredients.getVisibility() == View.GONE) {
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
        if (msList.size() == 0) {
            String msg = "No items to be removed";
            Toast.makeText(InsertRecipesActivity.this, msg, Toast.LENGTH_SHORT).show();
        } else {
            msList.clear();
            textViewListOfIngredients.setText("");
        }
    }

    private void clearLastMeasurement() {
        if (msList.size() == 0) {
            String msg = "No items to be removed";
            Toast.makeText(InsertRecipesActivity.this, msg, Toast.LENGTH_SHORT).show();
        } else {
            msList.remove(msList.size() - 1);
            textViewListOfIngredients.setText("");
            for (int i = 0; i < msList.size(); ++i) {
                textViewListOfIngredients.append(msList.get(i).toString() + "\n");
            }
        }
    }

    private boolean isMeasurementAdded(Measurement m) {
        if (msList.size() == 0) {
            return false;
        }
        for (int i = 0; i < msList.size(); ++i) {
            if (msList.get(i).toString().equals(m.toString())) {
                return true;
            }
        }
        return false;
    }
}
