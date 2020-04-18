package com.example.cookbookapp;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface IRecipesApi {

    @GET("Category/All")
    Call<List<String>> getCategoryTitles();

    @GET("Cuisine/All")
    Call<List<String>> getCuisineTitles();
}
