package com.binay.recipeapp.data.api

import com.binay.recipeapp.data.model.RecipeData
import retrofit2.http.GET
import com.binay.recipeapp.data.model.RecipeResponseData
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("recipes/random")
    suspend fun getData (
        @Query("tags") tags: String,
        @Query("number") number: Int = 30
    ): RecipeResponseData

    @GET("recipes/{id}/information")
    suspend fun getRecipeDetail(
        @Path("id") id: Int,
        @Query("includeNutrition") number: Boolean = true
    ): RecipeData
}