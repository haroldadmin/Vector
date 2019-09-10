package com.haroldadmin.vector

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * [ViewModelOwner] wraps the owner of a [VectorViewModel].
 *
 * Use it to get access to your object graph, arguments and saved instance state during the
 * creation of the ViewModel.
 *
 * DO NOT STORE A REFERENCE TO THIS IN YOUR VIEWMODEL
 */
sealed class ViewModelOwner

class ActivityViewModelOwner(
    val activity: FragmentActivity
) : ViewModelOwner() {

    @Suppress("UNCHECKED_CAST")
    fun <A : FragmentActivity> activity(): A = activity as A
}

class FragmentViewModelOwner(
    val fragment: Fragment,
    private val args: Bundle?
) : ViewModelOwner() {

    @Suppress("UNCHECKED_CAST")
    fun <A : FragmentActivity> activity(): A = fragment.activity as A

    @Suppress("UNCHECKED_CAST")
    fun <F : Fragment> fragment(): F = fragment as F

    @Suppress("UNCHECKED_CAST")
    fun args(): Bundle? = args
}

fun ViewModelOwner.context(): Context {
    return when (this) {
        is ActivityViewModelOwner -> activity
        is FragmentViewModelOwner -> fragment.requireContext()
    }
}

fun Fragment.fragmentViewModelOwner(): FragmentViewModelOwner {
    return FragmentViewModelOwner(this, arguments)
}

fun Fragment.activityViewModelOwner(): ActivityViewModelOwner {
    return ActivityViewModelOwner(requireActivity())
}

fun AppCompatActivity.activityViewModelOwner(): ActivityViewModelOwner {
    return ActivityViewModelOwner(this)
}
