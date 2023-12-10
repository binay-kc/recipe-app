package com.binay.recipeapp.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.binay.recipeapp.data.api.ApiHelper
import com.binay.recipeapp.data.local.favoriteDb.AppDatabase
import com.binay.recipeapp.data.repository.MainRepository
import com.binay.recipeapp.data.repository.UnitConverterRepository
import com.binay.recipeapp.uis.viewmodel.MainViewModel
import com.binay.recipeapp.uis.viewmodel.UnitConverterViewModel

class ViewModelFactory(private val apiHelper: ApiHelper, private val mContext: Context, private val mDatabase : AppDatabase) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(MainRepository(apiHelper, mContext,mDatabase), mContext) as T
        } else if (modelClass.isAssignableFrom(UnitConverterViewModel::class.java)) {
            return UnitConverterViewModel(UnitConverterRepository(mContext)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}