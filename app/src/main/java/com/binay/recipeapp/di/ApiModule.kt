package com.binay.recipeapp.di

import com.binay.recipeapp.data.api.ApiService
import com.binay.recipeapp.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    fun provideOkHttpClient() : OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val originalRequest : Request = chain.request()
                val newRequest = originalRequest.newBuilder()
                    .addHeader("x-api-key", Constants.API_KEY)
                    .build()
                chain.proceed(newRequest)
            })
            .addInterceptor((HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC // Set the logging level to BASIC
            })).build()
    }

    @Provides
    fun provideApiService(httpClient: OkHttpClient) : ApiService{
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
        return retrofit.create(ApiService::class.java)
    }

}