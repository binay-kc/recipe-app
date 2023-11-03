package com.binay.recipeapp.uis.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.binay.recipeapp.R
import com.binay.recipeapp.data.api.ApiHelperImpl
import com.binay.recipeapp.data.api.RetrofitBuilder
import com.binay.recipeapp.data.local.favoriteDb.AppDatabase
import com.binay.recipeapp.data.model.RecipeData
import com.binay.recipeapp.databinding.FragmentHomeBinding
import com.binay.recipeapp.uis.intent.DataIntent
import com.binay.recipeapp.uis.intent.UnitIntent
import com.binay.recipeapp.uis.viewmodel.MainViewModel
import com.binay.recipeapp.uis.viewstate.DataState
import com.binay.recipeapp.uis.viewstate.UnitState
import com.binay.recipeapp.util.ViewModelFactory
import kotlinx.coroutines.launch
import java.util.Locale

class HomeFragment: Fragment(), OnCategoryClickListener {

    lateinit var binding: FragmentHomeBinding
    lateinit var adapter: CategoryRecyclerAdapter
    lateinit var recipeAdapter: RecipeRecyclerAdapter

    lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initViewModel()
        binding.categoryRecycler.setHasFixedSize(true)
        binding.categoryRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        adapter = CategoryRecyclerAdapter(requireContext(), resources.getStringArray(R.array.category_array), this)
        binding.categoryRecycler.adapter = adapter

        binding.recipeRecycler.setHasFixedSize(true)
        binding.recipeRecycler.layoutManager = GridLayoutManager(requireContext(), 2)

        fetchData("")

    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService), requireContext())
        )[MainViewModel::class.java]

        lifecycleScope.launch {
            viewModel.dataState.collect {
                when (it) {
                    is DataState.Loading -> {

                    }

                    is DataState.ResponseData -> {
                        Log.d("haancha", "initViewModel: "+it.recipeResponseData)
                        recipeAdapter = RecipeRecyclerAdapter(requireContext(), it.recipeResponseData.recipes,
                            object : RecipeRecyclerAdapter.RecipeClickListener{
                                override fun onFavoriteChanged(
                                    recipe: RecipeData,
                                    isToFavorite: Boolean
                                ) {
                                    changeFavoriteStatus(recipe,isToFavorite)
                                }

                                override fun onRecipeClicked(recipe: RecipeData) {

                                }

                            })
                        binding.recipeRecycler.adapter = recipeAdapter
                    }

                    is DataState.FavoriteResponse ->{
                        Log.d("Favorite"," Vayo")
                    }

                    else -> {

                    }
                }
            }
        }
    }

    private fun fetchData(tag: String) {
        lifecycleScope.launch {
            viewModel.dataIntent.send(
                DataIntent.FetchRecipeData(
                    tag
                )
            )
        }
    }

    private fun changeFavoriteStatus(recipe : RecipeData, isToFavorite : Boolean) {
        lifecycleScope.launch {
            viewModel.dataIntent.send(
                DataIntent.ChangeFavoriteStatus(
                    recipe, isToFavorite
                )
            )
        }
    }

    override fun categoryClick(position: Int) {

        adapter.updateAdapter(position)
        val cuisines = resources.getStringArray(R.array.category_array)

        when (position) {
            0 -> {
                fetchData("")
            }
            else -> {
                Log.d("hanyo", "categoryClick: " +position + cuisines[position])
                fetchData(cuisines[position].lowercase(Locale.ROOT))
            }
        }
    }
}