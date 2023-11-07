package com.techiq.wallpaperwonders.service

import com.techiq.wallpaperwonders.model.request.LoginRequest
import com.techiq.wallpaperwonders.model.request.RegisterationRequest

import com.techiq.wallpaperwonders.model.response.Register.RegisterResponse
import com.techiq.wallpaperwonders.model.response.SignIn.SigninResponse
import com.techiq.wallpaperwonders.model.response.pixabay.PixabayImagesResponse
import com.techiq.wallpaperwonders.model.response.pixabay.PixabayResponse

import com.techiq.wallpaperwonders.utils.Constant.SERVICE_WITH_GSON_SIGNIN
import com.techiq.wallpaperwonders.utils.Constant.SERVICE_WITH_PIXABAY
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class ApiClient @Inject constructor(
    @Named(SERVICE_WITH_GSON_SIGNIN) private val apiInterface: ApiInterface,
    @Named(SERVICE_WITH_PIXABAY) private val pixabayInterface: ApiInterface,
    ) {
    suspend fun signin(loginRequest: LoginRequest): Response<SigninResponse> {
        return apiInterface.signin(loginRequest)
    }

    suspend fun register(registerationRequest: RegisterationRequest): Response<RegisterResponse> {
        return apiInterface.register(registerationRequest = registerationRequest)
    }

    suspend fun getImagesPixabay(key: String?,
                                 category: String?,
                                 pretty: Boolean?,
                                 page: Int?,
                                 orientation: String?,
                                 per_page: Int?,
                                 image_type: String?,
                                 q: String?,
                                 safesearch: Boolean?,
                                 order: String?): Response<PixabayImagesResponse>{
        return pixabayInterface.getImagesPixabay(
            key = key,
            category = category,
            pretty = pretty,
            page = page,
            orientation = orientation,
            per_page = per_page,
            image_type = image_type,
            q = q,
            safesearch = safesearch,
            order = order
        )
    }

}