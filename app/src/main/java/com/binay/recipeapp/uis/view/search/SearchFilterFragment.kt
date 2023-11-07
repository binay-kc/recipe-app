package com.binay.recipeapp.uis.view.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.binay.recipeapp.R
import com.binay.recipeapp.databinding.FragmentSearchFilterBinding

private const val ARG_SEARCH_BY = "searchByToSelect"

//Initially planned to use this to display search filter
class SearchFilterFragment : DialogFragment() {
    private var selectedFilterIndex: Int = 0

    private lateinit var mBinding : FragmentSearchFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedFilterIndex = it.getInt(ARG_SEARCH_BY)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            it.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentSearchFilterBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView(){
        when(selectedFilterIndex){
            0 -> mBinding.chipRecipes.isSelected = true
            1-> mBinding.chipNutrients.isSelected = false
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(searchByToSelect: Int) =
            SearchFilterFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SEARCH_BY, searchByToSelect)
                }
            }
    }
}