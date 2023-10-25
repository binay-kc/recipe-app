package com.binay.recipeapp.data.api

import com.binay.recipeapp.data.model.RecipeResponseData
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // TODO: Remove this example and use as required
    @GET("recipes/random")
    suspend fun getData (
        @Query("tags") tags: String,
        @Query("number") number: Int = 15
    ): RecipeResponseData
}