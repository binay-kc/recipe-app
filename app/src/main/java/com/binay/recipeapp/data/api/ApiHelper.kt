package com.binay.recipeapp.data.api


import com.binay.recipeapp.data.model.RecipeData
import com.binay.recipeapp.data.model.RecipeResponseData

interface ApiHelper {
    suspend fun getData(tag: String): RecipeResponseData

    suspend fun getRecipeDetail(id: Int): RecipeData
}