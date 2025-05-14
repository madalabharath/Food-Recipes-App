package com.example.foodies

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

object FirebaseUtils {

    private val database = FirebaseDatabase.getInstance().getReference("Favorites")


    // Add a food to favorites
    fun addFavorite(food: FavoriteFood, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        database.child("food${food.Id}").setValue(food.copy(Favorites = true))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception.message ?: "Failed to add favorite") }
    }

    // Remove a food from favorites
    fun removeFavorite(foodId: Int, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        database.child("food$foodId").removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception.message ?: "Failed to remove favorite") }
    }

    // Check if a food is a favorite
    fun isFavorite(foodId: Int, callback: (Boolean) -> Unit) {
        database.child("food$foodId").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }
}