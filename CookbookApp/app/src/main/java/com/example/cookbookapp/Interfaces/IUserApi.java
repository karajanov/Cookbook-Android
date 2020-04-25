package com.example.cookbookapp.Interfaces;

import com.example.cookbookapp.Models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IUserApi {

    @GET("User/Availability/Name/{username}")
    Call<Boolean> isUsernameTaken(@Path("username") String username);

    @GET("User/Availability/Email/{email}")
    Call<Boolean> isEmailTaken(@Path("email") String email);

    @POST("User")
    Call<Integer> registerUser(@Body User userViewModel);
}
