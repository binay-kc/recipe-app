package com.binay.recipeapp.uis.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.binay.recipeapp.R
import com.binay.recipeapp.data.model.WebsiteData
import com.binay.recipeapp.databinding.ItemExternalWebsiteBinding
import com.binay.recipeapp.util.RandomColors

class WebsiteRecyclerAdapter(
    private val context: Context,
    private val mListener: OnWebsiteClickListener
): RecyclerView.Adapter<WebsiteRecyclerAdapter.WebsiteViewHolder>() {

    private var websiteList: List<WebsiteData> = ArrayList()

    fun setData(websites: List<WebsiteData>) {
        this.websiteList = websites
        notifyDataSetChanged()
    }

    class WebsiteViewHolder(mBinding: ItemExternalWebsiteBinding): RecyclerView.ViewHolder(mBinding.root) {
        val name = mBinding.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebsiteViewHolder {
        val mBinding = ItemExternalWebsiteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WebsiteViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return websiteList.size
    }

    override fun onBindViewHolder(holder: WebsiteViewHolder, position: Int) {
        val item = websiteList[position]

        holder.name.text = item.name
        holder.name.setBackgroundColor(RandomColors.generateRandomColor(context, R.drawable.icon_plus))

        holder.itemView.setOnClickListener {
            mListener.onWebsiteClicked(item)
        }
    }

    interface OnWebsiteClickListener {
        fun onWebsiteClicked(website: WebsiteData)
    }
}