package com.binay.recipeapp.uis.view

import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowMetrics
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.binay.recipeapp.R
import com.binay.recipeapp.data.api.ApiHelperImpl
import com.binay.recipeapp.data.api.RetrofitBuilder
import com.binay.recipeapp.data.model.RecipeData
import com.binay.recipeapp.databinding.FragmentRandomRecipeBinding
import com.binay.recipeapp.uis.intent.DataIntent
import com.binay.recipeapp.uis.viewmodel.FragmentDataViewModel
import com.binay.recipeapp.uis.viewmodel.MainViewModel
import com.binay.recipeapp.uis.viewstate.DataState
import com.binay.recipeapp.util.NetworkUtil
import com.binay.recipeapp.util.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class RandomRecipeFragment : DialogFragment() {

    private lateinit var mBinding: FragmentRandomRecipeBinding

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

        val screenWidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = requireActivity().windowManager.currentWindowMetrics
            windowMetrics.bounds.width()
        } else {
            val display: Display = requireActivity().windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            size.x
        }

        dialog?.window?.let {
            it.setLayout(
                (screenWidth / 1.1).roundToInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initView()
    }

    private fun initView() {
        mBinding.explore.setOnClickListener {
            if (recipeId != -1) {
                if (!NetworkUtil.isNetworkAvailable(requireContext())) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content), getString(R.string.no_connection), Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val intent = Intent(context, RecipeDetailActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra("recipe_id", recipeId)
                startActivity(intent)
                dismiss()
            }
        }
        mBinding.close.setOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(requireActivity())[FragmentDataViewModel::class.java]
        viewModel.randomRecipe.observe(viewLifecycleOwner) {
            if (it.image?.isNotEmpty() == true)
                Picasso.with(context).load(it.image).into(mBinding.recipeImage)
            displayRecipeInfo(it)
        }
    }

    private fun displayRecipeInfo(recipe: RecipeData) {
        recipeId = recipe.id
        mBinding.recipeName.text = recipe.title
        if (recipe.vegetarian == true) {
            mBinding.vegImage.setImageResource(R.drawable.icon_veg)
            mBinding.vegTag.text = "Vegetarian"
        } else {
            mBinding.vegImage.setImageResource(R.drawable.icon_meat)
            mBinding.vegTag.text = "Contains Egg/Meat"
        }

        mBinding.timerTag.text = "${recipe.readyInMinutes} mins"

    }
}