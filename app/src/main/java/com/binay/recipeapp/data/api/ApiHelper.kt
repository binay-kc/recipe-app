package com.binay.recipeapp.data.api


import com.binay.recipeapp.data.model.RecipeResponseData

interface ApiHelper {

    suspend fun getData(): RecipeResponseData
}