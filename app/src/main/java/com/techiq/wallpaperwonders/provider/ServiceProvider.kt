package com.techiq.wallpaperwonders.provider

import com.google.gson.Gson

import com.techiq.wallpaperwonders.service.ApiClient
import com.techiq.wallpaperwonders.service.ApiInterface
import com.techiq.wallpaperwonders.service.NetworkConstants.ApiUrl.HOME_URL
import com.techiq.wallpaperwonders.utils.Constant.API_CLIENT
import com.techiq.wallpaperwonders.utils.Constant.BASE_URL
import com.techiq.wallpaperwonders.utils.Constant.GSON
import com.techiq.wallpaperwonders.utils.Constant.OK_HTTPS
import com.techiq.wallpaperwonders.utils.Constant.SERVICE_FOR_LOGIN_GSON
import com.techiq.wallpaperwonders.utils.Constant.SERVICE_WITH_GSON
import com.techiq.wallpaperwonders.utils.Constant.SERVICE_WITH_GSON_SIGNIN
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

@Module
@InstallIn(ActivityRetainedComponent::class)
object ServiceProvider {

    @ActivityRetainedScoped
    @Provides
    @Named(BASE_URL)
    fun provideBaseUrl(): String =
     HOME_URL

    @ActivityRetainedScoped
    @Provides
    @Named(API_CLIENT)
    fun providesApiClient(
        @Named(SERVICE_WITH_GSON) service: ApiInterface,
        @Named(SERVICE_FOR_LOGIN_GSON) serviceForLogin: ApiInterface,
        @Named(SERVICE_WITH_GSON_SIGNIN) serviceForSignin: ApiInterface,

    ): ApiClient = ApiClient(
        serviceForSignIn = serviceForSignin,



    )

    @Provides
    @Named(SERVICE_WITH_GSON)
    @ActivityRetainedScoped
    fun providesApiService(
        @Named(OK_HTTPS) okHttpClient: OkHttpClient,
        @Named(BASE_URL) baseUrl: String,
        @Named(GSON) gson: Gson
    ): ApiInterface {

        return Retrofit.Builder().baseUrl(baseUrl).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
            .create(ApiInterface::class.java)
    }

}