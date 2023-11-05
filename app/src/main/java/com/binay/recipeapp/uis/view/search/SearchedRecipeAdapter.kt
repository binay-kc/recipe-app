package com.binay.recipeapp.uis.view.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.recyclerview.widget.RecyclerView
import com.binay.recipeapp.data.model.SearchedRecipe
import com.binay.recipeapp.databinding.ItemRecipeBinding
import com.squareup.picasso.Picasso

class SearchedRecipeAdapter(
    private val context: Context,
    private val mListener: RecipeClickListener
) : RecyclerView.Adapter<SearchedRecipeAdapter.RecipeViewHolder>() {

    private var recipeList: ArrayList<SearchedRecipe> = ArrayList()

    fun setRecipes(recipes: ArrayList<SearchedRecipe>) {
        this.recipeList = recipes
        notifyDataSetChanged()
    }

    class RecipeViewHolder(binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        val recipeName = binding.recipeName
        val recipeImage = binding.recipeImage
        val calories = binding.recipeCalorie
        val cbFavorite = binding.cbFavorite
        val rootView = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.recipeName.text = recipe.title

        if (recipe.image?.isNotEmpty() == true)
            Picasso.with(context).load(recipeList[position].image).into(holder.recipeImage)

        holder.calories.visibility = View.GONE

        holder.cbFavorite.isChecked = recipe.isFavorite

        holder.cbFavorite.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener,
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {

            }

            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                mListener.onFavoriteChanged(recipe, p1)
            }

        })

        holder.rootView.setOnClickListener {
            mListener.onRecipeClicked(recipe)
        }

    }

    interface RecipeClickListener {
        fun onFavoriteChanged(recipe: SearchedRecipe, isToFavorite: Boolean)
        fun onRecipeClicked(recipe: SearchedRecipe)
    }
}