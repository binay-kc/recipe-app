package com.binay.recipeapp.data.api


import com.binay.recipeapp.data.model.RecipeData
import com.binay.recipeapp.data.model.RecipeResponseData
import com.binay.recipeapp.data.model.SearchedRecipe
import com.binay.recipeapp.data.model.SearchedRecipeData

interface ApiHelper {
    suspend fun getData(tag: String): RecipeResponseData

    suspend fun searchRecipes(query: String): SearchedRecipeData

    suspend fun getRecipeDetail(id: Int): RecipeData

    suspend fun searchRecipesByIngredients(query: String): ArrayList<SearchedRecipe>
}