package com.example.marsproject

import android.graphics.drawable.Drawable

class EquipmentItem(val name: String, private val image: Drawable) {
//    fun getName(): String? {
//        return name
//    }

    fun getImage(): Drawable? {
        return image
    }
}
