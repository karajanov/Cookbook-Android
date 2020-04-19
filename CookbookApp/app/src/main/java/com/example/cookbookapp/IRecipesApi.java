package com.example.cookbookapp;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
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
}
