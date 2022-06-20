package com.example.storyapp.nonui.retrofit

import com.example.storyapp.nonui.response.AllStoriesResponse
import com.example.storyapp.nonui.response.ListStoryItem
import com.example.storyapp.nonui.response.LoginResponse
import com.example.storyapp.nonui.response.OverallResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") pass: String
    ): Call<OverallResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") pass: String
    ): Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun addStories(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") token: String,
        @Header("Accept") type : String,
    ): Call<OverallResponse>

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): AllStoriesResponse

    @GET("stories")
    fun AllStoriesResponse(
        @Header("Authorization") token: String,
    ): Call<AllStoriesResponse>




}