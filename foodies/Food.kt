package com.example.foodies

import java.io.Serializable

data class Food(
    val Id: Int? = null,
    val Title: String? = null,
    val Description: String? = null,
    val ImagePath: String? = null,
    val Calorie: Int? = null,
    val CategoryId: String? = null,
    val CookingProcess: String? = null,
    val Star: Float? = null,
    val TimeId: Int? = null,
    val TimeValue: Int? = null,
    val VideoLink: String? = null,
    val Servings: Int? = null,
    val Frozen: Boolean? = null,
    val VegOrNonVeg: String? = null,
    val Ingredients: List<String>? = null,
    val BestFood: Boolean? = null,
    val isFavorite: Boolean? = null
) : Serializable