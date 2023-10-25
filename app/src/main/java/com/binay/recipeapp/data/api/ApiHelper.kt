package com.binay.recipeapp.data.api

import com.binay.recipeapp.data.model.RecipeResponseData

interface ApiHelper {

    // TODO: Change this as required for api consumption
    suspend fun getData(tag: String): RecipeResponseData
}