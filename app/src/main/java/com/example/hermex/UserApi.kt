package com.example.hermex

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.Response

data class UserResponse(val username: String, val email: String, val profileImageUrl: String)
data class UpdateProfileRequest(val username: String, val email: String, val immagine: String?)
data class MessageResponse(val message: String)

interface UserApi {
    @GET("api/user/profile")
    suspend fun getUserData(@Header("Authorization") token: String): UserResponse

    @PUT("api/user/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body body: UpdateProfileRequest
    ): MessageResponse
}

fun provideUserApi(): UserApi {
    return Retrofit.Builder()
        .baseUrl("http://10.0.2.2:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(UserApi::class.java)
}
