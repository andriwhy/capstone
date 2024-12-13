package com.example.aifeel.data.retrofit

import com.example.aifeel.data.response.DiaryListResponse
import com.example.aifeel.data.response.DiaryRequest
import com.example.aifeel.data.response.DiaryResponse
import com.example.aifeel.data.response.LoginResponse
import com.example.aifeel.data.response.RegisterResponse
import com.example.aifeel.data.response.UserResponse
import com.example.aifeel.data.response.WeeklyRecapResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @GET("users/profile")
    suspend fun getUser(
        @Header("Authorization") token: String
    ): Response<UserResponse>

    @PUT("user/{userId}")
    fun updateUser(
        @Path("userId") userId: String,
        @Header("Authorization") authToken: String,
        @Body userUpdateBody: Map<String, String>
    ): Call<Void>

    @POST("diaries")
    fun createDiary(
        @Header("Authorization") token: String,
        @Body diaryRequest: DiaryRequest
    ): Call<DiaryResponse>

    @GET("diaries")
    fun getDiaries(
        @Query("userId") userId: String
    ): Call<DiaryListResponse>

    @GET("recap/{userId}")
    fun getWeeklyRecap(
        @Path("userId") userId: String,
        @Header("Authorization") token: String
    ): Call<WeeklyRecapResponse>
}

