package com.example.photoeditor

import android.graphics.Bitmap
import androidx.fragment.app.FragmentManager

class Masking(private val parameters: MutableMap<String, Int>,
              override val icon_source: Int,
              override val string_source: Int): Filter(parameters, icon_source, string_source) {

     override fun apply(image: Bitmap?): Bitmap? {
         // TODO()
         return null
     }

    override fun showOptions(container: Int, manager: FragmentManager) {
        val newFragment = MaskingInputFragment()

        val transaction = manager.beginTransaction()
        transaction.replace(container, newFragment)
        transaction.commit()
    }
}
