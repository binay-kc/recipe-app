package com.binay.recipeapp.uis.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.binay.recipeapp.data.model.RecipeData
import com.binay.recipeapp.databinding.ItemRecipeBinding
import com.squareup.picasso.Picasso

class RecipeRecyclerAdapter(private val context: Context, private val recipeList: List<RecipeData>): RecyclerView.Adapter<RecipeRecyclerAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(binding: ItemRecipeBinding): RecyclerView.ViewHolder(binding.root) {
        val recipeName = binding.recipeName
        val recipeImage = binding.recipeImage
        val calories = binding.recipeCalorie
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.recipeName.text = recipeList[position].title

        if (recipeList[position].image?.isNotEmpty() == true)
            Picasso.with(context).load(recipeList[position].image).into(holder.recipeImage)

        holder.calories.text = "" + recipeList[position].readyInMinutes + " mins"
    }
}