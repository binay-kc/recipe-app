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

class InstructionsFragment: Fragment() {

    private lateinit var mBinding: FragmentInstructionsBinding
    private lateinit var viewModel: MyViewModel

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
        viewModel.instructions.observe(viewLifecycleOwner) { items ->
            populateInstructions(items)
        }
    }

    private fun initView() {
//        mBinding.recyclerView.setHasFixedSize(true)
        mBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun populateInstructions(items: List<AnalyzedInstructions>) {
        Log.e("TAG", "populateInstructions: " + items[0])
        val mAdapter = items[0].steps?.let { InstructionsRecyclerAdapter(it) }
        mBinding.recyclerView.adapter = mAdapter
    }

}