package com.arya.submission3.data.remote.retrofit

import com.arya.submission3.data.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int,
        @Header("Authorization") token: String
    ) : StoriesResponse

    @GET("stories")
    fun getStoriesForMap(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int,
        @Header("Authorization") token: String
    ) : Call<StoriesResponse>

    @GET("stories/{id}")
    suspend fun getStory(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ) : StoryResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") token: String
    ): AddStoryResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
        @Header("Authorization") token: String
    ): AddStoryResponse
}