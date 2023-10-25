package com.binay.recipeapp.uis.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.recipeapp.data.repository.MainRepository
import com.binay.recipeapp.uis.intent.DataIntent
import com.binay.recipeapp.uis.viewstate.DataState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(private val mRepository : MainRepository) : ViewModel() {

    val dataIntent = Channel<DataIntent>(Channel.UNLIMITED)
    val dataState = MutableStateFlow<DataState>(DataState.Inactive)

    init{
        handleIntent()
    }

    private fun handleIntent(){
        viewModelScope.launch {
            dataIntent.consumeAsFlow().collect{
                when(it){
                    is DataIntent.FetchRecipeData -> fetchData(
                        it.tag
                    )
                }
            }
        }
    }

    private fun fetchData(tag: String){
        viewModelScope.launch {
            Log.d("viewmodel", "fetchData: ")
            dataState.value = DataState.Loading
            dataState.value = try{
                DataState.ResponseData(mRepository.getRecipes(tag))
            }catch (e: Exception){
                // TODO: Add proper way to parse error message and display to users
                DataState.Error(e.localizedMessage)
            }
        }
    }

}