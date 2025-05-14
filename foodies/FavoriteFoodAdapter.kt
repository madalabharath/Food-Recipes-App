package com.example.foodies

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FavoriteFoodAdapter : RecyclerView.Adapter<FavoriteFoodAdapter.FavoriteFoodViewHolder>() {

    private var favoriteFoods: List<FavoriteFood> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: List<FavoriteFood>) {
        favoriteFoods = newList
        Log.d("FavoriteFoodAdapter", "Submitted ${newList.size} items")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteFoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_food, parent, false)
        return FavoriteFoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteFoodViewHolder, position: Int) {
        holder.bind(favoriteFoods[position])
    }

    override fun getItemCount(): Int = favoriteFoods.size

    class FavoriteFoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foodImage: ImageView = itemView.findViewById(R.id.food_image)
        private val foodName: TextView = itemView.findViewById(R.id.food_name)
        private val foodDescription: TextView = itemView.findViewById(R.id.food_description)

        fun bind(food: FavoriteFood) {
            Log.d("FavoriteFoodAdapter", "Binding: ${food.Title}")
            foodName.text = food.Title
            foodDescription.text = food.Description
            Glide.with(itemView.context)
                .load(food.ImagePath)
                .placeholder(R.drawable.food_placeholder)
                .into(foodImage)
        }
    }
}