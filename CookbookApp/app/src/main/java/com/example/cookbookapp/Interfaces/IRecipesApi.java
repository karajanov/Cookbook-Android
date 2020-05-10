package com.example.cookbookapp.Interfaces;

import com.example.cookbookapp.Models.*;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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

    @GET("Recipe/Author/{name}")
    Call<List<RecipePreview>> getRecipePreviewByAuthor(@Path("name") String name);

    @Multipart
    @POST("Recipe/Insert")
    Call<RegularStatus> insertRecipe(
            @Part("rawRecipe") RequestBody rawRecipe,
            @Part("rawMeasurements") RequestBody rawMeasurements,
            @Part MultipartBody.Part image);

    @DELETE("Recipe/Delete/{id}")
    Call<RegularStatus> deleteRecipe(@Path("id") int id);
}