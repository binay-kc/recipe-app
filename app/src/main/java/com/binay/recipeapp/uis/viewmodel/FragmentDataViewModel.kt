package com.binay.recipeapp.uis.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binay.recipeapp.data.model.AnalyzedInstructions
import com.binay.recipeapp.data.model.ExtendedIngredients
import com.binay.recipeapp.data.model.RecipeData

class FragmentDataViewModel : ViewModel() {
    val ingredients = MutableLiveData<List<ExtendedIngredients>>()
    val instructions = MutableLiveData<List<AnalyzedInstructions>>()
    val isChecked = MutableLiveData<Boolean>()
    val groceryList = MutableLiveData<List<ExtendedIngredients>>()
    val isAddedToList = MutableLiveData<Boolean>()
    val randomRecipe = MutableLiveData<RecipeData>()
}