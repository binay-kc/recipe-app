package com.binay.recipeapp.di

import android.content.Context
import com.binay.recipeapp.data.api.ApiHelper
import com.binay.recipeapp.data.local.favoriteDb.AppDatabase
import com.binay.recipeapp.data.repository.MainRepository
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
    fun provideMainRepo(
        @ApplicationContext context: Context,
        mDatabase: AppDatabase, apiHelper: ApiHelper
    ) = MainRepository(context, mDatabase, apiHelper)


}