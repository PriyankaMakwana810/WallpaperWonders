package com.techiq.wallpaperwonders.provider

import com.google.gson.Gson

import com.techiq.wallpaperwonders.service.ApiClient
import com.techiq.wallpaperwonders.repository.Signin.SigninRepository
import com.techiq.wallpaperwonders.utils.Constant.API_CLIENT
import com.techiq.wallpaperwonders.utils.Constant.GSON
import com.techiq.wallpaperwonders.utils.Constant.SHARED_COMMON
import com.techiq.wallpaperwonders.utils.PrefUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Named

@Module
@InstallIn(ActivityRetainedComponent::class)
object RepositoryProvider {

    @Provides
    @ActivityRetainedScoped

    fun signinRepository(

        @Named(GSON) gson: Gson,
        @Named(API_CLIENT) client: ApiClient,
        @Named(SHARED_COMMON) prefUtils: PrefUtils,

    ): SigninRepository = SigninRepository(
         client, prefUtils
    )


}