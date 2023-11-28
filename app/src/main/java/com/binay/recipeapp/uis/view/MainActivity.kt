package com.binay.recipeapp.uis.view

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.binay.recipeapp.R
import com.binay.recipeapp.databinding.ActivityMainBinding
import com.binay.recipeapp.uis.view.search.SearchFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(),
    MoreFragment.MoreFragmentListener,
    HomeFragment.HomeFragmentListener,
    FavoriteFragment.FavoriteListener,
    SearchFragment.SearchListener {


    lateinit var binding: ActivityMainBinding


    private val imageList = intArrayOf(
        R.drawable.nav_home,
        R.drawable.nav_search,
        R.drawable.nav_fav,
        R.drawable.nav_convert
    )

    private lateinit var pagerAdapter: MyPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {

        binding.toolbar.list.setOnClickListener {
            startActivity(Intent(this, ShoppingListActivity::class.java))
        }

        binding.toolbar.externalLink.setOnClickListener {

        }

        binding.viewPager.isUserInputEnabled = false

        //code to change selected tab color
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.icon!!.setColorFilter(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.primary_color
                    ), PorterDuff.Mode.SRC_IN
                )
                binding.viewPager.setCurrentItem(tab.position, false)

                when (tab.position) {
                    1 -> {
                        binding.toolbar.toolbarTitle.text = getString(R.string.nav_search)
                    }

                    2 -> {
                        binding.toolbar.toolbarTitle.text = getString(R.string.nav_favorite)
                    }

                    3 -> {
                        binding.toolbar.toolbarTitle.text = getString(R.string.nav_converter)
                    }

                    else -> {
                        binding.toolbar.toolbarTitle.text = getString(R.string.app_name)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.icon!!.setColorFilter(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.default_tab_color
                    ), PorterDuff.Mode.SRC_IN
                )
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        val fragments: MutableList<Fragment> = ArrayList()
        fragments.add(HomeFragment())
        fragments.add(SearchFragment())
        fragments.add(FavoriteFragment())
        fragments.add(UnitConverterFragment())

        pagerAdapter = MyPagerAdapter(this, fragments)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            // Set tab text or leave it empty if you want to display only icons
            tab.text = ""
            tab.icon = ContextCompat.getDrawable(this, imageList[position])
        }.attach()
    }

    class MyPagerAdapter(fragmentActivity: FragmentActivity?, fragments: List<Fragment>) :
        FragmentStateAdapter(fragmentActivity!!) {
        private val fragments: List<Fragment>

        init {
            this.fragments = fragments
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }

        override fun getItemCount(): Int {
            return fragments.size
        }
    }

    override fun onUnitConverterClicked() {

    }

    override fun refreshFavoriteFragment() {
        Handler(Looper.getMainLooper()).post {
            val positionOfFavorite = 2
            val fragment = pagerAdapter.createFragment(positionOfFavorite)
            pagerAdapter.notifyItemChanged(positionOfFavorite)
            if (fragment is FavoriteFragment) {
                Log.e("Favorite fragment ", "refreshed")
            }
        }
    }

    override fun refreshHomeFragment() {
        Handler(Looper.getMainLooper()).post {
            val positionOfFavorite = 0
            val fragment = pagerAdapter.createFragment(positionOfFavorite)
            pagerAdapter.notifyItemChanged(positionOfFavorite)
            if (fragment is HomeFragment) {
                Log.e("Home fragment ", "refreshed")
            }
        }
    }
}