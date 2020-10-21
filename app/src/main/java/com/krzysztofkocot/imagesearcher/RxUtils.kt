package com.krzysztofkocot.imagesearcher

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
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
