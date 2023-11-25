package com.binay.recipeapp.uis.view

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.binay.recipeapp.data.api.ApiHelperImpl
import com.binay.recipeapp.data.api.RetrofitBuilder
import com.binay.recipeapp.data.model.RecipeData
import com.binay.recipeapp.databinding.FragmentRandomRecipeBinding
import com.binay.recipeapp.uis.intent.DataIntent
import com.binay.recipeapp.uis.viewmodel.MainViewModel
import com.binay.recipeapp.uis.viewstate.DataState
import com.binay.recipeapp.util.ViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class RandomRecipeFragment : DialogFragment() {

    private lateinit var mBinding: FragmentRandomRecipeBinding
    private lateinit var mViewModel: MainViewModel

    private var recipeId = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentRandomRecipeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            it.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT ,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
        fetchData()
    }

    private fun initView() {
        mBinding.btnCancel.setOnClickListener {
            dismiss()
        }

        mBinding.btnSeeDetail.setOnClickListener {
            if (recipeId != -1) {
                val intent = Intent(context, RecipeDetailActivity::class.java)
                intent.putExtra("recipe_id", recipeId)
                startActivity(intent)
                dismiss()
            }
        }
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService), requireContext())
        )[MainViewModel::class.java]


        lifecycleScope.launch {
            mViewModel.dataState.collect {
                when (it) {
                    is DataState.Loading -> {
                        Log.e("TAG", "initViewModel: loading")
                    }

                    is DataState.ResponseData -> {
                        if (it.recipeResponseData.recipes.isEmpty()) {
                            dismiss()
                            return@collect
                        }

                        val recipe = it.recipeResponseData.recipes[0]
                        displayRecipeInfo(recipe)

                        if (recipe.image?.isNotEmpty() == true)
                            Picasso.with(context).load(recipe.image).into(mBinding.recipeImage)

                    }

                    else -> {

                    }
                }
            }
        }
    }

    private fun displayRecipeInfo(recipe: RecipeData) {
        mBinding.llButtons.visibility = View.VISIBLE
        recipeId = recipe.id
        mBinding.recipeName.text = recipe.title
        mBinding.recipeCalorie.text = "${recipe.readyInMinutes} mins"
    }

    private fun fetchData() {
        lifecycleScope.launch {
            mViewModel.dataIntent.send(
                DataIntent.FetchRandomRecipe
            )
        }
    }
}