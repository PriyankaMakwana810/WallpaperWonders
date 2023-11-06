package com.techiq.wallpaperwonders.service

import com.techiq.wallpaperwonders.model.request.LoginRequest
import com.techiq.wallpaperwonders.model.request.RegisterationRequest
import com.techiq.wallpaperwonders.model.response.Register.RegisterResponse
import com.techiq.wallpaperwonders.model.response.SignIn.SigninResponse
import com.techiq.wallpaperwonders.model.response.collection.CollectionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {


    @POST("login")
    suspend fun signin(
        @Body loginRequest: LoginRequest,
    ): Response<SigninResponse>


    @POST("registration")
    suspend fun register(
        @Body registerationRequest: RegisterationRequest,
    ): Response<RegisterResponse>

    @GET("/v1/collections/{id}")
    suspend fun getCollectionByIdPexels(
        @Path("id") collectionId:String?,
        @Header("Authorization") authKey:String?,
        @Query("pretty") pretty: Boolean?,
        @Query("page") page: Int?,
        @Query("per_page") per_page: Int?
    ): Response<CollectionResponse>
}