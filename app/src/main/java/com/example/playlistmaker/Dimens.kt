package com.example.playlistmaker
import android.content.Context
import android.util.TypedValue

fun dpToPx(dp: Float, context: Context): Int { // преобразование dpToPx это нужно для бибилотеки Glide, так как она использует Px, в Figma тоже Px, но другие, адаптированные под dp AS
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    ).toInt()
}

fun pxToDp(px: Float, context: Context): Float { // преобразование pxToDp
    return px / context.resources.displayMetrics.density
}