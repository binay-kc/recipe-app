package com.binay.recipeapp.di

import android.content.Context
import androidx.room.Room
import com.binay.recipeapp.data.local.WebsiteDao
import com.binay.recipeapp.data.local.favoriteDb.AppDatabase
import com.binay.recipeapp.data.local.favoriteDb.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "recipe-palette"
        )
            .build()
    }

    @Provides
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao {
        return database.favoriteDao()
    }

    @Provides
    fun provideWebsiteDao(database: AppDatabase): WebsiteDao {
        return database.websiteDao()
    }
}