package com.binay.recipeapp.uis.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.binay.recipeapp.data.model.AnalyzedInstructions
import com.binay.recipeapp.data.model.ExtendedIngredients
import com.binay.recipeapp.databinding.FragmentInstructionsBinding

class IngredientsFragment: Fragment(), IngredientsRecyclerAdapter.IngredientClickListener {

    private lateinit var mBinding: FragmentInstructionsBinding
    private lateinit var viewModel: MyViewModel
    private var groceryList: MutableList<ExtendedIngredients> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentInstructionsBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initView()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[MyViewModel::class.java]
        viewModel.ingredients.observe(viewLifecycleOwner) { items ->
            populateIngredients(items)
        }
    }

    private fun initView() {
        mBinding.recyclerView.setHasFixedSize(true)
        mBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun populateIngredients(items: List<ExtendedIngredients>) {
        val mAdapter = IngredientsRecyclerAdapter(requireContext(), items, this)
        mBinding.recyclerView.adapter = mAdapter
    }

    override fun onIngredientSelected(ingredient: ExtendedIngredients, isChecked: Boolean) {
        if (isChecked) {
            groceryList.add(ingredient)
        } else groceryList.remove(ingredient)

        viewModel.isChecked.value = groceryList.isNotEmpty()
        viewModel.groceryList.value = groceryList
    }
}