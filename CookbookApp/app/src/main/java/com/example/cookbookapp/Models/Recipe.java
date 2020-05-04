package com.example.cookbookapp.Models;

import okhttp3.MultipartBody;

public class Recipe {

    private String title;

    private String prepTime;

    private String cuisineTitle;

    private String categoryTitle;

    private String instructions;

    private String username;

    public Recipe(
            String title,
            String prepTime,
            String cuisineTitle,
            String categoryTitle,
            String instructions,
            String username
    ) {
        this.title = title;
        this.prepTime = prepTime;
        this.cuisineTitle = cuisineTitle;
        this.categoryTitle = categoryTitle;
        this.instructions = instructions;
        this.username = username;
    }
}
