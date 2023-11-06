package com.binay.recipeapp.uis.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.binay.recipeapp.data.api.ApiHelperImpl
import com.binay.recipeapp.data.api.RetrofitBuilder
import com.binay.recipeapp.uis.intent.DataIntent
import com.binay.recipeapp.uis.viewmodel.MainViewModel
import com.binay.recipeapp.uis.viewstate.DataState
import com.binay.recipeapp.util.ViewModelFactory
import kotlinx.coroutines.launch

class RecipeDetailActivity: AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService), this)
        )[MainViewModel::class.java]

        lifecycleScope.launch {
            viewModel.dataState.collect {
                when (it) {
                    is DataState.Loading -> {

                    }

                    is DataState.RecipeDetail -> {
                        Log.d("haancha", "initViewModel: "+it.recipeData)

                    }

                    else -> {

                    }
                }
            }
        }
    }

    private fun fetchDetailData(id: Int) {
        lifecycleScope.launch {
            viewModel.dataIntent.send(
                DataIntent.FetchRecipeDetail(
                    id
                )
            )
        }
    }
}