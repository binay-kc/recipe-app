package com.binay.recipeapp.uis.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.binay.recipeapp.R
import com.binay.recipeapp.data.model.RecipeData
import com.binay.recipeapp.databinding.FragmentFavoriteBinding
import com.binay.recipeapp.uis.intent.DataIntent
import com.binay.recipeapp.uis.viewmodel.MainViewModel
import com.binay.recipeapp.uis.viewstate.DataState
import com.binay.recipeapp.util.NetworkUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {

    private lateinit var mBinding: FragmentFavoriteBinding
    private lateinit var mAdapter: RecipeRecyclerAdapter
    private val mViewModel: MainViewModel by viewModels()


    private var mListener: FavoriteListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFavoriteBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    override fun onAttach(context: Context) {
        if (context is FavoriteListener) {
            mListener = context
        }
        super.onAttach(context)
    }

    private fun initView() {
        mAdapter = RecipeRecyclerAdapter(requireContext(),
            object : RecipeRecyclerAdapter.RecipeClickListener {
                override fun onFavoriteChanged(
                    recipe: RecipeData,
                    isToFavorite: Boolean
                ) {
                    changeFavoriteStatus(recipe, isToFavorite)
                }

                override fun onRecipeClicked(recipe: RecipeData) {
                    if (!NetworkUtil.isNetworkAvailable(requireContext())) {
                        Snackbar.make(
                            mBinding.root,
                            getString(R.string.no_connection),
                            Snackbar.LENGTH_SHORT
                        ).show()
                        return
                    }
                    val intent = Intent(context, RecipeDetailActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("recipe_id", recipe.id)
                    startActivity(intent)
                }

            })

        mBinding.rvFavorite.adapter = mAdapter

        mBinding.rvFavorite.setHasFixedSize(true)
        mBinding.rvFavorite.layoutManager = GridLayoutManager(requireContext(), 2)

    }

    private fun initViewModel() {
        lifecycleScope.launch {
            mViewModel.dataState.collect {
                when (it) {
                    is DataState.Loading -> {

                    }

                    is DataState.ResponseData -> {

                    }

                    is DataState.AddToFavoriteResponse -> {
                        val isFavorite = it.recipe.isFavorite ?: true
                        if (!isFavorite) {
                            mAdapter.removeRecipe(it.recipe)
                        }
                        mListener?.refreshHomeFragment()
                    }

                    is DataState.FavoriteResponse -> {
                        val recipes = it.recipes ?: ArrayList()
                        mAdapter.setRecipes(recipes)
                    }

                    else -> {

                    }
                }
            }
        }

        fetchData()
    }


    private fun changeFavoriteStatus(recipe: RecipeData, isToFavorite: Boolean) {
        lifecycleScope.launch {
            mViewModel.dataIntent.send(
                DataIntent.ChangeFavoriteStatus(
                    recipe, isToFavorite
                )
            )
        }
    }


    private fun fetchData() {
        lifecycleScope.launch {
            mViewModel.dataIntent.send(
                DataIntent.FetchFavoriteRecipe(
                    true
                )
            )
        }
    }


    interface FavoriteListener {
        fun refreshHomeFragment()
    }
}