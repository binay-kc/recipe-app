package com.binay.recipeapp.uis.view

import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.binay.recipeapp.R
import com.binay.recipeapp.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.GONE
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(), MoreFragment.MoreFragmentListener {


    lateinit var binding: ActivityMainBinding


    val imageList = intArrayOf(R.drawable.nav_home, R.drawable.nav_search, R.drawable.nav_fav, R.drawable.nav_more,R.drawable.ic_swap)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.viewPager.isUserInputEnabled = false

        val fragments: MutableList<Fragment> = ArrayList()
        fragments.add(HomeFragment())
        fragments.add(SearchFragment())
        fragments.add(FavoriteFragment())
        fragments.add(UnitConverterFragment())

        //code to change selected tab color

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.icon!!.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.primary_color), PorterDuff.Mode.SRC_IN)
                binding.viewPager.setCurrentItem(tab.position, false)

                when(tab.position) {
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

        val pagerAdapter = MyPagerAdapter(this, fragments)
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
}