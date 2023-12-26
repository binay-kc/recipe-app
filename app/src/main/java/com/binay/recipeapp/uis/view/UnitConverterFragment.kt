package com.binay.recipeapp.uis.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.binay.recipeapp.R
import com.binay.recipeapp.databinding.FragmentUnitConverterBinding
import com.binay.recipeapp.uis.intent.UnitIntent
import com.binay.recipeapp.uis.viewmodel.UnitConverterViewModel
import com.binay.recipeapp.uis.viewstate.UnitState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UnitConverterFragment : Fragment() {

    private lateinit var mBinding: FragmentUnitConverterBinding
    private lateinit var mUnitArray: Array<String>

    private val mViewModel: UnitConverterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentUnitConverterBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initViewModel()
        initSpinnerTop()
        initSpinnerBottom()
        initSwap()
        initEditText()
    }

    private fun initViewModel() {
//        mViewModel = ViewModelProvider(
//            this,
//            ViewModelFactory(requireContext())
//        )[UnitConverterViewModel::class.java]

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            mViewModel.unitState.collect {
                when (it) {
                    is UnitState.Loading -> {

                    }

                    is UnitState.ResponseData -> {
                        mBinding.etSpinnerBottom.setText(it.convertedAmt)
                    }

                    else -> {

                    }
                }
            }
        }
    }

    private fun initSpinnerTop() {
        mUnitArray = resources.getStringArray(R.array.units_array)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.units_array,
            R.layout.layout_spinner_item
        )
            .also { adapter ->
                adapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item)
                mBinding.spinnerTop.adapter = adapter
            }

        mBinding.spinnerTop.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                convertUnit(mBinding.etSpinnerTop.text.toString())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun initSpinnerBottom() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.units_array,
            R.layout.layout_spinner_item
        )
            .also { adapter ->
                adapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item)
                mBinding.spinnerBottom.adapter = adapter
            }

        mBinding.spinnerBottom.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                convertUnit(mBinding.etSpinnerTop.text.toString())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun initSwap() {
        mBinding.cvSwap.setOnClickListener {
            val indexOfSelectedTop = mBinding.spinnerTop.selectedItemPosition
            val indexOfSelectedBottom = mBinding.spinnerBottom.selectedItemPosition

            mBinding.spinnerTop.setSelection(indexOfSelectedBottom)
            mBinding.spinnerBottom.setSelection(indexOfSelectedTop)
            convertUnit(mBinding.etSpinnerTop.text.toString())
        }
    }

    private fun initEditText() {
        mBinding.etSpinnerTop.doOnTextChanged { text, start, before, count ->
            convertUnit(text.toString())
        }
    }

    private fun convertUnit(amount: String?) {
        if (!amount.isNullOrEmpty()) {
            val indexOfSelectedTop = mBinding.spinnerTop.selectedItemPosition
            val indexOfSelectedBottom = mBinding.spinnerBottom.selectedItemPosition
            if (indexOfSelectedTop == indexOfSelectedBottom) {
                mBinding.etSpinnerBottom.setText(mBinding.etSpinnerTop.text.toString())
            } else {
                lifecycleScope.launch {
                    mViewModel.unitIntent.send(
                        UnitIntent.ConvertUnit(
                            mUnitArray[mBinding.spinnerTop.selectedItemPosition],
                            mUnitArray[mBinding.spinnerBottom.selectedItemPosition],
                            amount
                        )
                    )
                }
            }
        }
    }

}