package com.techiq.wallpaperwonders.provider


import androidx.room.RoomDatabase
import com.techiq.wallpaperwonders.database.UserDatabase


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataBaseProvider {

    @Provides
    @Singleton
    fun providesUserDatabase(
        userDatabaseBuilder: RoomDatabase.Builder<UserDatabase>
    ): UserDatabase = userDatabaseBuilder.build()



}