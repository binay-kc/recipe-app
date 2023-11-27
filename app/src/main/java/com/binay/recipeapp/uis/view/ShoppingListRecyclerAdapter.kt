package com.binay.recipeapp.uis.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.recyclerview.widget.RecyclerView
import com.binay.recipeapp.data.model.ExtendedIngredients
import com.binay.recipeapp.databinding.ItemGroceryListBinding
import com.squareup.picasso.Picasso


class ShoppingListRecyclerAdapter(
    val context: Context,
    val mListener: GroceryItemClickListener): RecyclerView.Adapter<ShoppingListRecyclerAdapter.ViewHolder>() {

    private var ingredientList: MutableList<ExtendedIngredients> = ArrayList()

    fun setIngredients(ingredientList: MutableList<ExtendedIngredients>) {
        this.ingredientList = ingredientList
        notifyDataSetChanged()
    }

    class ViewHolder(mBinding: ItemGroceryListBinding): RecyclerView.ViewHolder(mBinding.root) {
        val title = mBinding.name
        val checkbox = mBinding.checkbox
        val image = mBinding.image
        val increase = mBinding.increase
        val decrease = mBinding.decrease
        val counter = mBinding.count

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mBinding = ItemGroceryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return ingredientList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = ingredientList[position]
        var count = item.count

        holder.counter.setText(item.count.toString())

        holder.title.text = item.name
        Picasso.with(context).load("https://spoonacular.com/cdn/ingredients_100x100/".plus(item.image)).into(holder.image)

        holder.decrease.setOnClickListener {
            if (count > 1) count--

            item.count = count
            holder.counter.setText(item.count.toString())
            ingredientList.add(position, item)
            mListener.onCounterValueChanged(ingredientList)
        }

        holder.increase.setOnClickListener {
            if (count < 999) count++

            item.count = count
            holder.counter.setText(item.count.toString())
            mListener.onCounterValueChanged(ingredientList)
        }

        holder.checkbox.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener,
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {

            }

            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if (p1)
                    mListener.onIngredientSelected(item, p1)
            }

        })
    }

    interface GroceryItemClickListener {
        fun onIngredientSelected(ingredient: ExtendedIngredients, isChecked: Boolean)
        fun onCounterValueChanged(ingredients: List<ExtendedIngredients>)
    }
}