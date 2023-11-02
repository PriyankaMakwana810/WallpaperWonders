package com.techiq.wallpaperwonders.service

import com.techiq.wallpaperwonders.Model.request.LoginRequest
import com.techiq.wallpaperwonders.Model.request.RegisterationRequest

import com.techiq.wallpaperwonders.Model.response.Register.RegisterResponse
import com.techiq.wallpaperwonders.Model.response.SignIn.SigninResponse

import com.techiq.wallpaperwonders.utils.Constant.SERVICE_WITH_GSON_SIGNIN
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class ApiClient @Inject constructor(
    @Named(SERVICE_WITH_GSON_SIGNIN) private val serviceForSignIn: ApiInterface,

    ) {

    suspend fun signin(loginRequest: LoginRequest): Response<SigninResponse> {
        return serviceForSignIn.signin(loginRequest)
    }

    suspend fun register(registerationRequest: RegisterationRequest): Response<RegisterResponse> {
        return serviceForSignIn.register(registerationRequest = registerationRequest)
    }
}