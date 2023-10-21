package com.binay.recipeapp.data.api

import retrofit2.http.GET

interface ApiService {

    // TODO: Remove this example and use as required
    @GET("recipes")
    suspend fun getData() : String
}