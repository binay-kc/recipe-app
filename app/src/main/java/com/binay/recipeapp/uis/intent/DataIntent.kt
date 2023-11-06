package com.binay.recipeapp.uis.intent

sealed class DataIntent{
    // TODO: Remove this and use as per required
    data class FetchRecipeData(
        val tag: String
    ) : DataIntent()

    data class FetchRecipeDetail (
        val recipeId: Int
    ): DataIntent()
}
