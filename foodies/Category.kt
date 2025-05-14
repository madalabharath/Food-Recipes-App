package com.example.foodies

import java.io.Serializable

data class Category(
    val Id: Int = 0,
    val ImagePath: String = "",
    val Name: String = ""
) : Serializable