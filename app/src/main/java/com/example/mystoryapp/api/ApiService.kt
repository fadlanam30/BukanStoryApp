package com.example.mystoryapp.api

import com.example.mystoryapp.StoryResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization")
        authHeader: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): StoryResponse

    @GET("stories?location=1")
    suspend fun getStoriesLoct(
        @Header("Authorization")
        authHeader: String
    ): StoryResponse

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): StoryResponse

    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<StoryResponse>

    @FormUrlEncoded
    @POST("register")
    fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<StoryResponse>

    @Multipart
    @POST("stories")
    fun addStory(
        @Header("Authorization")
        authHeader: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: String,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?,
    ): Call<StoryResponse>

}