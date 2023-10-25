package com.binay.recipeapp.data.api

import retrofit2.http.GET
import com.binay.recipeapp.data.model.RecipeResponseData
import retrofit2.http.Query

interface ApiService {
    @GET("recipes/random")
    suspend fun getData (
        @Query("tags") tags: String,
        @Query("number") number: Int = 15
    ): RecipeResponseData
}