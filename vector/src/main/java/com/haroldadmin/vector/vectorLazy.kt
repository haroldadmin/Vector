package com.haroldadmin.vector

import androidx.annotation.RestrictTo

/**
 * A lazy delegate class, which takes in an [initializer] to initialize its underlying
 * property. Implements the double-checked locking algorithm.
 */
@Suppress("ClassName")
@RestrictTo(RestrictTo.Scope.LIBRARY)
class vectorLazy<out T>(private val initializer: () -> T) : Lazy<T> {

    @Volatile
    private var _value: T? = null

    private val lock = this

    override val value: T
        @Suppress("UNCHECKED_CAST")
        get() {
            if (_value != null) {
                return _value as T
            } else {
                synchronized(lock) {
                    return if (_value != null) {
                        _value as T
                    } else {
                        val initializedValue = initializer()
                        _value = initializedValue
                        initializedValue
                    }
                }
            }
        }

    override fun isInitialized(): Boolean = _value != null
}
