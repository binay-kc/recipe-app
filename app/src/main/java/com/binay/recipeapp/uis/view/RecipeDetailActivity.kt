package com.binay.recipeapp.uis.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.binay.recipeapp.R
import com.binay.recipeapp.data.api.ApiHelperImpl
import com.binay.recipeapp.data.api.RetrofitBuilder
import com.binay.recipeapp.data.model.AnalyzedInstructions
import com.binay.recipeapp.data.model.ExtendedIngredients
import com.binay.recipeapp.data.model.RecipeData
import com.binay.recipeapp.databinding.ActivityRecipedetailBinding
import com.binay.recipeapp.uis.intent.DataIntent
import com.binay.recipeapp.uis.viewmodel.MainViewModel
import com.binay.recipeapp.uis.viewstate.DataState
import com.binay.recipeapp.util.ViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import kotlin.math.abs

class RecipeDetailActivity: AppCompatActivity(), TabLayout.OnTabSelectedListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var mBinding: ActivityRecipedetailBinding
    private var recipeId = 0

    private var isToolbarVisible = false
    private val titleList = arrayOf("Ingredients", "Instructions")

    private lateinit var fragmentViewModel: MyViewModel
    private lateinit var recipeData: RecipeData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRecipedetailBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        recipeId = intent.getIntExtra("recipe_id", 0)

        initViewModel()
        initView()

        fetchDetailData(recipeId)
    }

    private fun initView() {
        setSupportActionBar(mBinding.toolbar)

        mBinding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        mBinding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val maxScroll = appBarLayout.totalScrollRange
            val percentage = (abs(verticalOffset).toFloat() / maxScroll.toFloat())

            if (percentage > 0.7 && !isToolbarVisible) {
                // Change toolbar title color to white and toolbar background color
                mBinding.toolbar.background = ContextCompat.getDrawable(this, R.drawable.toolbar_background)
                mBinding.toolbarTitle.visibility = View.VISIBLE
                mBinding.recipeName.visibility = View.GONE
                isToolbarVisible = true
            } else if (percentage <= 0.7 && isToolbarVisible) {
                // Change toolbar title color to your desired color and toolbar background color
                mBinding.toolbar.background = ColorDrawable(Color.TRANSPARENT)
                mBinding.toolbarTitle.visibility = View.GONE
                mBinding.recipeName.visibility = View.VISIBLE
                isToolbarVisible = false
            }
        }
    }

    private fun initViewModel() {

        fragmentViewModel = ViewModelProvider(this)[MyViewModel::class.java]
        fragmentViewModel.isChecked.observe(this) {
            if (it)
                mBinding.addToListButton.visibility = View.VISIBLE
            else
                mBinding.addToListButton.visibility = View.GONE
        }

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
                        recipeData = it.recipeData
                        populateView(recipeData)
                    }

                    else -> {

                    }
                }
            }
        }
    }

    private fun populateView(recipeData: RecipeData) {
        mBinding.toolbarTitle.text = recipeData.title
        mBinding.recipeName.text = recipeData.title

        mBinding.addToListButton.setOnClickListener {
            addToShoppingList()
        }

        var mealType = ""
        for (cuisine in recipeData.cuisines!!) {
            mealType += cuisine.plus(", ") //-2 below coz i added 2 characters here
        }

        if (mealType.isNotEmpty())
            mBinding.mealType.text = mealType.substring(0, mealType.length - 2)

        Picasso.with(this).load(recipeData.image).into(mBinding.recipeImage)

        if (recipeData.vegetarian == true) {
            mBinding.recipeDetail.vegImage.setImageResource(R.drawable.icon_veg)
            mBinding.recipeDetail.vegTag.text = "Vegetarian"
        } else {
            mBinding.recipeDetail.vegImage.setImageResource(R.drawable.icon_meat)
            mBinding.recipeDetail.vegTag.text = "Contains Egg/Meat"
        }

        mBinding.recipeDetail.timerTag.text = recipeData.readyInMinutes.toString() + " mins"
        mBinding.recipeDetail.servingTag.text = recipeData.servings.toString() + " servings"

        for (nutrients in recipeData.nutrition?.nutrients!!) {
            if (nutrients.name.equals("calories", true)) {
                mBinding.recipeDetail.calorieTag.text = nutrients.amount.toString() + " " +nutrients.unit + "/serving"
                break
            }
        }

        val fragments: ArrayList<Fragment> = ArrayList()
        fragments.add(IngredientsFragment())
        fragments.add(InstructionsFragment())
        mBinding.recipeDetail.tabLayout.addOnTabSelectedListener(this)
        val pagerAdapter = MyPagerAdapter(this, fragments)
        mBinding.recipeDetail.viewPager.adapter = pagerAdapter

        TabLayoutMediator(mBinding.recipeDetail.tabLayout, mBinding.recipeDetail.viewPager) { tab, position ->
            // Set tab text or leave it empty if you want to display only icons
            tab.text = titleList[position]
        }.attach()
    }

    private fun addToShoppingList() {
        fragmentViewModel.groceryList.observe(this) {items ->
            Log.e("grocery", "populateView: " +items.size)
            lifecycleScope.launch {
                viewModel.dataIntent.send(
                    DataIntent.AddToShoppingList(
                        items
                    )
                )
            }
            fragmentViewModel.groceryList.removeObservers(this)
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

    override fun onTabSelected(tab: TabLayout.Tab) {

        when(tab.position) {
            0 -> { //ingredients
                fragmentViewModel.ingredients.value = recipeData.extendedIngredients
            }
            else -> { //instructions
                mBinding.addToListButton.visibility = View.GONE
                fragmentViewModel.instructions.value = recipeData.analyzedInstructions
            }
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }
}

class MyPagerAdapter(fragmentActivity: FragmentActivity?, fragments: ArrayList<Fragment>) :
    FragmentStateAdapter(fragmentActivity!!) {

    private val fragments = fragments

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return fragments.size
    }
}

class MyViewModel : ViewModel() {
    val ingredients = MutableLiveData<List<ExtendedIngredients>>()
    val instructions = MutableLiveData<List<AnalyzedInstructions>>()
    val isChecked = MutableLiveData<Boolean>()
    val groceryList = MutableLiveData<List<ExtendedIngredients>>()
}