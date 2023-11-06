package com.techiq.wallpaperwonders.service

import com.techiq.wallpaperwonders.model.request.LoginRequest
import com.techiq.wallpaperwonders.model.request.RegisterationRequest

import com.techiq.wallpaperwonders.model.response.Register.RegisterResponse
import com.techiq.wallpaperwonders.model.response.SignIn.SigninResponse
import com.techiq.wallpaperwonders.model.response.collection.CollectionResponse

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

    suspend fun collectionList(
        collectionId: String?,
        authKey: String? = "AUTHORIZATION_KEY",
        pretty: Boolean?,
        page: Int?,
        per_page: Int?,
    ): Response<CollectionResponse> {
        return serviceForSignIn.getCollectionByIdPexels(
            collectionId,
            authKey,
            pretty,
            page,
            per_page
        )
    }
}