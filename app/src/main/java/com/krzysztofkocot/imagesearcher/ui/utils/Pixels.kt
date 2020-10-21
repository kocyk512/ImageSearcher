package com.krzysztofkocot.imagesearcher.ui.utils

import android.content.Context

class Pixels {
    companion object {
        fun dp(pixels: Int, ctx: Context) = (pixels * ctx.resources.displayMetrics.density).toInt()
    }
}