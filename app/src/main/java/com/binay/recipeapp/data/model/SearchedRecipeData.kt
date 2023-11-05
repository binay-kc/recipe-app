package com.binay.recipeapp.data.model

data class SearchedRecipeData(
    var offset: Int? = null,
    var number: Int? = null,
    var results: ArrayList<SearchedRecipe>? = arrayListOf(),
    var totalResults: Int? = null,
    var recipeDataToStore : RecipeData
)
