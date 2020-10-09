package com.example.imagesearcher.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.subscribe(lifecycleOwner: LifecycleOwner, call: (T) -> Unit) =
    observe(lifecycleOwner, Observer { call(value!!) })
