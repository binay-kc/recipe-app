package com.binay.recipeapp.uis.viewstate


import com.binay.recipeapp.data.model.ExtendedIngredients
import com.binay.recipeapp.data.model.RecipeData
import com.binay.recipeapp.data.model.RecipeResponseData
import com.binay.recipeapp.data.model.SearchedRecipe
import com.binay.recipeapp.data.model.SearchedRecipeData

sealed class DataState {
    object Inactive : DataState()
    object Loading : DataState()
    data class ResponseData(val recipeResponseData: RecipeResponseData) : DataState()

    data class RecipeDetail(val recipeData: RecipeData) : DataState()

    data class Error(val error: String?) : DataState()

    data class AddToFavoriteResponse(val recipe: RecipeData) : DataState()

    data class FavoriteResponse(val recipes: ArrayList<RecipeData>?) : DataState()

    data class SearchRecipes(val searchRecipeData: SearchedRecipeData) : DataState()

    data class SearchRecipesByNutrients(val searchedRecipes: ArrayList<SearchedRecipe>) : DataState()

    data class IngredientResponse(val ingredients: ArrayList<ExtendedIngredients>): DataState()

    data class AddToShoppingList(val ingredients: List<ExtendedIngredients>): DataState()

    data class RemoveFromShoppingList(val ingredients: ExtendedIngredients): DataState()
}
