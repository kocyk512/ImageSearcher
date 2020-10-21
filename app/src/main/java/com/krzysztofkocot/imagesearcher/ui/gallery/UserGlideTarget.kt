package com.krzysztofkocot.imagesearcher.ui.gallery

import android.graphics.drawable.Drawable
import android.widget.TextView
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.krzysztofkocot.imagesearcher.ui.utils.dp

class UserGlideTarget(
    private val textViewUserName: TextView
) : CustomTarget<Drawable>(
    dp(25, textViewUserName),
    dp(25, textViewUserName)
) {
    override fun onLoadCleared(placeholder: Drawable?) {
        textViewUserName.setCompoundDrawablesWithIntrinsicBounds(placeholder, null, null, null)
    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        textViewUserName.setCompoundDrawablesWithIntrinsicBounds(resource, null, null, null)
    }
}