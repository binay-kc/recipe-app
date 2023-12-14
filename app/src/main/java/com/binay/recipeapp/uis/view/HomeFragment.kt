package com.binay.recipeapp.uis.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.binay.recipeapp.R
import com.binay.recipeapp.data.api.ApiHelperImpl
import com.binay.recipeapp.data.api.RetrofitBuilder
import com.binay.recipeapp.data.model.RecipeData
import com.binay.recipeapp.data.model.RecipeResponseData
import com.binay.recipeapp.databinding.FragmentHomeBinding
import com.binay.recipeapp.uis.base.BaseFragment
import com.binay.recipeapp.uis.intent.DataIntent
import com.binay.recipeapp.uis.viewmodel.MainViewModel
import com.binay.recipeapp.uis.viewstate.DataState
import com.binay.recipeapp.util.NetworkUtil
import com.binay.recipeapp.util.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.Locale

class HomeFragment : BaseFragment(), OnCategoryClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: CategoryRecyclerAdapter
    private lateinit var recipeAdapter: RecipeRecyclerAdapter

    private lateinit var viewModel: MainViewModel

    private var mListener: HomeFragmentListener? = null
    private var responseData: RecipeResponseData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        binding.categoryRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        adapter = CategoryRecyclerAdapter(
            requireContext(),
            resources.getStringArray(R.array.category_array),
            this
        )
        binding.categoryRecycler.adapter = adapter

        binding.recipeRecycler.setHasFixedSize(true)
        binding.recipeRecycler.layoutManager = GridLayoutManager(requireContext(), 2)

        recipeAdapter = RecipeRecyclerAdapter(requireContext(),
            object : RecipeRecyclerAdapter.RecipeClickListener {
                override fun onFavoriteChanged(
                    recipe: RecipeData,
                    isToFavorite: Boolean
                ) {
                    try {
                        changeFavoriteStatus(recipe, isToFavorite)
                    } catch (e: Exception) {
                        Log.e("Here", " Favorite exception")
                    }
                }

                override fun onRecipeClicked(recipe: RecipeData) {
                    if (!NetworkUtil.isNetworkAvailable(requireContext())) {
                        Snackbar.make(binding.root, getString(R.string.no_connection), Snackbar.LENGTH_SHORT).show()
                        return
                    }
                    val intent = Intent(context, RecipeDetailActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("recipe_id", recipe.id)
                    startActivity(intent)
                }

            })
        binding.recipeRecycler.adapter = recipeAdapter

        binding.refreshLayout.setOnRefreshListener {
            if (!NetworkUtil.isNetworkAvailable(requireContext())) {
                binding.refreshLayout.isRefreshing = false
                Snackbar.make(binding.root, getString(R.string.no_connection), Snackbar.LENGTH_SHORT).show()
            } else
                getCategoryWiseData()
        }

        fetchData("all")
    }

    override fun onAttach(context: Context) {
        if (context is HomeFragmentListener) {
            mListener = context
        }
        super.onAttach(context)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService), requireContext(), mDatabase)
        )[MainViewModel::class.java]

        lifecycleScope.launch {
            viewModel.dataState.collect {
                when (it) {
                    is DataState.Loading -> {
                        Log.e("TAG", "initViewModel: loading")
                        if (responseData == null)
                            binding.progressBar.visibility = View.VISIBLE
                    }

                    is DataState.ResponseData -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recipeRecycler.visibility = View.VISIBLE
                        binding.refreshLayout.isRefreshing = false
                        binding.noInternetLayout.visibility = View.GONE

                        Log.d("haancha", "initViewModel: " + it.recipeResponseData)
                        responseData = it.recipeResponseData
                        recipeAdapter.setRecipes(it.recipeResponseData.recipes)
                        if (it.recipeResponseData.recipes.isEmpty() && !NetworkUtil.isNetworkAvailable(requireContext()))
                            binding.noInternetLayout.visibility = View.VISIBLE

                    }

                    is DataState.AddToFavoriteResponse -> {
                        Log.d("Favorite", " Vayo")
                        mListener?.refreshFavoriteFragment()
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

    private fun changeFavoriteStatus(recipe: RecipeData, isToFavorite: Boolean) {
        lifecycleScope.launch {
            viewModel.dataIntent.send(
                DataIntent.ChangeFavoriteStatus(
                    recipe, isToFavorite
                )
            )
        }
    }

    private var currentCategoryPosition = 0
    override fun categoryClick(position: Int) {

        currentCategoryPosition = position
        adapter.updateAdapter(position)
        binding.recipeRecycler.visibility = View.GONE

        responseData = null
        getCategoryWiseData()
    }

    private fun getCategoryWiseData() {
        binding.noInternetLayout.visibility = View.GONE
        val cuisines = resources.getStringArray(R.array.category_array)
        when (currentCategoryPosition) {
            0 -> {
                fetchData("all")
            }

            else -> {
                Log.d("hanyo", "categoryClick: " + currentCategoryPosition + cuisines[currentCategoryPosition])
                fetchData(cuisines[currentCategoryPosition].lowercase(Locale.ROOT))
            }
        }
    }

    interface HomeFragmentListener {
        fun refreshFavoriteFragment()
    }
}