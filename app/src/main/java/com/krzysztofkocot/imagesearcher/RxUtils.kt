package com.krzysztofkocot.imagesearcher

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.jakewharton.rxrelay3.BehaviorRelay
import com.jakewharton.rxrelay3.Relay
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

fun <T> LiveData<T>.subscribe(lifecycleOwner: LifecycleOwner, call: (T) -> Unit) =
    observe(lifecycleOwner, Observer { call(value!!) })

fun <T> Observable<T>.subscribeAndroid(call: (T) -> Unit): Disposable =
    observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            {
                call(it)
            },
            {
                Log.e("KK", it.message.toString())
            }
        )

infix fun <T> Relay<T>.put(value: T) { accept(value) }

data class Optional<out T>(val value: T?)

val <T> T?.optional get() = Optional(this)
