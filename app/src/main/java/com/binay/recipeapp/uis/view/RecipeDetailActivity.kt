package com.binay.recipeapp.uis.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.binay.recipeapp.R
import com.binay.recipeapp.data.model.RecipeData
import com.binay.recipeapp.databinding.ActivityRecipedetailBinding
import com.binay.recipeapp.uis.intent.DataIntent
import com.binay.recipeapp.uis.view.cookingTimer.CookingTimerActivity
import com.binay.recipeapp.uis.viewmodel.FragmentDataViewModel
import com.binay.recipeapp.uis.viewmodel.MainViewModel
import com.binay.recipeapp.uis.viewstate.DataState
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class RecipeDetailActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var mBinding: ActivityRecipedetailBinding
    private var recipeId = 0
    private var readyInMinutes: Int? = null
    private var recipeName = ""

    private var isToolbarVisible = false
    private val titleList = arrayOf("Ingredients", "Instructions")

    private lateinit var fragmentViewModel: FragmentDataViewModel
    private lateinit var recipeData: RecipeData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRecipedetailBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        recipeId = intent?.data?.getQueryParameter("id")?.toInt() ?: 0
        if (recipeId == 0)
            recipeId = intent.getIntExtra("recipe_id", 0)

        initViewModel()
        initView()

        fetchDetailData(recipeId)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        recipeId = intent?.data?.getQueryParameter("id")?.toInt() ?: 0
        fetchDetailData(recipeId)
    }

    private fun initView() {
        setSupportActionBar(mBinding.toolbar)

        mBinding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        mBinding.addToListButton.setOnClickListener {
            addToShoppingList()
        }

        mBinding.btnStartCooking.setOnClickListener {
            mBinding.btnStartCooking.text = "View Timer"
            if (readyInMinutes != null) {
                val cookingFragment =
                    CookingTimerActivity().newInstance(readyInMinutes!!, recipeName)
                cookingFragment?.show(
                    supportFragmentManager,
                    CookingTimerActivity::class.java.canonicalName
                )
            }
        }

        mBinding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val maxScroll = appBarLayout.totalScrollRange
            val percentage = (abs(verticalOffset).toFloat() / maxScroll.toFloat())

            if (percentage > 0.7 && !isToolbarVisible) {
                // Change toolbar title color to white and toolbar background color
                mBinding.toolbar.background =
                    ContextCompat.getDrawable(this, R.drawable.toolbar_background)
                mBinding.toolbarTitle.visibility = View.VISIBLE
                mBinding.recipeName.visibility = View.GONE
                isToolbarVisible = true
            } else if (percentage <= 0.7 && isToolbarVisible) {
                // Change toolbar title color to your desired color and toolbar background color
                mBinding.toolbar.background = ColorDrawable(Color.TRANSPARENT)
                mBinding.toolbarTitle.visibility = View.INVISIBLE
                mBinding.recipeName.visibility = View.VISIBLE
                isToolbarVisible = false
            }
        }

        mBinding.shareBtn.setOnClickListener {
            val deepLink = "http://open.my.recipe/detail?id=$recipeId"
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check out this recipe: $deepLink")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    private fun initViewModel() {

        fragmentViewModel = ViewModelProvider(this)[FragmentDataViewModel::class.java]
        fragmentViewModel.isChecked.observe(this) {
            if (it)
                mBinding.addToListButton.visibility = View.VISIBLE
            else
                mBinding.addToListButton.visibility = View.GONE
        }

//        viewModel = ViewModelProvider(
//            this,
//            ViewModelFactory(this)
//        )[MainViewModel::class.java]

        lifecycleScope.launch {
            viewModel.dataState.collect {
                when (it) {
                    is DataState.Loading -> {

                    }

                    is DataState.RecipeDetail -> {
                        Log.d("haancha", "initViewModel: " + it.recipeData)
                        recipeData = it.recipeData
                        recipeName = recipeData.title ?: ""
                        readyInMinutes = recipeData.readyInMinutes
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
                mBinding.recipeDetail.calorieTag.text =
                    nutrients.amount.toString() + " " + nutrients.unit + "/serving"
                break
            }
        }

        val fragments: ArrayList<Fragment> = ArrayList()
        fragments.add(IngredientsFragment())
        fragments.add(InstructionsFragment())
        mBinding.recipeDetail.tabLayout.addOnTabSelectedListener(this)
        val pagerAdapter = MyPagerAdapter(this, fragments)
        mBinding.recipeDetail.viewPager.adapter = pagerAdapter

        TabLayoutMediator(
            mBinding.recipeDetail.tabLayout,
            mBinding.recipeDetail.viewPager
        ) { tab, position ->
            // Set tab text or leave it empty if you want to display only icons
            tab.text = titleList[position]
        }.attach()
    }

    private fun addToShoppingList() {
        fragmentViewModel.groceryList.observe(this) { items ->
            Log.e("grocery", "populateView: " + items.size)
            lifecycleScope.launch {
                viewModel.dataIntent.send(
                    DataIntent.AddToShoppingList(
                        items
                    )
                )
            }
            fragmentViewModel.groceryList.removeObservers(this)
        }
        fragmentViewModel.isAddedToList.value = true
        mBinding.addToListButton.visibility = View.GONE
        Snackbar.make(mBinding.root, "Added to Grocery List successfully", Snackbar.LENGTH_SHORT)
            .show()
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

    override fun onDestroy() {
        super.onDestroy()

        val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putLong("started_at", 0)
        editor?.apply()
    }

    override fun onTabSelected(tab: TabLayout.Tab) {

        when (tab.position) {
            0 -> { //ingredients
                mBinding.btnStartCooking.visibility = View.GONE
                fragmentViewModel.ingredients.value = recipeData.extendedIngredients
            }

            else -> { //instructions
                mBinding.addToListButton.visibility = View.GONE
                if (readyInMinutes != null) mBinding.btnStartCooking.visibility =
                    View.VISIBLE
                val instructions = recipeData.analyzedInstructions
                if (!instructions.isNullOrEmpty()) instructions[0].readyInMinutes = readyInMinutes
                fragmentViewModel.instructions.value = instructions
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