package com.example.marsproject

import android.graphics.drawable.Drawable

class EquipmentItem(
    val name: String,
    private val image: Drawable?,
    val appearance: String
) {
    fun getImage(): Drawable? {
        return image
    }
}
