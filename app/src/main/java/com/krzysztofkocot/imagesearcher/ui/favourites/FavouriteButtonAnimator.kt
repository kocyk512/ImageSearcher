package com.krzysztofkocot.imagesearcher.ui.favourites

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator

fun View.jumpAnimate() = animate()
    .scaleX(JUMP_SCALE)
    .scaleY(JUMP_SCALE)
    .setDuration(JUMP_DURATION)
    .setInterpolator(AccelerateDecelerateInterpolator())
    .setListener(object : AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}
        override fun onAnimationEnd(animation: Animator?) {
            this@jumpAnimate.animate()
                .scaleX(GO_BACK_SCALE)
                .scaleY(GO_BACK_SCALE)
                .setInterpolator(OvershootInterpolator(GO_BACK_TENSION))
                .setDuration(GO_BACK_DURATION)
                .start()
        }
        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationStart(animation: Animator?) {}
    })
    .start()

private const val JUMP_SCALE = 1.2f
private const val GO_BACK_SCALE = 1.0f
const val JUMP_DURATION = 50L
const val GO_BACK_DURATION = 200L
private const val GO_BACK_TENSION = 5f
