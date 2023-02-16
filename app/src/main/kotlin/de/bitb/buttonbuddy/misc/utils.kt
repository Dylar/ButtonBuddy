package de.bitb.buttonbuddy.misc

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeOnce(observer: Observer<T?>) {
    observeForever {
        observer.onChanged(it)
        removeObserver(observer)
    }
}
