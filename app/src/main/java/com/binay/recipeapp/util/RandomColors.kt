package com.binay.recipeapp.util

import android.content.Context
import android.graphics.BitmapFactory
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import com.binay.recipeapp.R
import kotlin.random.Random


object RandomColors {
    fun generateRandomColor(context: Context, drawableId: Int): Int {
        // Generate a palette of colors based on the specified color
        val bitmap = BitmapFactory.decodeResource(context.resources, drawableId)
        val palette = Palette.from(bitmap).generate()
        val swatches = palette.swatches
        return if (swatches.isEmpty()) {
            // Fallback to use the original color if no palette swatches are available
            R.color.secondary_color
        } else {
            // Adjust the colors to make them less bright
            val hsl = FloatArray(3)
            ColorUtils.colorToHSL(context.getColor(R.color.secondary_color), hsl)

            // Reduce the brightness (lightness) and increase the saturation
            hsl[2] = hsl[2] * 2.5f // reduce brightness by 50%
            hsl[1] = hsl[1] * 2.5f // increase saturation by 50%

            // Convert the adjusted color back to RGB
            val adjustedColor = ColorUtils.HSLToColor(hsl)

            // Return a random palette swatch color
            swatches[Random.nextInt(swatches.size)].rgb
        }
    }
}