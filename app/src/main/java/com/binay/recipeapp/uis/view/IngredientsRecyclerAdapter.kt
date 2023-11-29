package com.binay.recipeapp.uis.view

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.recyclerview.widget.RecyclerView
import com.binay.recipeapp.R
import com.binay.recipeapp.data.model.ExtendedIngredients
import com.binay.recipeapp.databinding.ItemIngredientsBinding

class IngredientsRecyclerAdapter(
    private var context: Context,
    private val mListener: IngredientClickListener): RecyclerView.Adapter<IngredientsRecyclerAdapter.IngredientViewHolder>() {

    private var ingredientList: List<ExtendedIngredients> = ArrayList()

    fun setIngredients(ingredientList: List<ExtendedIngredients>) {
        this.ingredientList = ingredientList
        notifyDataSetChanged()
    }

    class IngredientViewHolder(binding: ItemIngredientsBinding): RecyclerView.ViewHolder(binding.root) {
        val checkbox = binding.checkbox
        val ingredient = binding.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val mBinding = ItemIngredientsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngredientViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return ingredientList.size
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val item = ingredientList[position]

        holder.checkbox.isChecked = false
        holder.checkbox.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener,
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {

            }

            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                mListener.onIngredientSelected(item, p1)
            }

        })

        holder.ingredient.text = item.original
    }

    interface IngredientClickListener {
        fun onIngredientSelected(ingredients: ExtendedIngredients, isChecked: Boolean)
    }
}