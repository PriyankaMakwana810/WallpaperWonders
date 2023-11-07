package com.techiq.wallpaperwonders.service

import com.techiq.wallpaperwonders.model.request.LoginRequest
import com.techiq.wallpaperwonders.model.request.RegisterationRequest
import com.techiq.wallpaperwonders.model.response.Register.RegisterResponse
import com.techiq.wallpaperwonders.model.response.SignIn.SigninResponse
import com.techiq.wallpaperwonders.model.response.pexels.collection.CollectionByIdResponse
import com.techiq.wallpaperwonders.model.response.pexels.collection.CollectionResponse
import com.techiq.wallpaperwonders.model.response.pexels.images.PexelsImagesResponse
import com.techiq.wallpaperwonders.model.response.pexels.videos.PexelsVideosResponse
import com.techiq.wallpaperwonders.model.response.pixabay.PixabayImagesResponse
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

    @GET("api/")
    suspend fun getImagesPixabay(
        @Query("key") key: String?,
        @Query("category") category: String?,
        @Query("pretty") pretty: Boolean?,
        @Query("page") page: Int?,
        @Query("orientation") orientation: String?,
        @Query("per_page") per_page: Int?,
        @Query("image_type") image_type: String?,
        @Query("q") q: String?,
        @Query("safesearch") safesearch: Boolean?,
        @Query("order") order: String?,
    ): Response<PixabayImagesResponse>

    @GET("/v1/search")
    fun getImagesPexels(
        @Header("Authorization") authKey: String?,
        @Query("query") query: String?,
        @Query("image_type") image_type: String?,
        @Query("pretty") pretty: Boolean?,
        @Query("page") page: Int?,
        @Query("per_page") per_page: Int?,
    ): Response<PexelsImagesResponse>

    @GET("/videos/search")
    fun getVideosPexels(
        @Header("Authorization") authKey: String?,
        @Query("query") query: String?,
        @Query("orientation") orientation: String?,
        @Query("size") size: String?,
        @Query("pretty") pretty: Boolean?,
        @Query("page") page: Int?,
        @Query("per_page") per_page: Int?,
    ): Response<PexelsVideosResponse>

    @GET("/v1/collections/featured")
    fun getCollectionPexels(
        @Header("Authorization") authKey: String?,
        @Query("pretty") pretty: Boolean?,
        @Query("page") page: Int?,
        @Query("per_page") per_page: Int?,
    ): Response<CollectionResponse>

    @GET("/v1/collections/{id}")
    suspend fun getCollectionByIdPexels(
        @Path("id") collectionId: String?,
        @Header("Authorization") authKey: String?,
        @Query("pretty") pretty: Boolean?,
        @Query("page") page: Int?,
        @Query("per_page") per_page: Int?,
    ): Response<CollectionByIdResponse>
}