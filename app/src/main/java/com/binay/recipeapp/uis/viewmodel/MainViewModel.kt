package com.binay.recipeapp.uis.viewmodel

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
                    is DataIntent.FetchData -> fetchData()
                }
            }
        }
    }

    private fun fetchData(){
        viewModelScope.launch {
            dataState.value = DataState.Loading
            dataState.value = try{
                DataState.ResponseData(mRepository.getRecipes())
            }catch (e: Exception){
                // TODO: Add proper way to parse error message and display to users
                DataState.Error(e.localizedMessage)
            }
        }
    }

}