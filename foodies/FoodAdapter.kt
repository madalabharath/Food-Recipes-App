package com.example.foodies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodies.databinding.ItemFoodLeftBinding
import com.example.foodies.databinding.ItemFoodRightBinding

class FoodAdapter(
    private val foods: List<Food>,
    private val onFoodClick: (Food) -> Unit
) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_LEFT = 0
        private const val VIEW_TYPE_RIGHT = 1
    }

    abstract class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(food: Food)
    }

    class LeftViewHolder(
        private val binding: ItemFoodLeftBinding,
        private val onFoodClick: (Food) -> Unit
    ) : ViewHolder(binding.root) {
        override fun bind(food: Food) {
            binding.foodTitleTextView.text = food.Title ?: "Unknown"
            binding.foodCalorieTextView.text = food.Calorie?.toString()?.plus(" kcal") ?: "N/A"
            binding.foodStarTextView.text = food.Star?.toString() ?: "N/A"
            binding.foodTimeTextView.text = food.TimeValue?.toString()?.plus(" min") ?: "N/A"
            Glide.with(binding.foodImage.context)
                .load(food.ImagePath)
                .placeholder(R.drawable.image2)
                .error(R.drawable.image2)
                .into(binding.foodImage)
            binding.root.setOnClickListener { onFoodClick(food) }
        }
    }

    class RightViewHolder(
        private val binding: ItemFoodRightBinding,
        private val onFoodClick: (Food) -> Unit
    ) : ViewHolder(binding.root) {
        override fun bind(food: Food) {
            binding.foodTitleTextView.text = food.Title ?: "Unknown"
            binding.foodCalorieTextView.text = food.Calorie?.toString()?.plus(" kcal") ?: "N/A"
            binding.foodStarTextView.text = food.Star?.toString() ?: "N/A"
            binding.foodTimeTextView.text = food.TimeValue?.toString()?.plus(" min") ?: "N/A"
            Glide.with(binding.foodImage.context)
                .load(food.ImagePath)
                .placeholder(R.drawable.image2)
                .error(R.drawable.image2)
                .into(binding.foodImage)
            binding.root.setOnClickListener { onFoodClick(food) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) VIEW_TYPE_LEFT else VIEW_TYPE_RIGHT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LEFT -> {
                val binding = ItemFoodLeftBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                LeftViewHolder(binding, onFoodClick)
            }
            VIEW_TYPE_RIGHT -> {
                val binding = ItemFoodRightBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                RightViewHolder(binding, onFoodClick)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(foods[position])
    }

    override fun getItemCount(): Int = foods.size
}