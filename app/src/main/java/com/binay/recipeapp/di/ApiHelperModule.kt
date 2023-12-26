package com.binay.recipeapp.di

import com.binay.recipeapp.data.api.ApiHelper
import com.binay.recipeapp.data.api.ApiHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface ApiHelperModule {
    @Binds
    fun bindApiHelper(impl: ApiHelperImpl): ApiHelper
}