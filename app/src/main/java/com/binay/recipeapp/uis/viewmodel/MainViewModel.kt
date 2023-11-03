package com.binay.recipeapp.uis.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.binay.recipeapp.data.local.favoriteDb.AppDatabase
import com.binay.recipeapp.data.model.RecipeData
import com.binay.recipeapp.data.repository.MainRepository
import com.binay.recipeapp.uis.intent.DataIntent
import com.binay.recipeapp.uis.viewstate.DataState
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

                    is DataIntent.ChangeFavoriteStatus -> changeFavoriteStatus(
                        it.recipe,
                        it.isToFavorite
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

    private fun changeFavoriteStatus(recipe: RecipeData, isToFavorite: Boolean) {
        viewModelScope.launch {
            dataState.value = DataState.Loading
            dataState.value = try {
                recipe.isFavorite = isToFavorite
//               Checks favorite Dao and updates data accordingly
                val favoriteDao = db.favoriteDao()
                if (isToFavorite) {
                    favoriteDao.addRecipe(recipe)
                } else {
                    favoriteDao.removeRecipe(recipe)
                }
                DataState.FavoriteResponse(recipe)
            } catch (e: Exception) {
                // TODO: Add proper way to parse error message and display to users
                DataState.Error(e.localizedMessage)
            }
        }
    }

}