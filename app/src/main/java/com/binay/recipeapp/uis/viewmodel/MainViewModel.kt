package com.binay.recipeapp.uis.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.binay.recipeapp.data.local.favoriteDb.AppDatabase
import com.binay.recipeapp.data.model.ExtendedIngredients
import com.binay.recipeapp.data.model.RecipeData
import com.binay.recipeapp.data.model.SearchedRecipe
import com.binay.recipeapp.data.repository.MainRepository
import com.binay.recipeapp.uis.intent.DataIntent
import com.binay.recipeapp.uis.viewstate.DataState
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(private val mRepository: MainRepository, mContext: Context) : ViewModel() {

    val dataIntent = Channel<DataIntent>(Channel.UNLIMITED)
    val dataState = MutableStateFlow<DataState>(DataState.Inactive)


    val db = Room.databaseBuilder(
        mContext.applicationContext,
        AppDatabase::class.java, "recipe-palette"
    ).allowMainThreadQueries().build()

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            dataIntent.consumeAsFlow().collect {
                when (it) {
                    is DataIntent.FetchRecipeData -> fetchData(
                        it.tag
                    )

                    is DataIntent.FetchRecipeDetail -> fetchRecipeDetailData(
                        it.recipeId
                    )

                    is DataIntent.ChangeFavoriteStatus -> changeFavoriteStatus(
                        it.recipe,
                        it.isToFavorite
                    )

                    is DataIntent.FetchFavoriteRecipe -> fetchFavoriteRecipes()

                    is DataIntent.SearchRecipe -> searchRecipes(it.query)

                    is DataIntent.ChangeFavoriteStatusFromSearch -> changeFavoriteStatus(
                        it.recipe,
                        it.isToFavorite
                    )

                    is DataIntent.SearchRecipesByNutrients -> searchRecipesByNutrients(it.query)

                    is DataIntent.FetchShoppingListData -> fetchShoppingListData()

                    is DataIntent.AddToShoppingList -> addToShoppingList(
                        it.ingredients
                    )

                }
            }
        }
    }

    private fun fetchData(tag: String) {
        viewModelScope.launch {
            dataState.value = DataState.Loading
            dataState.value = try {
                val recipes = mRepository.getRecipes(tag)
//               Checks favorite Dao and updates data accordingly
//                Note: If room has recipe, then it is automatically favorite
                recipes.recipes.forEach {
                    val favoriteRecipe = db.favoriteDao().getRecipe(it.id)
                    if (favoriteRecipe != null) {
                        it.isFavorite = true
                    }
                }
                DataState.ResponseData(recipes)
            } catch (e: Exception) {
                // TODO: Add proper way to parse error message and display to users
                DataState.Error(e.localizedMessage)
            }
        }
    }

    private fun fetchRecipeDetailData(recipeID: Int) {
        viewModelScope.launch {
            Log.e("viewmodel", "fetchRecipeDetailData: ")
            dataState.value = DataState.Loading
            dataState.value = try {
                val recipeDetail = mRepository.getRecipeDetail(recipeID)
                DataState.RecipeDetail(recipeDetail)
            } catch (e: Exception) {
                DataState.Error(e.message)
            }
        }
    }

    private fun changeFavoriteStatus(recipe: RecipeData, isToFavorite: Boolean) {
        viewModelScope.launch {
            dataState.value = DataState.Loading
            dataState.value = try {
                recipe.isFavorite = isToFavorite
//               Checks favorite Dao and updates data accordingly
                val favoriteDao = db.favoriteDao()
                val favoriteRecipes = db.favoriteDao().getAllRecipes()
                favoriteRecipes.size
                if (isToFavorite) {
                    favoriteDao.addRecipe(recipe)
                } else {
                    favoriteDao.removeRecipe(recipe)
                }
                DataState.AddToFavoriteResponse(recipe)
            } catch (e: Exception) {
                // TODO: Add proper way to parse error message and display to users
                Log.e("Error ", "" + e.localizedMessage)
                DataState.Error(e.localizedMessage)
            }
        }
    }

    private fun fetchFavoriteRecipes() {
        dataState.value = try {
            val favoriteRecipe = db.favoriteDao().getAllRecipes()
            DataState.FavoriteResponse(favoriteRecipe.toCollection(ArrayList()))
        } catch (e: Exception) {
            DataState.Error(e.localizedMessage)
        }
    }

    private fun searchRecipes(query: String) {
        viewModelScope.launch {
            dataState.value = DataState.Loading
            dataState.value = try {
                val searchedRecipeData = mRepository.searchRecipes(query)
                Log.e("sdsdd ", "" + searchedRecipeData)
//               Checks favorite Dao and updates data accordingly
//                Note: If room has recipe, then it is automatically favorite
                val searchedRecipes = searchedRecipeData.results ?: return@launch
                searchedRecipes.forEach {
                    val recipeId = it.id
                    if (recipeId != null) {
                        val favoriteRecipe = db.favoriteDao().getRecipe(recipeId)
                        if (favoriteRecipe != null) {
                            it.isFavorite = true
                        }
                    }
                }
                DataState.SearchRecipes(searchedRecipeData)
            } catch (e: Exception) {
                // TODO: Add proper way to parse error message and display to users
                DataState.Error(e.localizedMessage)
            }
        }
    }

    private fun changeFavoriteStatus(recipe: SearchedRecipe, isToFavorite: Boolean) {
        val recipeId = recipe.id ?: return
        viewModelScope.launch {
            dataState.value = DataState.Loading
            try {

                val deferredRecipeDetail = async {
                    Log.e("Recipe Detail ", " Here")
                    return@async mRepository.getRecipeDetail(recipeId)
                }

                val recipeDetail = deferredRecipeDetail.await()
                dataState.value = try {
                    Log.e("Favorite ", " Here")
                    recipeDetail.isFavorite = isToFavorite
//               Checks favorite Dao and updates data accordingly
                    val favoriteDao = db.favoriteDao()
                    if (isToFavorite) {
                        favoriteDao.addRecipe(recipeDetail)
                    } else {
                        favoriteDao.removeRecipe(recipeDetail)
                    }
                    DataState.AddToFavoriteResponse(RecipeData())
                } catch (e: Exception) {
                    // TODO: Add proper way to parse error message and display to users
                    Log.e("Error ", "" + e.localizedMessage)
                    DataState.Error(e.localizedMessage)
                }
            } catch (e: Exception) {
                Log.e("Error ", "" + e.localizedMessage)
            }

        }
    }

    private fun searchRecipesByNutrients(query: String) {
        viewModelScope.launch {
            dataState.value = DataState.Loading
            dataState.value = try {
                val searchedRecipes: ArrayList<SearchedRecipe> =
                    mRepository.searchRecipesByIngredients(query)
                Log.e("Recipes by nutrients ", "" + searchedRecipes)
//               Checks favorite Dao and updates data accordingly
//                Note: If room has recipe, then it is automatically favorite
                searchedRecipes.forEach {
                    val recipeId = it.id
                    if (recipeId != null) {
                        val favoriteRecipe = db.favoriteDao().getRecipe(recipeId)
                        if (favoriteRecipe != null) {
                            it.isFavorite = true
                        }
                    }
                }
                DataState.SearchRecipesByNutrients(searchedRecipes)
            } catch (e: Exception) {
                // TODO: Add proper way to parse error message and display to users
                DataState.Error(e.localizedMessage)
            }
        }
    }

    private fun fetchShoppingListData() {
        dataState.value = try {
            val ingredientData = db.ingredientDao().getAllIngredients()
            DataState.IngredientResponse(ingredientData.toCollection(ArrayList()))
        } catch (e: Exception) {
            DataState.Error(e.localizedMessage)
        }
    }

    private fun addToShoppingList(ingredients: List<ExtendedIngredients>) {
        viewModelScope.launch {
            dataState.value = DataState.Loading
            dataState.value = try {
                val ingredientDao = db.ingredientDao()
                ingredientDao.addAllIngredients(ingredients)
                DataState.AddToShoppingList(ingredients)
            } catch (e: Exception) {
                // TODO: Add proper way to parse error message and display to users
                Log.e("Error ", "" + e.localizedMessage)
                DataState.Error(e.localizedMessage)
            }
        }
    }
}