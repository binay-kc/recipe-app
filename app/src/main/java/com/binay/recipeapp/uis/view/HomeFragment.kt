package com.binay.recipeapp.uis.view

import android.content.Context
import android.content.Intent
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
import com.binay.recipeapp.R
import com.binay.recipeapp.data.api.ApiHelperImpl
import com.binay.recipeapp.data.api.RetrofitBuilder
import com.binay.recipeapp.data.model.RecipeData
import com.binay.recipeapp.data.model.RecipeResponseData
import com.binay.recipeapp.databinding.FragmentHomeBinding
import com.binay.recipeapp.uis.intent.DataIntent
import com.binay.recipeapp.uis.viewmodel.MainViewModel
import com.binay.recipeapp.uis.viewstate.DataState
import com.binay.recipeapp.util.ViewModelFactory
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.Locale

class HomeFragment : Fragment(), OnCategoryClickListener {

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
                    val intent = Intent(context, RecipeDetailActivity::class.java)
                    intent.putExtra("recipe_id", recipe.id)
                    startActivity(intent)
                }

            })
        binding.recipeRecycler.adapter = recipeAdapter

        fetchData("")
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
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService), requireContext())
        )[MainViewModel::class.java]

        lifecycleScope.launch {
            viewModel.dataState.collect {
                when (it) {
                    is DataState.Loading -> {
                        Log.e("TAG", "initViewModel: loading", )
                        if (responseData == null)
                            binding.progressBar.visibility = View.VISIBLE
                    }

                    is DataState.ResponseData -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recipeRecycler.visibility = View.VISIBLE
                        Log.d("haancha", "initViewModel: " + it.recipeResponseData)
                        responseData = it.recipeResponseData
                        recipeAdapter.setRecipes(it.recipeResponseData.recipes)
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

    override fun categoryClick(position: Int) {

        adapter.updateAdapter(position)
        binding.recipeRecycler.visibility = View.GONE
        val cuisines = resources.getStringArray(R.array.category_array)

        responseData = null
        when (position) {
            0 -> {
                fetchData("")
            }

            else -> {
                Log.d("hanyo", "categoryClick: " + position + cuisines[position])
                fetchData(cuisines[position].lowercase(Locale.ROOT))
            }
        }
    }

    interface HomeFragmentListener {
        fun refreshFavoriteFragment()
    }
}