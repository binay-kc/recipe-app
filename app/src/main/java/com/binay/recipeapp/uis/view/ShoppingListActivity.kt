package com.binay.recipeapp.uis.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.binay.recipeapp.R
import com.binay.recipeapp.data.api.ApiHelperImpl
import com.binay.recipeapp.data.api.RetrofitBuilder
import com.binay.recipeapp.data.model.ExtendedIngredients
import com.binay.recipeapp.databinding.ActivityShoppingListBinding
import com.binay.recipeapp.uis.intent.DataIntent
import com.binay.recipeapp.uis.viewmodel.MainViewModel
import com.binay.recipeapp.uis.viewstate.DataState
import com.binay.recipeapp.util.ViewModelFactory
import kotlinx.coroutines.launch


class ShoppingListActivity: AppCompatActivity(),
    ShoppingListRecyclerAdapter.GroceryItemClickListener {

    private lateinit var mBinding: ActivityShoppingListBinding
    private lateinit var mViewModel: MainViewModel
    private lateinit var mAdapter: ShoppingListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityShoppingListBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initView()
        initViewModel()
    }

    private fun initView() {

        mBinding.toolbar.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        mBinding.toolbar.toolbarTitle.text = getString(R.string.list_name)

        mBinding.recyclerView.setHasFixedSize(true)
        mBinding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = ShoppingListRecyclerAdapter(this, this)
        mBinding.recyclerView.adapter = mAdapter
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService), this)
        )[MainViewModel::class.java]

        lifecycleScope.launch {
            mViewModel.dataState.collect {
                when (it) {
                    is DataState.Loading -> {

                    }

                    is DataState.IngredientResponse -> {
                        val ingredients = it.ingredients
                        Log.e("list", "initViewModel: " +ingredients.size )
                        mBinding.itemCount.text = "Total Items: ".plus(ingredients.size)
                        mAdapter.setIngredients(ingredients)
                    }

                    else -> {

                    }
                }
            }
        }
        fetchData()
    }

    private fun fetchData() {
        lifecycleScope.launch {
            mViewModel.dataIntent.send(
                DataIntent.FetchShoppingListData
            )
        }
    }

    override fun onIngredientSelected(ingredient: ExtendedIngredients, isChecked: Boolean) {

    }

    override fun onCounterValueChanged(ingredients: List<ExtendedIngredients>) {

        lifecycleScope.launch {
            mViewModel.dataIntent.send(
                DataIntent.AddToShoppingList(
                    ingredients
                )
            )
        }
    }
}