package com.binay.recipeapp.uis.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.binay.recipeapp.R
import com.binay.recipeapp.databinding.ItemCategoryBinding

class CategoryRecyclerAdapter(private val context: Context, private val categoryArray: Array<String>, private val onCategoryClickListener: OnCategoryClickListener) : RecyclerView.Adapter<CategoryRecyclerAdapter.CategoryViewHolder>() {

    var selectedPosition: Int = 0

    inner class CategoryViewHolder(binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        val category = binding.categoryName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categoryArray.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        if (selectedPosition == position) {
            holder.itemView.background =
                ContextCompat.getDrawable(context, R.drawable.category_selected_bg)
            holder.category.setTextColor(ContextCompat.getColor(context, R.color.text_on_background))
        } else {
            holder.itemView.background =
                ContextCompat.getDrawable(context, R.drawable.category_bg)
            holder.category.setTextColor(ContextCompat.getColor(context, R.color.text_color))
        }

        holder.category.text = categoryArray[position]
        holder.itemView.setOnClickListener {
            onCategoryClickListener.categoryClick(position)
        }
    }

    fun updateAdapter(selectedPosition: Int) {
        this.selectedPosition = selectedPosition
        notifyDataSetChanged()
    }

}

interface OnCategoryClickListener {
    fun categoryClick(position: Int)
}