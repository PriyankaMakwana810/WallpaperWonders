package com.techiq.wallpaperwonders.service

import com.techiq.wallpaperwonders.Model.request.LoginRequest
import com.techiq.wallpaperwonders.Model.request.RegisterationRequest
import com.techiq.wallpaperwonders.Model.response.Register.RegisterResponse
import com.techiq.wallpaperwonders.Model.response.SignIn.SigninResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {


    @POST("login")
    suspend fun signin(
        @Body loginRequest: LoginRequest
    ): Response<SigninResponse>


    @POST("registration")
    suspend fun register(
        @Body registerationRequest: RegisterationRequest
    ): Response<RegisterResponse>
}