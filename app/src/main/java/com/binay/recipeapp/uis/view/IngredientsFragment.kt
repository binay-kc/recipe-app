package com.binay.recipeapp.uis.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.binay.recipeapp.data.model.ExtendedIngredients
import com.binay.recipeapp.databinding.FragmentInstructionsBinding
import com.binay.recipeapp.uis.viewmodel.FragmentDataViewModel

class IngredientsFragment: Fragment(), IngredientsRecyclerAdapter.IngredientClickListener {

    private lateinit var mBinding: FragmentInstructionsBinding
    private lateinit var viewModel: FragmentDataViewModel
    private var groceryList: MutableList<ExtendedIngredients> = ArrayList() //to be added to shopping list
    private lateinit var mAdapter: IngredientsRecyclerAdapter
    private var ingredientList: List<ExtendedIngredients> = ArrayList() //list to be shown here

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

        initView()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[FragmentDataViewModel::class.java]
        viewModel.ingredients.observe(viewLifecycleOwner) { items ->
            ingredientList = items
            mAdapter.setIngredients(items)
        }

        viewModel.isAddedToList.observe(viewLifecycleOwner) {
            viewModel.isAddedToList.removeObservers(this)
            mAdapter.setIngredients(ingredientList)
        }
    }

    private fun initView() {
        mBinding.recyclerView.setHasFixedSize(true)
        mBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        mAdapter = IngredientsRecyclerAdapter(requireContext(), this)
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