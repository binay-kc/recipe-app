package com.binay.recipeapp.uis.view.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.binay.recipeapp.R
import com.binay.recipeapp.data.model.SearchedRecipe
import com.binay.recipeapp.databinding.FragmentSearchBinding
import com.binay.recipeapp.uis.intent.DataIntent
import com.binay.recipeapp.uis.view.HomeFragment
import com.binay.recipeapp.uis.view.RecipeDetailActivity
import com.binay.recipeapp.uis.viewmodel.MainViewModel
import com.binay.recipeapp.uis.viewstate.DataState
import com.binay.recipeapp.util.NetworkUtil
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception
@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val mViewModel: MainViewModel by viewModels()
    private lateinit var mAdapter: SearchedRecipeAdapter
    private var mListener: SearchListener? = null

    //    Use this to search by recipes or nutrients
    private var isToSearchByRecipes = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onAttach(context: Context) {
        if (context is SearchListener) {
            mListener = context
        }
        super.onAttach(context)
    }

    private fun initView() {
        initViewModel()
        initSearchBy()

        binding.rvSearchRecipes.setHasFixedSize(true)
        binding.rvSearchRecipes.layoutManager = GridLayoutManager(requireContext(), 2)

        mAdapter = SearchedRecipeAdapter(requireContext(),
            object : SearchedRecipeAdapter.RecipeClickListener {
                override fun onFavoriteChanged(
                    recipe: SearchedRecipe,
                    isToFavorite: Boolean
                ) {
                    try {
                        changeFavoriteStatus(recipe, isToFavorite)
                    } catch (e: Exception) {
                        Log.e("Here", " Favorite exception")
                    }
                }

                override fun onRecipeClicked(recipe: SearchedRecipe) {
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
        binding.rvSearchRecipes.adapter = mAdapter

        binding.svRecipe.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (!p0.isNullOrEmpty() && p0.length >= 3) {
                        searchRecipe(p0)
                    } else {
                        mAdapter.setRecipes(ArrayList())
                    }
                }, 900)
                return false
            }
        })
    }

    private fun initSearchBy() {
        val layoutSearchFilter = binding.layoutSearchFilter
        layoutSearchFilter.cgSearchBy.setOnCheckedStateChangeListener { _, checkedId ->
            val selectedChip = checkedId.contains(layoutSearchFilter.chipRecipes.id)
            isToSearchByRecipes = selectedChip
            binding.svRecipe.setQuery("", false)
            if (isToSearchByRecipes) {
                binding.note.visibility = View.GONE
                binding.svRecipe.queryHint = getString(R.string.recipe_search_hint)
            } else {
                binding.note.visibility = View.VISIBLE
                binding.svRecipe.queryHint = getString(R.string.ingredient_search_hint)
            }
        }
    }

    private fun initViewModel() {
//        mViewModel = ViewModelProvider(
//            this,
//            ViewModelFactory(requireContext())
//        )[MainViewModel::class.java]

        lifecycleScope.launch {
            mViewModel.dataState.collect {
                when (it) {
                    is DataState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is DataState.ResponseData -> {

                    }

                    is DataState.SearchRecipes -> {
                        binding.progressBar.visibility = View.GONE
                        val recipes = it.searchRecipeData.results ?: ArrayList()
                        mAdapter.setRecipes(recipes)
                    }

                    is DataState.AddToFavoriteResponse -> {
                        mListener?.refreshFavoriteFragment()
                        mListener?.refreshHomeFragment()
                    }

                    is DataState.SearchRecipesByNutrients -> {
                        binding.progressBar.visibility = View.GONE
                        mAdapter.setRecipes(it.searchedRecipes)
                    }

                    else -> {

                    }
                }
            }
        }
    }

    private fun searchRecipe(query: String) {
        lifecycleScope.launch {
            if (isToSearchByRecipes) {
                binding.svRecipe.queryHint = getString(R.string.recipe_search_hint)
                mViewModel.dataIntent.send(
                    DataIntent.SearchRecipe(
                        query
                    )
                )
            } else {
                binding.svRecipe.queryHint = getString(R.string.ingredient_search_hint)
                mViewModel.dataIntent.send(DataIntent.SearchRecipesByNutrients(query))
            }
        }
    }

    private fun changeFavoriteStatus(recipe: SearchedRecipe, isToFavorite: Boolean) {
        lifecycleScope.launch {
            mViewModel.dataIntent.send(
                DataIntent.ChangeFavoriteStatusFromSearch(
                    recipe, isToFavorite
                )
            )
        }
    }

    interface SearchListener : HomeFragment.HomeFragmentListener {
        fun refreshHomeFragment()
    }
}