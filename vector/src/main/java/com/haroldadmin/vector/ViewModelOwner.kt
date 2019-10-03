package com.haroldadmin.vector

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * [ViewModelOwner] wraps the owner of a [VectorViewModel].
 *
 * Use it to get access to your object graph, arguments and saved instance state during the
 * creation of the ViewModel.
 *
 * **DO NOT STORE A REFERENCE TO THIS IN YOUR VIEWMODEL**
 */
sealed class ViewModelOwner

/**
 * A [ViewModelOwner] wrapping an Activity
 *
 * @property activity The underlying activity of this [ViewModelOwner]
 * @constructor Creates a [ViewModelOwner] wrapping the given Activity
 *
 */
class ActivityViewModelOwner(
    val activity: ComponentActivity
) : ViewModelOwner() {

    /**
     * Get a type-casted version of the wrapped activity
     *
     * @param A The type of your Activity
     * @throws ClassCastException If the wrapped activity can not be casted to given activity type
     *
     */
    @Suppress("UNCHECKED_CAST")
    fun <A : FragmentActivity> activity(): A = activity as A
}

/**
 * A [ViewModelOwner] which wraps the parent fragment of a ViewModel
 *
 * @property fragment The parent fragment wrapped in this class
 * @property args The supplied arguments to be used for a ViewModel creation
 * @constructor Creates a [ViewModelOwner] wrapping the given Fragment
 *
 */
class FragmentViewModelOwner(
    val fragment: Fragment,
    private val args: Bundle? = fragment.arguments
) : ViewModelOwner() {

    /**
     * Get a type-casted version of the parent activity of the wrapped fragment
     *
     * @param A The type of the parent activity
     *
     */
    @Suppress("UNCHECKED_CAST")
    fun <A : FragmentActivity> activity(): A = fragment.activity as A

    /**
     * Get a type-casted version of the parent fragment wrapped in this class
     *
     * @param F The type of the parent activity
     *
     */
    @Suppress("UNCHECKED_CAST")
    fun <F : Fragment> fragment(): F = fragment as F

    /**
     * Get the arguments bundle wrapped in this class
     */
    fun args(): Bundle? = args
}

/**
 * Get access to [Context] from a [ViewModelOwner].
 *
 * When this is an [ActivityViewModelOwner], it returns the wrapped activity itself.
 * When this is a [FragmentViewModelOwner], returns the context from the wrapped fragment.
 */
fun ViewModelOwner.context(): Context {
    return when (this) {
        is ActivityViewModelOwner -> activity
        is FragmentViewModelOwner -> fragment.requireContext()
    }
}

/**
 * Creates a [ViewModelOwner] from this Fragment
 */
fun Fragment.fragmentViewModelOwner(): FragmentViewModelOwner {
    return FragmentViewModelOwner(this)
}

/**
 * Creates a [ViewModelOwner] from the parent activity of this Fragment
 */
fun Fragment.activityViewModelOwner(): ActivityViewModelOwner {
    return ActivityViewModelOwner(requireActivity())
}

/**
 * Creates a [ViewModelOwner] from this activity
 */
fun ComponentActivity.activityViewModelOwner(): ActivityViewModelOwner {
    return ActivityViewModelOwner(this)
}
