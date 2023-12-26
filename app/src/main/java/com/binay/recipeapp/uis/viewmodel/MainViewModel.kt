package com.binay.recipeapp.uis.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.recipeapp.data.local.WebsiteDao
import com.binay.recipeapp.data.local.favoriteDb.AppDatabase
import com.binay.recipeapp.data.local.favoriteDb.FavoriteDao
import com.binay.recipeapp.data.local.ingredientDb.IngredientDao
import com.binay.recipeapp.data.model.ExtendedIngredients
import com.binay.recipeapp.data.model.RecipeData
import com.binay.recipeapp.data.model.SearchedRecipe
import com.binay.recipeapp.data.model.WebsiteData
import com.binay.recipeapp.data.repository.MainRepository
import com.binay.recipeapp.uis.intent.DataIntent
import com.binay.recipeapp.uis.viewstate.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mRepository: MainRepository,
    private val favoriteDao: FavoriteDao,
    private val websiteDao: WebsiteDao,
    private val ingredientDao: IngredientDao
) : ViewModel() {

    val dataIntent = Channel<DataIntent>(Channel.UNLIMITED)
    val dataState = MutableStateFlow<DataState>(DataState.Inactive)

    init {
        handleIntent()
        insertWebsiteData()
    }

    private fun insertWebsiteData() {
        viewModelScope.launch {
            if (websiteDao.getAll().isNotEmpty()) {
                val websiteList: MutableList<WebsiteData> = ArrayList()
                websiteList.add(WebsiteData("Yummy", "https://www.yummly.com/recipes"))
                websiteList.add(WebsiteData("MyRecipes", "https://www.myrecipes.com/"))
                websiteList.add(WebsiteData("Allrecipes", "https://www.allrecipes.com/"))
                websiteList.add(WebsiteData("Food Network", "https://www.foodnetwork.com/recipes"))
                websiteList.add(WebsiteData("BBC Good Food", "https://www.bbcgoodfood.com/"))
                websiteList.add(WebsiteData("Tasty", "https://tasty.co/"))
                websiteList.add(WebsiteData("Epicurious", "https://www.epicurious.com/"))
                websiteList.add(WebsiteData("Serious Eats", "https://www.seriouseats.com/recipes"))
                websiteList.add(WebsiteData("Bon Appétit", "https://www.bonappetit.com/recipes"))
                websiteList.add(WebsiteData("Simply Recipes", "https://www.simplyrecipes.com/"))
                websiteList.add(WebsiteData("EatingWell", "https://www.eatingwell.com/recipes"))
                websiteList.add(
                    WebsiteData(
                        "The Spruce Eats",
                        "https://www.thespruceeats.com/recipes-4160606/"
                    )
                )
                websiteList.add(WebsiteData("Skinnytaste", "https://www.skinnytaste.com/"))
                websiteList.add(WebsiteData("Cookstr", "https://www.cookstr.com/"))
                websiteList.add(
                    WebsiteData(
                        "Taste of Home",
                        "https://www.tasteofhome.com/recipes/"
                    )
                )
                websiteList.add(WebsiteData("Delish", "https://www.delish.com/"))
                websiteList.add(WebsiteData("Food52", "https://food52.com/recipes"))
                websiteList.add(
                    WebsiteData(
                        "Cooking Channel",
                        "https://www.cookingchanneltv.com/recipes"
                    )
                )
                websiteList.add(WebsiteData("Delish", "https://www.delish.com/"))

                websiteDao.insert(websiteList)
            }
        }
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

                    is DataIntent.RemoveFromShoppingList -> removeFromShoppingList(
                        it.ingredients
                    )

                    is DataIntent.FetchWebsiteList -> fetchWebsiteData()

                    is DataIntent.FetchRandomRecipe -> fetchRandomRecipe()

                    else -> {}
                }
            }
        }
    }

    private fun fetchData(tag: String) {
        viewModelScope.launch {
            dataState.value = DataState.Loading
            dataState.value = try {
                val recipes = mRepository.getRecipes(tag)

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
                val favoriteRecipes = favoriteDao.getAllRecipes()
                favoriteRecipes.size
                if (isToFavorite) {
                    favoriteDao.addRecipe(recipe)
                } else {
                    favoriteDao.removeRecipeFromFavorite(recipe)
                }
                DataState.AddToFavoriteResponse(recipe)
            } catch (e: Exception) {
                // TODO: Add proper way to parse error message and display to users
                Log.e("Error ", "" + e.localizedMessage)
                DataState.Error(e.localizedMessage)
            }
        }
    }

    private suspend fun fetchFavoriteRecipes() {
        dataState.value = try {
            val favoriteRecipe = favoriteDao.getAllRecipes()
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
                        val favoriteRecipe = favoriteDao.getRecipe(recipeId)
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
                    if (isToFavorite) {
                        favoriteDao.addRecipe(recipeDetail)
                    } else {
                        favoriteDao.removeRecipeFromFavorite(recipeDetail)
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
                        val favoriteRecipe = favoriteDao.getRecipe(recipeId)
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
        viewModelScope.launch {
            dataState.value = try {
                val ingredientData = ingredientDao.getAllIngredients()
                DataState.IngredientResponse(ingredientData.toCollection(ArrayList()))
            } catch (e: Exception) {
                DataState.Error(e.localizedMessage)
            }
        }
    }

    private fun addToShoppingList(ingredients: List<ExtendedIngredients>) {
        viewModelScope.launch {
            dataState.value = DataState.Loading
            dataState.value = try {
                ingredientDao.addAllIngredients(ingredients)
                DataState.AddToShoppingList(ingredients)
            } catch (e: Exception) {
                // TODO: Add proper way to parse error message and display to users
                Log.e("Error ", "" + e.localizedMessage)
                DataState.Error(e.localizedMessage)
            }
        }
    }

    private fun removeFromShoppingList(ingredients: ExtendedIngredients) {
        viewModelScope.launch {
            dataState.value = DataState.Loading
            dataState.value = try {
                ingredientDao.removeIngredient(ingredients)
                val updatedList = ingredientDao.getAllIngredients()
                DataState.IngredientResponse(updatedList.toCollection(ArrayList()))
            } catch (e: Exception) {
                Log.e("Error ", "" + e.localizedMessage)
                DataState.Error(e.localizedMessage)
            }
        }
    }

    private fun fetchWebsiteData() {
        viewModelScope.launch {
            dataState.value = try {
                val websiteData = websiteDao.getAll()
                DataState.FetchWebsiteList(websiteData)
            } catch (e: Exception) {
                DataState.Error(e.localizedMessage)
            }
        }
    }

    private fun fetchRandomRecipe() {
        viewModelScope.launch {
            dataState.value = DataState.Loading
            dataState.value = try {
                val recipes = mRepository.getRandomRecipe()
                DataState.ResponseData(recipes)
            } catch (e: Exception) {
                // TODO: Add proper way to parse error message and display to users
                DataState.Error(e.localizedMessage)
            }
        }
    }
}