package com.example.foodies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodies.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClick: (Category, View) -> Unit // Match fragment's lambda signature
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        with(holder.binding) {
            categoryNameTextView.text = category.Name
            Glide.with(holder.itemView.context)
                .load(category.ImagePath)
                .placeholder(R.drawable.image2)
                .error(R.drawable.image2)
                .into(categoryImage)
            root.setOnClickListener {
                onCategoryClick(category, holder.itemView)
            }
        }
    }

    override fun getItemCount(): Int = categories.size
}