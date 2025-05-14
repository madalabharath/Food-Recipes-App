package com.example.foodies

data class FavoriteFood(
    val Id: Int = 0,
    val Title: String = "",
    val Description: String = "",
    val ImagePath: String = "",
    val Favorites: Boolean = true,
    val Calorie: Int = 0,
    val CategoryId: String = "",
    val CookingProcess: String = "",
    val Star: Float = 0f,
    val TimeId: Int = 0,
    val TimeValue: Int = 0,
    val VideoLink: String = "",
    val BestFood: Boolean = false
)