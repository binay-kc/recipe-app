package com.binay.recipeapp.data.api

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    private const val BASE_URL = "https://api.spoonacular.com/"
    private const val API_KEY = "a92ba302332043249daf5d02b0620791"

    private fun getRetrofit() = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient.build())
        .build()

//    Adds API KEY to every request
   private val httpClient : OkHttpClient.Builder = OkHttpClient.Builder()
       .addInterceptor(Interceptor { chain ->
           val originalRequest : Request = chain.request()
           val originalHttpUrl : HttpUrl = originalRequest.url
           val newUrl : HttpUrl = originalHttpUrl.newBuilder()
               .addQueryParameter("apiKey", API_KEY)
               .build()
           val newRequestBuilder : Request.Builder = originalRequest.newBuilder().url(newUrl)
           val newRequest = newRequestBuilder.build()
           chain.proceed(newRequest)
       })

    val apiService : ApiService = getRetrofit().create(ApiService::class.java)
}