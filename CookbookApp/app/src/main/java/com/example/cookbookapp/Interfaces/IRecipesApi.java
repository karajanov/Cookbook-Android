package com.example.cookbookapp.Interfaces;

import com.example.cookbookapp.Models.*;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IRecipesApi {

    @GET("Category/All")
    Call<List<String>> getCategoryTitles();

    @GET("Cuisine/All")
    Call<List<String>> getCuisineTitles();

    @GET("Recipe/Category")
    Call<List<RecipePreview>> getRecipePreviewByCategory(@Query("item") String item);

    @GET("Recipe/Cuisine")
    Call<List<RecipePreview>> getRecipePreviewByCuisine(@Query("item") String item);

    @GET("Recipe/Details/{id}")
    Call<RecipeDetails> getRecipeDetailsById(@Path("id") int recipeId);

    @GET("Measurement/Recipe/{id}")
    Call<List<RecipeMeasurement>> getRecipeMeasurementsById(@Path("id") int recipeId);

    @GET("Recipe/Exact")
    Call<List<RecipePreview>> getRecipePreviewByExactTitle(@Query("title") String title);

    @GET("Recipe/Contains")
    Call<List<RecipePreview>> getRecipePreviewThatContainsKey(@Query("key") String key);

    @GET("Recipe/Starts")
    Call<List<RecipePreview>> getRecipePreviewThatStartsWithKey(@Query("key") String key);
}