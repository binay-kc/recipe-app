package com.binay.recipeapp.uis.intent

import com.binay.recipeapp.data.model.RecipeData

sealed class DataIntent {
    data class FetchRecipeData(
        val tag: String
    ) : DataIntent()

    data class ChangeFavoriteStatus(
        val recipe: RecipeData,
        val isToFavorite: Boolean
    ) : DataIntent()
}
