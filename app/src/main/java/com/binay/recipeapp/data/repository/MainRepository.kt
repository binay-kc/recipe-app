package com.binay.recipeapp.data.repository

import com.binay.recipeapp.data.api.ApiHelper

class MainRepository(private val apiHelper: ApiHelper) {

    // TODO: Remove this and use as per required
    suspend fun getRecipes(tag: String) = apiHelper.getData(tag)

    suspend fun getRecipeDetail(id: Int) = apiHelper.getRecipeDetail(id)
}