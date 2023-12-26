package com.binay.recipeapp.di

import android.content.Context
import com.binay.recipeapp.data.api.ApiHelper
import com.binay.recipeapp.data.local.favoriteDb.AppDatabase
import com.binay.recipeapp.data.local.favoriteDb.FavoriteDao
import com.binay.recipeapp.data.local.randomRecipeDb.RandomRecipeDao
import com.binay.recipeapp.data.local.recipesDb.RecipeDao
import com.binay.recipeapp.data.repository.MainRepository
import com.binay.recipeapp.data.repository.local.LocalRepo
import com.binay.recipeapp.data.repository.remote.RemoteRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object ViewModelMainModule {

    @Provides
    @ViewModelScoped
    fun provideLocalRepo(
        randomRecipeDao: RandomRecipeDao, recipeDao: RecipeDao
    ) = LocalRepo(randomRecipeDao, recipeDao)

    @Provides
    @ViewModelScoped
    fun provideRemoteRepo(
        apiHelper: ApiHelper,
        randomRecipeDao: RandomRecipeDao,
        recipeDao: RecipeDao,
        favoriteDao: FavoriteDao
    ) = RemoteRepo(apiHelper, randomRecipeDao, recipeDao, favoriteDao)

    @Provides
    @ViewModelScoped
    fun provideMainRepo(
        @ApplicationContext context: Context,
        mDatabase: AppDatabase, apiHelper: ApiHelper, localRepo: LocalRepo, remoteRepo: RemoteRepo
    ) = MainRepository(context, mDatabase, apiHelper, localRepo, remoteRepo)


}