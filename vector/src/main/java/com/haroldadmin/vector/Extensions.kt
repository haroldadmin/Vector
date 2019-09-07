package com.haroldadmin.vector

import androidx.annotation.RestrictTo
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

/**
 * A convenience function to access current state and execute an action on it.
 *
 * @param S The type of the current state
 * @param block The action to be performed using the current state
 *
 * Example:
 *
 * class MyViewModel(): VectorViewModel<MyState>()
 *
 * class MyFragment(): VectorFragment {
 *      onViewCreated(...) {
 *         withState(viewModel) { state ->
 *              if (state.isPremiumUser) {
 *                  premiumFeature.enable()
 *              }
 *         }
 *      }
 * }
 *
 * Note: The state provided to the block is not guaranteed to be the latest state, because there
 * might be other state mutation blocks in the State Store's queue
 *
 * Warning: This WILL cause your app to crash if you create your ViewModels without initial state
 * and fail to provide it later, before calling this function.
 */
inline fun <S : VectorState> withState(
    viewModel: VectorViewModel<S>,
    crossinline block: (S) -> Unit
) {
    block(viewModel.currentState)
}

/**
 * A convenience function to update the value present in the Receiving [ConflatedBroadcastChannel]
 *
 * Takes in a function to calculate a new value based on the current value in the channel,
 * and then sets the new value to the channel.
 */
internal inline fun <T> ConflatedBroadcastChannel<T>.compute(crossinline newValueProvider: (T) -> T): Boolean {

    if (this.isClosedForSend) return false

    val newValue = newValueProvider.invoke(this.value)
    this.offer(newValue)
    return true
}

/**
 * Tries to find the companion object of a class that implements [VectorViewModelFactory] and
 * returns it. If no such companion object is found, it throws [DoesNotImplementVectorVMFactoryException]
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal fun Class<*>.factoryCompanion(): Class<*> {
    return companionObject()?.let { clazz ->
        if (VectorViewModelFactory::class.java.isAssignableFrom(clazz)) {
            clazz
        } else {
            null
        }
    } ?: throw DoesNotImplementVectorVMFactoryException()
}

/**
 * Tries to find the companion object of the given class, and returns it. If the class does not
 * have a companion object, returns null
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal fun Class<*>.companionObject(): Class<*>? {
    return try {
        Class.forName("$name\$Companion")
    } catch (ex: ClassNotFoundException) {
        null
    }
}

/**
 * Creates a new instance of the given class using the constructor having one parameter only.
 * If no such constructor exists, returns null.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal fun Class<*>.instance(initArg: Any? = null): Any? {
    return declaredConstructors.firstOrNull { it.parameterTypes.size == 1 }?.newInstance(initArg)
}
