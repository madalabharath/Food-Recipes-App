package com.example.foodies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodies.databinding.ItemBestFoodBinding
import java.io.Serializable

class BestFoodAdapter(
    private val foods: List<BestFood>
) : RecyclerView.Adapter<BestFoodAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemBestFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(food: BestFood) {
            binding.foodName.text = food.Title ?: "Unknown"
            binding.timeText.text = food.TimeValue?.toString()?: "N/A"
            binding.ratingText.text = food.Star?.toString() ?: "N/A"
            Glide.with(binding.foodImage.context)
                .load(food.ImagePath)
                .placeholder(R.drawable.food_placeholder)
                .error(R.drawable.food_placeholder)
                .into(binding.foodImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBestFoodBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(foods[position])
    }

    override fun getItemCount(): Int = foods.size
}