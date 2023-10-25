package com.binay.recipeapp.data.api

import retrofit2.http.GET
import com.binay.recipeapp.data.model.RecipeResponseData
import retrofit2.http.Query

interface ApiService {

    // TODO: Remove this example and use as required

    @GET("recipes/complexSearch?instructionsRequired=true&number=50&limitLicense=true")
    suspend fun getData(): RecipeResponseData
}