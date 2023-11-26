package com.binay.recipeapp.uis.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binay.recipeapp.R
import com.binay.recipeapp.databinding.FragmentCookingTimerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private const val ARG_TIME_IN_MINUTES = "timeInMinutes"

class CookingTimerFragment : BottomSheetDialogFragment() {

    private var timeInMinutes: Int? = null
    private lateinit var mBinding : FragmentCookingTimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            timeInMinutes = it.getInt(ARG_TIME_IN_MINUTES)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCookingTimerBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance(timeInMinutes: Int) =
            CookingTimerFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TIME_IN_MINUTES, timeInMinutes)
                }
            }

        const val TAG = "CookingTimerFragment"
    }
}