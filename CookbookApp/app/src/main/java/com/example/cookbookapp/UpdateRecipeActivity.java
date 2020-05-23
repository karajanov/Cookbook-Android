package com.example.cookbookapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.icu.util.Measure;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cookbookapp.Interfaces.IRecipesApi;
import com.example.cookbookapp.Models.FullRecipeInfo;
import com.example.cookbookapp.Models.Measurement;
import com.example.cookbookapp.Models.Recipe;
import com.example.cookbookapp.Models.RegularStatus;
import com.example.cookbookapp.Utility.FileUtils;
import com.example.cookbookapp.Utility.ImageUtils;
import com.example.cookbookapp.Utility.UserSession;
import com.example.cookbookapp.Utility.Validator;
import com.example.cookbookapp.Utility.MeasurementAdapter;
import com.example.cookbookapp.Utility.RetrofitBuilder;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UpdateRecipeActivity extends AppCompatActivity
        implements MeasurementAdapter.OnItemClickListener {

    public static final String EXTRA_UPDATE_SUCCESS = "com.example.cookbookapp.EXTRA_UPDATE_SUCCESS";
    private static final int CHOOSE_IMAGE_REQUEST = 1;

    private int recipeId;
    private Retrofit rb = RetrofitBuilder.getBuilder(Validator.RECIPES_API_BASE);
    private IRecipesApi recipesApiRef = rb.create(IRecipesApi.class);
    private ConstraintLayout layout;
    private TextView textViewTitle, textViewPrep, textViewCuisine, textViewCategory, textViewMeasurements;
    private EditText editTextTitle, editTextPrep, editTextCuisine, editTextCategory, editTextInstructions;
    private Button resetInstructionsBtn, chooseImageBtn, removeImageBtn, confirmUpdateBtn;
    private RecyclerView recyclerViewCurrentMeasurements;
    private MeasurementAdapter msAdapter;
    private TextView textViewIngredient, textViewQuantity, textViewConsistency;
    private TextView textViewImage;
    private EditText editTextQuantity, editTextConsistency;
    private LinearLayout linearLayoutMeasurementForm;
    private Button viewMsFormBtn, updateMsBtn;
    private boolean isMeasurementSelected = false;
    private int positionOfSelectedMs = -1;
    private CheckBox checkBoxTitle, checkBoxPrep, checkBoxCuisine, checkBoxCategory;
    private CheckBox checkBoxQuantity, checkBoxConsistency;
    private ArrayList<EditText> requiredEditTextList = new ArrayList<EditText>();
    private ArrayList<CheckBox> checkBoxArrayList = new ArrayList<CheckBox>();
    private Uri imageUri;
    private String selectedImageName = null;
    private Long imageSize = null;
    private static final Gson gsonConverter = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_recipe);

        //Preventing landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        recipeId = intent.getIntExtra(EditPreviewActivity.EXTRA_EDIT_RECIPE_ID, 0);
        layout = (ConstraintLayout) findViewById(R.id.update_recipe_layout);
        textViewTitle = (TextView) findViewById(R.id.text_view_update_title);
        textViewPrep = (TextView) findViewById(R.id.text_view_update_prep);
        textViewCuisine = (TextView) findViewById(R.id.text_view_update_cuisine);
        textViewCategory = (TextView) findViewById(R.id.text_view_update_category);
        textViewMeasurements = (TextView) findViewById(R.id.text_view_update_measurements);
        textViewImage = (TextView) findViewById(R.id.text_view_update_img);
        editTextTitle = (EditText) findViewById(R.id.edit_text_update_title);
        editTextPrep = (EditText) findViewById(R.id.edit_text_update_prep);
        editTextCuisine = (EditText) findViewById(R.id.edit_text_update_cuisine);
        editTextCategory = (EditText) findViewById(R.id.edit_text_update_category);
        editTextInstructions = (EditText) findViewById(R.id.edit_text_ml_update_instr);
        resetInstructionsBtn = (Button) findViewById(R.id.btn_reset_instructions);
        chooseImageBtn = (Button) findViewById(R.id.btn_update_image);
        removeImageBtn = (Button) findViewById(R.id.btn_clear_image);
        confirmUpdateBtn = (Button) findViewById(R.id.btn_confirm_update);
        recyclerViewCurrentMeasurements = (RecyclerView) findViewById(R.id.recycler_view_update_measurements);
        textViewIngredient = (TextView) findViewById(R.id.text_view_update_ingredient);
        textViewQuantity = (TextView) findViewById(R.id.text_view_update_quantity);
        textViewConsistency = (TextView) findViewById(R.id.text_view_update_consistency);
        editTextQuantity = (EditText) findViewById(R.id.edit_text_update_quantity);
        editTextConsistency = (EditText) findViewById(R.id.edit_text_update_consistency);
        linearLayoutMeasurementForm = (LinearLayout) findViewById(R.id.linear_layout_update_ms_form);
        viewMsFormBtn = (Button) findViewById(R.id.btn_view_ms_form);
        updateMsBtn = (Button) findViewById(R.id.btn_update_ms);
        checkBoxTitle = (CheckBox) findViewById(R.id.checkBox_keep_title);
        checkBoxPrep = (CheckBox) findViewById(R.id.checkBox_keep_prep);
        checkBoxCuisine = (CheckBox) findViewById(R.id.checkBox_keep_cuisine);
        checkBoxCategory = (CheckBox) findViewById(R.id.checkBox_keep_category);
        checkBoxQuantity = (CheckBox) findViewById(R.id.checkBox_quantity_empty);
        checkBoxConsistency = (CheckBox) findViewById(R.id.checkBox_consistency_empty);

        requiredEditTextList.add(editTextTitle);
        requiredEditTextList.add(editTextInstructions);

        checkBoxArrayList.add(checkBoxTitle);
        checkBoxArrayList.add(checkBoxPrep);
        checkBoxArrayList.add(checkBoxCuisine);
        checkBoxArrayList.add(checkBoxCategory);

        recyclerViewCurrentMeasurements.setHasFixedSize(true);
        recyclerViewCurrentMeasurements.setLayoutManager(new LinearLayoutManager(this));
        linearLayoutMeasurementForm.setVisibility(View.GONE);
        setCurrentRecipeInfo();
        setCurrentMeasurements();
        setHandlerForInstructionsReset();
        setHandlerForMeasurementForm();
        setHandlerForMeasurementUpdate();
        setHandlerForChoosingImages();
        setHandlerForRemovingImage();
        setHandlerForCurrentItemCheckbox();
        setHandlerForRecipeUpdate();
    }

    @Override // Embedding the menu in the activity
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recipe_help_menu, menu);
        return true;
    }

    @Override //Menu preparation, right before it is shown
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem clearAllIngredientsBtn = menu.findItem(R.id.menu_item_clear_all);
        MenuItem clearLastIngredientBtn = menu.findItem(R.id.menu_item_clear_last_ingredient);
        clearAllIngredientsBtn.setVisible(false);
        clearLastIngredientBtn.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override // Menu items event handler
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_hide_keyboard:
                hideKeyboard();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(int position, List<Measurement> msList) {
        if (linearLayoutMeasurementForm.getVisibility() == View.VISIBLE) {
            isMeasurementSelected = true;
            positionOfSelectedMs = position;
            Measurement m = msList.get(position);
            textViewIngredient.setText(m.getIngredientTitle());
            textViewQuantity.setText("Quantity: " + m.getQuantity());
            textViewConsistency.setText("Consistency: " + m.getConsistency());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                File f = FileUtils.getFile(UpdateRecipeActivity.this, imageUri);
                selectedImageName = f.getName();
                imageSize = f.length();
                textViewImage.setText("Image: (" + selectedImageName + ")");
            }
        }
    }

    private void setCurrentRecipeInfo() {
        Call<FullRecipeInfo> call = recipesApiRef.getFullRecipeInfoById(recipeId);
        call.enqueue(new Callback<FullRecipeInfo>() {
            @Override
            public void onResponse(Call<FullRecipeInfo> call, Response<FullRecipeInfo> response) {
                if (!response.isSuccessful()) {
                    Log.e(getLocalClassName(), "setCurrentRecipeInfo: status code " + response.code());
                    String msg = "Current info could not be loaded, try again later";
                    Toast.makeText(UpdateRecipeActivity.this, msg, Toast.LENGTH_LONG).show();
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
                Toast.makeText(UpdateRecipeActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setHandlerForInstructionsReset() {
        resetInstructionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<List<String>> call = recipesApiRef.getInstructionsById(recipeId);
                call.enqueue(new Callback<List<String>>() {
                    @Override
                    public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                        if (!response.isSuccessful()) {
                            Log.e(getLocalClassName(), "resetInstructions: status code " + response.code());
                            String msg = "Instructions could not be loaded, try again later";
                            Toast.makeText(UpdateRecipeActivity.this, msg, Toast.LENGTH_LONG).show();
                            return;
                        }
                        List<String> instrList = response.body();
                        editTextInstructions.setText(instrList.get(0));
                    }

                    @Override
                    public void onFailure(Call<List<String>> call, Throwable t) {
                        Log.e(getLocalClassName(), t.getMessage());
                        String msg = "Instructions failed to load due to server issues, try again later";
                        Toast.makeText(UpdateRecipeActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void setCurrentMeasurements() {
        Call<List<Measurement>> call = recipesApiRef.getRecipeMeasurementsById(recipeId);
        call.enqueue(new Callback<List<Measurement>>() {
            @Override
            public void onResponse(Call<List<Measurement>> call, Response<List<Measurement>> response) {
                if (!response.isSuccessful()) {
                    Log.e(getLocalClassName(), "setCurrentMeasurements: status code " + response.code());
                    String msg = "Measurements could not be loaded, try again later";
                    Toast.makeText(UpdateRecipeActivity.this, msg, Toast.LENGTH_LONG).show();
                    return;
                }
                List<Measurement> msList = response.body();
                textViewMeasurements.setText("Measurements: " + msList.size());
                msAdapter = new MeasurementAdapter(UpdateRecipeActivity.this, msList);
                recyclerViewCurrentMeasurements.setAdapter(msAdapter);
                msAdapter.setOnItemClickListener(UpdateRecipeActivity.this);
            }

            @Override
            public void onFailure(Call<List<Measurement>> call, Throwable t) {
                Log.e(getLocalClassName(), t.getMessage());
                String msg = "Measurements failed to load due to server issues, try again later";
                Toast.makeText(UpdateRecipeActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setHandlerForMeasurementForm() {
        viewMsFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearLayoutMeasurementForm.getVisibility() == View.VISIBLE) {
                    linearLayoutMeasurementForm.setVisibility(View.GONE);
                    viewMsFormBtn.setText(R.string.open_form);
                } else {
                    linearLayoutMeasurementForm.setVisibility(View.VISIBLE);
                    viewMsFormBtn.setText(R.string.close_form);
                }
            }
        });
    }

    private void setHandlerForMeasurementUpdate() {
        updateMsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMeasurementSelected) {
                    String msg = "No measurement selected";
                    Toast.makeText(UpdateRecipeActivity.this, msg, Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (!checkBoxQuantity.isChecked()) {
                    if (Validator.isFieldEmpty(editTextQuantity)) {
                        Validator.displayErrorMessage(editTextQuantity, getString(R.string.empty_field_error_msg));
                        return;
                    }
                    if (!Validator.hasMinLengthTestPassed(editTextQuantity, 3)) {
                        Validator.displayErrorMessage(editTextQuantity, "Min length of field is 3");
                        return;
                    }
                }
                if (!checkBoxConsistency.isChecked()) {
                    if (Validator.isFieldEmpty(editTextConsistency)) {
                        Validator.displayErrorMessage(editTextConsistency, getString(R.string.empty_field_error_msg));
                        return;
                    }
                    if (!Validator.hasMinLengthTestPassed(editTextConsistency, 3)) {
                        Validator.displayErrorMessage(editTextConsistency, "Min length of field is 3");
                        return;
                    }
                }
                Validator.clearErrorField(editTextQuantity);
                Validator.clearErrorField(editTextConsistency);
                if (msAdapter != null) {
                    if (positionOfSelectedMs == -1) {
                        return;
                    }
                    List<Measurement> currentMsList = msAdapter.getMeasurementList();
                    Measurement selectedMs = currentMsList.get(positionOfSelectedMs);
                    int selectedMsId = selectedMs.getId();
                    currentMsList.remove(positionOfSelectedMs);
                    String ingredient = textViewIngredient.getText().toString();
                    String quantity = Validator.setToNullIfEmpty(editTextQuantity.getText().toString());
                    String consistency = Validator.setToNullIfEmpty(editTextConsistency.getText().toString());
                    Measurement newMs = new Measurement(ingredient, quantity, consistency);
                    newMs.setId(selectedMsId);
                    currentMsList.add(newMs);
                    msAdapter = new MeasurementAdapter(UpdateRecipeActivity.this, currentMsList);
                    recyclerViewCurrentMeasurements.setAdapter(msAdapter);
                    Toast.makeText(UpdateRecipeActivity.this, "Item updated", Toast.LENGTH_SHORT)
                            .show();
                }

            }
        });
    }

    private void setHandlerForChoosingImages() {
        chooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void setHandlerForRemovingImage() {
        removeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUri = null;
                imageSize = null;
                selectedImageName = null;
                textViewImage.setText(R.string.image);
            }
        });
    }

    private void setHandlerForRecipeUpdate() {
        confirmUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validator.isFieldEmpty(editTextTitle)) {
                    Validator.displayErrorMessage(editTextTitle, getString(R.string.update_current_item_error_msg));
                    return;
                }
                if (!Validator.hasMinLengthTestPassed(editTextTitle, InsertRecipesActivity.MIN_LENGTH)) {
                    Validator.displayErrorMessage(editTextTitle, "Min length: " + InsertRecipesActivity.MIN_LENGTH);
                    return;
                }
                if (!Validator.isFieldEmpty(editTextPrep)) {
                    if (!Validator.hasMinLengthTestPassed(editTextPrep, InsertRecipesActivity.MIN_LENGTH)) {
                        return;
                    }
                }
                if (!Validator.isFieldEmpty(editTextCuisine)) {
                    if (!Validator.hasMinLengthTestPassed(editTextCuisine, InsertRecipesActivity.MIN_LENGTH)) {
                        return;
                    }
                }
                if (!Validator.isFieldEmpty(editTextCategory)) {
                    if (!Validator.hasMinLengthTestPassed(editTextCategory, InsertRecipesActivity.MIN_LENGTH)) {
                        return;
                    }
                }
                if (Validator.isFieldEmpty(editTextInstructions)) {
                    Validator.displayErrorMessage(editTextInstructions, getString(R.string.empty_field_error_msg));
                    return;
                }
                if (!Validator.hasMinLengthTestPassed(editTextInstructions, InsertRecipesActivity.MIN_LENGTH_INSTRUCTIONS)) {
                    return;
                }
                Validator.clearErrorField(requiredEditTextList);
                if (msAdapter == null) {
                    String msg = getString(R.string.ms_adapter_error_msg);
                    Toast.makeText(UpdateRecipeActivity.this, msg, Toast.LENGTH_LONG).show();
                    return;
                }
                final List<Measurement> msUpdateList = msAdapter.getMeasurementList();
                if (msUpdateList.size() < 1) {
                    String msg = getString(R.string.measurements_min_size_error_msg);
                    Toast.makeText(UpdateRecipeActivity.this, msg, Toast.LENGTH_LONG).show();
                    return;
                }
                if (imageUri != null && imageSize > ImageUtils.MAX_IMAGE_BYTES) {
                    String sizeMsg = getString(R.string.exceeded_img_size_error_msg);
                    Toast.makeText(UpdateRecipeActivity.this, sizeMsg, Toast.LENGTH_LONG).show();
                    return;
                }
                SharedPreferences sp = getSharedPreferences(UserSession.SHARED_PREFS, MODE_PRIVATE);
                String currentUser = UserSession.getUser(sp);
                if (currentUser == null) {
                    String msg = getString(R.string.update_recipe_logged_in_error_msg);
                    Toast.makeText(UpdateRecipeActivity.this, msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                String updatedTitle = editTextTitle.getText().toString();
                String updatedPrep = Validator.setToNullIfEmpty(editTextPrep.getText().toString());
                String updatedCuisine = Validator.setToNullIfEmpty(editTextCuisine.getText().toString());
                String updatedCategory = Validator.setToNullIfEmpty(editTextCategory.getText().toString());
                String updatedInstructions = editTextInstructions.getText().toString();
                final Recipe recipe = new Recipe(
                        updatedTitle, updatedPrep,
                        updatedCuisine, updatedCategory,
                        updatedInstructions, currentUser);
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(UpdateRecipeActivity.this);
                adBuilder.setTitle(R.string.update_recipe_dialog_title);
                adBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestForRecipeUpdate(recipe, msUpdateList);
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

    private void startRequestForRecipeUpdate(Recipe recipe, List<Measurement> measurementList) {

        MultipartBody.Part imgFile = null;
        if (imageUri != null) {
            File originalFile = FileUtils.getFile(UpdateRecipeActivity.this, imageUri);
            imgFile = ImageUtils.getImageAsMultipart(originalFile);
        }

        RequestBody recipeRbody = RequestBody.create(MultipartBody.FORM, gsonConverter.toJson(recipe));
        RequestBody msRbody = RequestBody.create(MultipartBody.FORM, gsonConverter.toJson(measurementList));

        Call<RegularStatus> call = recipesApiRef.updateRecipe(recipeId, recipeRbody, msRbody, imgFile);
        call.enqueue(new Callback<RegularStatus>() {
            @Override
            public void onResponse(Call<RegularStatus> call, Response<RegularStatus> response) {
                if (!response.isSuccessful()) {
                    Log.e(getLocalClassName(), "updateRecipe: status code " + response.code());
                    String msg = "Update failed, try again later";
                    Toast.makeText(UpdateRecipeActivity.this, msg, Toast.LENGTH_LONG).show();
                    return;
                }
                RegularStatus rs = response.body();
                if (rs.isValid()) {
                    Intent intent = new Intent(UpdateRecipeActivity.this, UserRecipesActivity.class);
                    String successMsg = "You successfully updated your recipe";
                    intent.putExtra(EXTRA_UPDATE_SUCCESS, successMsg);
                    startActivity(intent);
                } else {
                    String msg = rs.getErrorMessage();
                    Toast.makeText(UpdateRecipeActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegularStatus> call, Throwable t) {
                Log.e(getLocalClassName(), t.getMessage());
                String errMsg = "Update failed due to server issues, try again later";
                Toast.makeText(UpdateRecipeActivity.this, errMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getCurrentItem(TextView textView) {
        String item = textView.getText().toString().split(": ")[1];
        if(TextUtils.isEmpty(item)) {
            item = null;
            String msg = "Current item not found, item set to null";
            Toast.makeText(UpdateRecipeActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
        return item;
    }

    private void setHandlerForCurrentItemCheckbox() {
        for (final CheckBox cb : checkBoxArrayList) {
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (cb.getId()) {
                        case R.id.checkBox_keep_title:
                            editTextTitle.setText(getCurrentItem(textViewTitle));
                            break;
                        case R.id.checkBox_keep_prep:
                            editTextPrep.setText(getCurrentItem(textViewPrep));
                            break;
                        case R.id.checkBox_keep_cuisine:
                            editTextCuisine.setText(getCurrentItem(textViewCuisine));
                            break;
                        case R.id.checkBox_keep_category:
                            editTextCategory.setText(getCurrentItem(textViewCategory));
                            break;
                    }
                }
            });
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CHOOSE_IMAGE_REQUEST);
    }
}
