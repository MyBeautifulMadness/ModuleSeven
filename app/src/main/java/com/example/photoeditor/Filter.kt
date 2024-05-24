package com.example.photoeditor

import android.graphics.Bitmap

open class Filter(private val parameters: MutableMap<String, Int>, open val icon_source: Int, open val string_source: Int) {
    open fun apply(image: Bitmap?): Bitmap? { return null }

    open fun showOptions() {}

    fun setParameter(name: String, value: Int) {
        this.parameters[name] = value
    }
}
