package com.binay.recipeapp.uis.view

import androidx.fragment.app.Fragment

import com.binay.recipeapp.R

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.ListFragment
import com.binay.recipeapp.databinding.FragmentMoreBinding

class MoreFragment : ListFragment() {

    private lateinit var binding: FragmentMoreBinding

    private var mListener: MoreFragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val adapter = ArrayAdapter.createFromResource(requireContext(),
//            R.array.more_fragment_list,
//            R.layout.layout_spinner_dropdown_item)
//        listAdapter = adapter
//        listView.onItemClickListener =
//            AdapterView.OnItemClickListener { p0, p1, p2, p3 ->
//                if(p2==0) {
//                    mListener?.onUnitConverterClicked()
//                }
//            }
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MoreFragmentListener) {
            mListener = context
        }
    }


    interface MoreFragmentListener {
        fun onUnitConverterClicked()
    }
}